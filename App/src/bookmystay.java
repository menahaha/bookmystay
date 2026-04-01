import java.util.*;

// ==========================================
// USE CASE 9: CUSTOM EXCEPTION
// ==========================================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// ==========================================
// USE CASE 2: DOMAIN MODELS
// ==========================================
abstract class Room {
    private String roomType;
    private double pricePerNight;

    public Room(String roomType, double pricePerNight) {
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
    }

    public String getRoomType() { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 100.0); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 180.0); }
}

// ==========================================
// USE CASE 5: RESERVATION MODEL
// ==========================================
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
        return "Guest: " + guestName + " | Room: " + roomType;
    }
}

// ==========================================
// USE CASE 3: INVENTORY MANAGEMENT
// ==========================================
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) { inventory.put(type, count); }

    // Returns -1 if the room type doesn't exist at all
    public int getCount(String type) {
        return inventory.containsKey(type) ? inventory.get(type) : -1;
    }

    public void update(String type, int delta) {
        inventory.put(type, inventory.get(type) + delta);
    }
}

// ==========================================
// USE CASE 9: VALIDATION LOGIC (Fail-Fast)
// ==========================================
class BookingValidator {
    public void validate(String roomType, RoomInventory inventory) throws InvalidBookingException {
        int count = inventory.getCount(roomType);

        if (count == -1) {
            throw new InvalidBookingException("Room type '" + roomType + "' is not recognized by the system.");
        }
        if (count <= 0) {
            throw new InvalidBookingException("Room type '" + roomType + "' is currently sold out.");
        }
    }
}

// ==========================================
// USE CASE 8: HISTORY & REPORTING
// ==========================================
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
        // 1. Initialize Components
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingValidator validator = new BookingValidator();

        // 2. Setup Initial State
        inventory.addRoomType("Single Room", 1); // Only one left!

        System.out.println("=== Book My Stay v9.0: Validation & Reliability ===");

        // 3. Scenario: Attempting to book a non-existent room
        processBooking("Alice", "Penthouse", validator, inventory, history);

        // 4. Scenario: Successful booking
        processBooking("Bob", "Single Room", validator, inventory, history);

        // 5. Scenario: Attempting to book the same room (Now Sold Out)
        processBooking("Charlie", "Single Room", validator, inventory, history);
    }

    /**
     * Helper method to encapsulate the Try-Catch logic.
     */
    public static void processBooking(String name, String room, BookingValidator v, RoomInventory i, BookingHistory h) {
        try {
            System.out.println("\n[Request] " + name + " wants a " + room);

            // Validate first (Throws Exception if invalid)
            v.validate(room, i);

            // If we reach here, validation passed
            i.update(room, -1);
            h.record(new Reservation(name, room));
            System.out.println("RESULT: Booking confirmed for " + name);

        } catch (InvalidBookingException e) {
            // Handle error gracefully
            System.out.println("RESULT: FAILED - " + e.getMessage());
        }
    }
}