package co.com.techtest.model.inventory;

public record Inventory(String eventId, Long capacity, Long available, Long reserved, Long sold) {
}
