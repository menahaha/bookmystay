import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// --- 1. ADD-ON SERVICE MODEL ---
class AddOnService {
    private String serviceName;
    private double price;

    public AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() { return serviceName; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return serviceName + " ($" + price + ")";
    }
}

// --- 2. SERVICE MANAGER (The "Attachment" Logic) ---
class ServiceManager {
    // Mapping Reservation ID -> List of Services
    private Map<String, List<AddOnService>> reservationAddOns;

    public ServiceManager() {
        this.reservationAddOns = new HashMap<>();
    }

    /**
     * Attaches a service to a specific reservation ID.
     */
    public void addServiceToReservation(String reservationId, AddOnService service) {
        // If the ID isn't in the map, create a new list for it
        reservationAddOns.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
        System.out.println("[Service] Added " + service.getServiceName() + " to ID: " + reservationId);
    }

    /**
     * Calculates the total cost of all add-ons for a specific reservation.
     */
    public double calculateTotalServiceCost(String reservationId) {
        List<AddOnService> services = reservationAddOns.get(reservationId);
        if (services == null) return 0.0;

        double total = 0;
        for (AddOnService s : services) {
            total += s.getPrice();
        }
        return total;
    }

    public void displayServicesForReservation(String reservationId) {
        List<AddOnService> services = reservationAddOns.get(reservationId);
        System.out.println("\n--- Add-on Services for " + reservationId + " ---");
        if (services == null || services.isEmpty()) {
            System.out.println("No extra services selected.");
        } else {
            services.forEach(s -> System.out.println(" + " + s));
            System.out.println("Total Extra Cost: $" + calculateTotalServiceCost(reservationId));
        }
    }
}

// --- 3. UPDATED MAIN APPLICATION ---
public class bookmystay {
    public static void main(String[] args) {
        System.out.println("Book My Stay v7.0 - Optional Services & Extensibility");

        // Assume a reservation was confirmed in the previous step
        String resId = "RES101";

        ServiceManager serviceManager = new ServiceManager();

        // 1. Define available services
        AddOnService breakfast = new AddOnService("Buffet Breakfast", 25.0);
        AddOnService wifi = new AddOnService("Premium Wi-Fi", 10.0);
        AddOnService spa = new AddOnService("Spa Treatment", 120.0);

        // 2. Guest selects services
        serviceManager.addServiceToReservation(resId, breakfast);
        serviceManager.addServiceToReservation(resId, spa);

        // 3. Display the results
        serviceManager.displayServicesForReservation(resId);

        System.out.println("\nCore booking logic remains untouched. Services are managed independently.");
    }
}