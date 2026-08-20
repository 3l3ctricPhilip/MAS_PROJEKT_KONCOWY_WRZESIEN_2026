package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Room {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int capacity;
    private double area;
    private BigDecimal pricePerHour;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Building building;

    protected Room() {}

    protected Room(String name, int capacity, double area, BigDecimal pricePerHour, Building building) {
        setName(name);
        setCapacity(capacity);
        setArea(area);
        setPricePerHour(pricePerHour);
        setBuilding(building);
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Room name is required");
        }
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Room capacity must be greater than 0");
        }
        this.capacity = capacity;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        if (area <= 0) {
            throw new IllegalArgumentException("Room area must be greater than 0");
        }
        this.area = area;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        if (pricePerHour == null) {
            throw new IllegalArgumentException("Price per hour is required");
        }
        if (pricePerHour.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price per hour must be greater than 0");
        }
        this.pricePerHour = pricePerHour;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        if (building == null) {
            throw new IllegalArgumentException("Room must belong to a Building (composition)");
        }
        if (this.building != null && this.building != building) {
            throw new IllegalStateException("Room cannot be moved to another Building (composition)");
        }
        this.building = building;
    }
}
