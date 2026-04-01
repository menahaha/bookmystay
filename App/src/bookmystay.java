import java.io.*;
import java.util.*;

// ==========================================
// MODELS (Must implement Serializable)
// ==========================================
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L; // Ensures version compatibility
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    @Override
    public String toString() { return "Guest: " + guestName + " | Room: " + roomType; }
}

// ==========================================
// PERSISTENT STATE HOLDER
// ==========================================
class AppState implements Serializable {
    private static final long serialVersionUID = 1L;
    public Map<String, Integer> inventory;
    public List<Reservation> history;

    public AppState(Map<String, Integer> inventory, List<Reservation> history) {
        this.inventory = inventory;
        this.history = history;
    }
}

// ==========================================
// USE CASE 12: PERSISTENCE SERVICE
// ==========================================
class PersistenceService {
    private static final String FILE_NAME = "hotel_data.ser";

    /**
     * Serializes the current state to a file.
     */
    public void saveState(Map<String, Integer> inventory, List<Reservation> history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            AppState state = new AppState(inventory, history);
            oos.writeObject(state);
            System.out.println("[System] State successfully persisted to " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("[Error] Failed to save state: " + e.getMessage());
        }
    }

    /**
     * Deserializes the state from a file.
     */
    public AppState loadState() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("[System] No previous state found. Starting fresh.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (AppState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Error] Recovery failed: " + e.getMessage());
            return null;
        }
    }
}

// ==========================================
// MAIN APPLICATION ENTRY POINT
// ==========================================
public class BookMyStayApp {
    public static void main(String[] args) {
        PersistenceService persistence = new PersistenceService();

        // 1. RECOVERY PHASE
        AppState savedState = persistence.loadState();

        Map<String, Integer> inventory;
        List<Reservation> history;

        if (savedState != null) {
            inventory = savedState.inventory;
            history = savedState.history;
            System.out.println("RECOVERY SUCCESSFUL. Restored " + history.size() + " bookings.");
        } else {
            inventory = new HashMap<>();
            history = new ArrayList<>();
            inventory.put("Suite Room", 5);
            System.out.println("INITIALIZED NEW SYSTEM STATE.");
        }

        // 2. SIMULATE ACTIVITY
        System.out.println("\n--- Current Inventory: " + inventory + " ---");

        if (inventory.get("Suite Room") > 0) {
            String guest = "Guest_" + (history.size() + 1);
            System.out.println("[Action] Booking for " + guest);
            inventory.put("Suite Room", inventory.get("Suite Room") - 1);
            history.add(new Reservation(guest, "Suite Room"));
        }

        // 3. SHUTDOWN & PERSISTENCE PHASE
        System.out.println("\nShutting down system...");
        persistence.saveState(inventory, history);

        System.out.println("Final History Log:");
        history.forEach(System.out::println);
        System.out.println("=== Session Ended ===");
    }
}