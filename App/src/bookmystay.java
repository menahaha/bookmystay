import java.util.HashMap;
import java.util.Map;

// --- DOMAIN MODEL (What a Room is) ---

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
    @Override
    public void displayFeatures() {
        System.out.println("-> Features: 1 Twin Bed, Wi-Fi, Work Desk.");
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 180.0); }
    @Override
    public void displayFeatures() {
        System.out.println("-> Features: 1 Queen Bed, Mini-bar, Sea View.");
    }
}

// --- STATE MANAGEMENT (How many Rooms we have) ---

class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        this.inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public boolean updateAvailability(String roomType, int change) {
        int currentCount = getAvailability(roomType);
        if (currentCount + change < 0) return false;
        inventory.put(roomType, currentCount + change);
        return true;
    }

    public void displayInventory() {
        System.out.println("\n--- Current Room Inventory ---");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": [" + entry.getValue() + " left]");
        }
    }
}

// --- APPLICATION ENTRY POINT (The Manager) ---

public class bookmystay {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay - System v3.0");
        System.out.println("========================================");

        // 1. Setup Inventory
        RoomInventory inventoryManager = new RoomInventory();
        inventoryManager.addRoomType("Single Room", 8);
        inventoryManager.addRoomType("Double Room", 4);

        // 2. Define Room Objects
        Room sRoom = new SingleRoom();
        Room dRoom = new DoubleRoom();

        // 3. Show Initial State
        inventoryManager.displayInventory();

        // 4. Test a Transaction
        System.out.println("\n[Action] Processing booking for 1 Double Room...");
        if (inventoryManager.updateAvailability(dRoom.getRoomType(), -1)) {
            System.out.println("SUCCESS: Room secured.");
        } else {
            System.out.println("FAILED: Room sold out.");
        }

        // 5. Final State
        inventoryManager.displayInventory();
        System.out.println("\nSystem operation complete.");
    }
}