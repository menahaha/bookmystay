/**
 * Abstract class representing the generalized concept of a Room.
 */
abstract class Room {
    private String roomType;
    private double pricePerNight;

    public Room(String roomType, double pricePerNight) {
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
    }

    // Encapsulation: Providing read access to private attributes
    public String getRoomType() { return roomType; }
    public double getPricePerNight() { return pricePerNight; }

    /**
     * Abstract method to be implemented by specific room types
     * to describe their unique features.
     */
    public abstract void displayFeatures();
}

// Concrete implementation for a Single Room
class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 100.0); }
    @Override
    public void displayFeatures() {
        System.out.println("Features: 1 Twin Bed, High-speed Wi-Fi, Work Desk.");
    }
}

// Concrete implementation for a Double Room
class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 180.0); }
    @Override
    public void displayFeatures() {
        System.out.println("Features: 1 Queen Bed, Mini-bar, Sea View.");
    }
}

// Concrete implementation for a Suite
class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 350.0); }
    @Override
    public void displayFeatures() {
        System.out.println("Features: King Bed, Separate Living Area, Jacuzzi.");
    }
}