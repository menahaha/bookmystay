import java.util.LinkedList;
import java.util.Queue;

// --- 1. RESERVATION MODEL (The Guest's Intent) ---
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return "Reservation [Guest: " + guestName + ", Room: " + roomType + "]";
    }
}

// --- 2. BOOKING QUEUE (The Waitlist) ---
class BookingQueue {
    // We use LinkedList because it implements the Queue interface in Java
    private Queue<Reservation> requestQueue;

    public BookingQueue() {
        this.requestQueue = new LinkedList<>();
    }

    /**
     * Adds a new booking request to the end of the line.
     */
    public void addRequest(Reservation res) {
        requestQueue.add(res);
        System.out.println("Enqueued: " + res.getGuestName() + " is waiting for a " + res.getRoomType());
    }

    /**
     * Shows all currently waiting requests without removing them.
     */
    public void displayQueue() {
        System.out.println("\n--- Current Booking Request Queue (FIFO) ---");
        if (requestQueue.isEmpty()) {
            System.out.println("The queue is currently empty.");
        } else {
            for (Reservation res : requestQueue) {
                System.out.println(" >> " + res);
            }
        }
    }

    // Accessor for the next phase (Processing)
    public Queue<Reservation> getRequestQueue() {
        return requestQueue;
    }
}

// --- 3. MAIN APPLICATION ---
public class bookmystay {
    public static void main(String[] args) {
        System.out.println("Book My Stay v5.0 - Fair Request Handling");

        // Initialize our Queue
        BookingQueue bookingOffice = new BookingQueue();

        // Simulate incoming requests in order
        bookingOffice.addRequest(new Reservation("Alice", "Suite Room"));
        bookingOffice.addRequest(new Reservation("Bob", "Single Room"));
        bookingOffice.addRequest(new Reservation("Charlie", "Suite Room"));

        // Display the queue to prove order is preserved
        bookingOffice.displayQueue();

        System.out.println("\nTotal requests waiting: " + bookingOffice.getRequestQueue().size());
        System.out.println("Note: No rooms have been assigned yet. Inventory remains unchanged.");
    }
}