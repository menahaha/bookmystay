import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

// --- 1. DOMAIN MODEL (Unchanged from UC3) ---
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

// --- 2. STATE HOLDER (The "Source of Truth") ---
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) { inventory.put(type, count); }

    // Read-only access for the Search Service
    public int getCount(String type) { return inventory.getOrDefault(type, 0); }

    // Mutation access (Used only for actual bookings)
    public void update(String type, int delta) {
        inventory.put(type, getCount(type) + delta);
    }
}

// --- 3. SEARCH SERVICE (The new Read-Only Layer) ---
class SearchService {
    /**
     * Filters and displays only available rooms.
     * This method does NOT modify the inventory.
     */
    public void searchAvailableRooms(List<Room> catalog, RoomInventory inventory) {
        System.out.println("\n--- Available Rooms Search Results ---");
        boolean found = false;

        for (Room room : catalog) {
            int availableCount = inventory.getCount(room.getRoomType());

            // Validation Logic: Only show rooms with count > 0
            if (availableCount > 0) {
                System.out.println("[" + room.getRoomType() + "] - Price: $" + room.getPricePerNight());
                System.out.print("   ");
                room.displayFeatures();
                System.out.println(" | Left: " + availableCount);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Sorry, no rooms are currently available.");
        }
    }
}

// --- 4. MAIN APPLICATION FILE ---
public class bookmystay {
    public static void main(String[] args) {
        // Setup System
        RoomInventory inventory = new RoomInventory();
        SearchService searchService = new SearchService();

        // Define our Catalog
        List<Room> catalog = new ArrayList<>();
        catalog.add(new SingleRoom());
        catalog.add(new DoubleRoom());

        // Initial Stock: 2 Singles, 0 Doubles (to test filtering)
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 0);

        // Perform Search
        System.out.println("User is searching for rooms...");
        searchService.searchAvailableRooms(catalog, inventory);

        System.out.println("\nSearch complete. No system state was changed.");
    }
}