package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Owner extends Person {

    @NotBlank
    private String contactPersonName;

    private Boolean verified;

    @OneToMany(mappedBy = "owner")
    private Set<Building> buildings = new HashSet<>();

    protected Owner() {}

    public Owner(String nick,
                 String email,
                 String country,
                 String city,
                 String phoneNumber,
                 String contactPersonName) {
        super(nick, email, country, city, phoneNumber);
        setContactPersonName(contactPersonName);
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        if (contactPersonName == null || contactPersonName.isBlank()) {
            throw new IllegalArgumentException("Contact person name is required");
        }
        this.contactPersonName = contactPersonName;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Set<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(Set<Building> buildings) {
        this.buildings = buildings;
    }

    public void addBuilding(Building building) {
        if (building == null) return;
        building.setOwner(this);
    }

    public void removeBuilding(Building building) {
        if (building == null) return;
        if (building.getOwner() == this) {
            building.setOwner(null);
        }
    }
}
