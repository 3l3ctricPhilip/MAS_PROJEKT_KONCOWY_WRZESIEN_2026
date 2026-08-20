package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Building {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotBlank
    private String street;

    @NotBlank
    private String number;

    @NotBlank
    private String country;

    @ManyToOne
    private Owner owner;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Room> rooms = new HashSet<>();

    protected Building() {}

    public Building(String name, String city, String street, String number, String country, Owner owner) {
        setName(name);
        setCity(city);
        setStreet(street);
        setNumber(number);
        setCountry(country);
        setOwner(owner);
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
            throw new IllegalArgumentException("Building name is required");
        }
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street is required");
        }
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Number is required");
        }
        this.number = number;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country is required");
        }
        this.country = country;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        if (this.owner == owner) return;

        Owner previousOwner = this.owner;
        this.owner = owner;

        if (previousOwner != null) {
            previousOwner.getBuildings().remove(this);
        }
        if (owner != null) {
            owner.getBuildings().add(this);
        }
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    protected void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }


    public ConcertHall createConcertHall(String name,
                                         int capacity,
                                         double area,
                                         BigDecimal pricePerHour,
                                         double stageSize,
                                         int acousticRating,
                                         boolean lightingSystem,
                                         boolean soundSystem) {

        ConcertHall concertHall = new ConcertHall(
                name, capacity, area, pricePerHour, this,
                stageSize, acousticRating, lightingSystem, soundSystem
        );

        rooms.add(concertHall);
        return concertHall;
    }

    public RehearsalRoom createRehearsalRoom(String name,
                                             int capacity,
                                             double area,
                                             BigDecimal pricePerHour,
                                             boolean drumKit,
                                             boolean bassAmp,
                                             boolean guitarAmps,
                                             boolean mixer,
                                             boolean paSpeakers,
                                             boolean stageMonitors,
                                             boolean microphones) {

        RehearsalRoom rehearsalRoom = new RehearsalRoom(
                name, capacity, area, pricePerHour, this,
                drumKit, bassAmp, guitarAmps, mixer, paSpeakers, stageMonitors, microphones
        );

        rooms.add(rehearsalRoom);
        return rehearsalRoom;
    }

    public void removeRoom(Room room) {
        if (room == null) return;
        rooms.remove(room);
    }

    public Set<Room> showRooms() {
        return Set.copyOf(rooms);
    }
}
