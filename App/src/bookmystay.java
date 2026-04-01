import java.util.*;

// ==========================================
// USE CASE 9: CUSTOM EXCEPTION
// ==========================================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

// ==========================================
// USE CASE 5: RESERVATION MODEL
// ==========================================
class Reservation {
    private String guestName;
    private String roomType;
    private boolean isCancelled = false;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        String status = isCancelled ? "[CANCELLED]" : "[CONFIRMED]";
        return status + " Guest: " + guestName + " | Room: " + roomType;
    }
}

// ==========================================
// USE CASE 11: THREAD-SAFE INVENTORY
// ==========================================
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();
    // Stack to track recently released room types for rollback audit
    private Stack<String> rollbackStack = new Stack<>();

    public void addRoomType(String type, int count) { inventory.put(type, count); }

    /**
     * Synchronized method ensures only ONE thread can check and
     * update inventory at any given time (Critical Section).
     */
    public synchronized boolean tryBookRoom(String type) {
        int count = inventory.getOrDefault(type, 0);
        if (count > 0) {
            // Simulate processing time to increase chance of race condition
            try { Thread.sleep(10); } catch (InterruptedException e) {}

            inventory.put(type, count - 1);
            return true;
        }
        return false;
    }

    public int getCount(String type) { return inventory.getOrDefault(type, 0); }
}

// ==========================================
// USE CASE 11: CONCURRENT PROCESSOR (Thread Logic)
// ==========================================
class BookingTask implements Runnable {
    private String guestName;
    private String roomType;
    private RoomInventory inventory;
    private List<Reservation> history;

    public BookingTask(String name, String type, RoomInventory inv, List<Reservation> hist) {
        this.guestName = name;
        this.roomType = type;
        this.inventory = inv;
        this.history = hist;
    }

    @Override
    public void run() {
        // Attempting to book in a multi-threaded environment
        if (inventory.tryBookRoom(roomType)) {
            Reservation res = new Reservation(guestName, roomType);
            synchronized(history) { // History list also needs protection!
                history.add(res);
            }
            System.out.println("[SUCCESS] " + guestName + " secured a " + roomType);
        } else {
            System.out.println("[FAILED] " + guestName + " - " + roomType + " is sold out.");
        }
    }
}

// ==========================================
// MAIN APPLICATION ENTRY POINT
// ==========================================
public class bookmystay {
    public static void main(String[] args) throws InterruptedException {
        // 1. Setup Shared Resources
        RoomInventory sharedInventory = new RoomInventory();
        List<Reservation> sharedHistory = Collections.synchronizedList(new ArrayList<>());

        // We only have 2 Double Rooms available
        sharedInventory.addRoomType("Double Room", 2);

        System.out.println("=== Book My Stay v11.0: Concurrent Simulation ===");
        System.out.println("Initial Inventory: 2 Double Rooms\n");

        // 2. Simulate 5 Guests trying to book those 2 rooms at once
        Thread t1 = new Thread(new BookingTask("Alice", "Double Room", sharedInventory, sharedHistory));
        Thread t2 = new Thread(new BookingTask("Bob", "Double Room", sharedInventory, sharedHistory));
        Thread t3 = new Thread(new BookingTask("Charlie", "Double Room", sharedInventory, sharedHistory));
        Thread t4 = new Thread(new BookingTask("Dave", "Double Room", sharedInventory, sharedHistory));
        Thread t5 = new Thread(new BookingTask("Eve", "Double Room", sharedInventory, sharedHistory));

        // Start all threads simultaneously
        t1.start(); t2.start(); t3.start(); t4.start(); t5.start();

        // Wait for all threads to finish
        t1.join(); t2.join(); t3.join(); t4.join(); t5.join();

        // 3. Final Verification
        System.out.println("\n--- Final System State ---");
        System.out.println("Remaining Double Rooms: " + sharedInventory.getCount("Double Room"));
        System.out.println("Total Confirmed Bookings: " + sharedHistory.size());

        if (sharedHistory.size() > 2) {
            System.out.println("ERROR: Double Booking detected!");
        } else {
            System.out.println("SUCCESS: Thread safety maintained. No double bookings.");
        }

        // 4. Verify System State
        System.out.println("After Cancellation - Double Rooms: " + inventory.getCount("Double Room"));
        inventory.showRollbackLog();

        System.out.println("\nFinal Audit Log:");
        history.getHistoryLog().forEach(System.out::println);
    }
}