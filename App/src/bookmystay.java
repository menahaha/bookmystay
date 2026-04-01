import java.util.*;

// ==========================================
// USE CASE 2: DOMAIN MODELS (Room Types)
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

    public abstract void displayFeatures();
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 100.0); }
    @Override public void displayFeatures() { System.out.print("1 Twin Bed, Wi-Fi"); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 180.0); }
    @Override public void displayFeatures() { System.out.print("1 Queen Bed, Sea View"); }
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
// USE CASE 3: INVENTORY MANAGEMENT (HashMap)
// ==========================================
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) { inventory.put(type, count); }
    public int getCount(String type) { return inventory.getOrDefault(type, 0); }

    public boolean update(String type, int delta) {
        int current = getCount(type);
        if (current + delta < 0) return false;
        inventory.put(type, current + delta);
        return true;
    }
}

// ==========================================
// USE CASE 8: HISTORY & REPORTING
// ==========================================
class BookingHistory {
    private List<Reservation> historyLog = new ArrayList<>();

    public void record(Reservation res) {
        historyLog.add(res);
    }

    public List<Reservation> getHistoryLog() {
        return historyLog;
    }
}

class ReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("\n--- FINAL BOOKING REPORT ---");
        List<Reservation> logs = history.getHistoryLog();
        if (logs.isEmpty()) {
            System.out.println("No records found.");
        } else {
            for (int i = 0; i < logs.size(); i++) {
                System.out.println((i + 1) + ". " + logs.get(i));
            }
        }
        System.out.println("----------------------------");
    }
}

// ==========================================
// MAIN APPLICATION ENTRY POINT
// ==========================================
public class bookmystay {
    public static void main(String[] args) {
        // 1. Initialize System Components
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        ReportService reportService = new ReportService();

        // 2. Setup Initial Data (UC3)
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 2);

        System.out.println("System Initialized: Book My Stay v8.0");

        // 3. Simulate a Booking Flow (UC5 -> UC8)
        Reservation res1 = new Reservation("Alice", "Single Room");

        // Logic check: If room is available, update inventory and record history
        if (inventory.update(res1.getRoomType(), -1)) {
            System.out.println("Booking Success for " + res1.getGuestName());
            history.record(res1);
        } else {
            System.out.println("Booking Failed for " + res1.getGuestName());
        }

        // 4. Generate the Audit Report (UC8)
        reportService.generateReport(history);
    }
}