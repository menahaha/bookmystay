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

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public boolean isCancelled() { return isCancelled; }
    public void setCancelled(boolean cancelled) { isCancelled = cancelled; }

    @Override
    public String toString() {
        String status = isCancelled ? "[CANCELLED]" : "[CONFIRMED]";
        return status + " Guest: " + guestName + " | Room: " + roomType;
    }
}

// ==========================================
// USE CASE 3 & 10: INVENTORY MANAGEMENT
// ==========================================
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();
    // Stack to track recently released room types for rollback audit
    private Stack<String> rollbackStack = new Stack<>();

    public void addRoomType(String type, int count) { inventory.put(type, count); }

    public int getCount(String type) {
        return inventory.containsKey(type) ? inventory.get(type) : -1;
    }

    public void update(String type, int delta) {
        inventory.put(type, inventory.get(type) + delta);
        // If we are adding back (delta > 0), track it in rollback
        if (delta > 0) { rollbackStack.push(type); }
    }

    public void showRollbackLog() {
        System.out.println("Current Rollback Stack (Recent cancellations first): " + rollbackStack);
    }
}

// ==========================================
// USE CASE 10: CANCELLATION SERVICE (LIFO Rollback)
// ==========================================
class CancellationService {
    /**
     * Reverses a booking, updates inventory, and logs the rollback.
     */
    public void cancelBooking(Reservation res, RoomInventory inventory) throws InvalidBookingException {
        if (res == null || res.isCancelled()) {
            throw new InvalidBookingException("Cancellation failed: Reservation is invalid or already cancelled.");
        }

        System.out.println("[Cancel] Processing rollback for: " + res.getGuestName());

        // 1. Mark the object state
        res.setCancelled(true);

        // 2. Rollback the Inventory (+1)
        inventory.update(res.getRoomType(), 1);

        System.out.println("SUCCESS: Inventory restored for " + res.getRoomType());
    }
}

// ==========================================
// PREVIOUS SERVICES (Validator & History)
// ==========================================
class BookingValidator {
    public void validate(String roomType, RoomInventory inventory) throws InvalidBookingException {
        int count = inventory.getCount(roomType);
        if (count == -1) throw new InvalidBookingException("Unknown room type: " + roomType);
        if (count <= 0) throw new InvalidBookingException("Room " + roomType + " is sold out.");
    }
}

class BookingHistory {
    private List<Reservation> historyLog = new ArrayList<>();
    public void record(Reservation res) { historyLog.add(res); }
    public List<Reservation> getHistoryLog() { return historyLog; }
}

// ==========================================
// MAIN APPLICATION ENTRY POINT
// ==========================================
public class bookmystay {
    public static void main(String[] args) {
        // 1. Init
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingValidator validator = new BookingValidator();
        CancellationService cancellationService = new CancellationService();

        inventory.addRoomType("Double Room", 2);
        System.out.println("=== Book My Stay v10.0: Inventory Rollback ===");

        // 2. Perform a Booking
        Reservation myBooking = new Reservation("Alice", "Double Room");
        try {
            validator.validate("Double Room", inventory);
            inventory.update("Double Room", -1);
            history.record(myBooking);
            System.out.println("Confirmed: " + myBooking);
        } catch (Exception e) { System.out.println(e.getMessage()); }

        System.out.println("Current Double Rooms: " + inventory.getCount("Double Room"));

        // 3. Perform a Cancellation (Rollback)
        try {
            cancellationService.cancelBooking(myBooking, inventory);
        } catch (InvalidBookingException e) {
            System.out.println(e.getMessage());
        }

        // 4. Verify System State
        System.out.println("After Cancellation - Double Rooms: " + inventory.getCount("Double Room"));
        inventory.showRollbackLog();

        System.out.println("\nFinal Audit Log:");
        history.getHistoryLog().forEach(System.out::println);
    }
}