package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class   Person {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String nick;

    @NaturalId
    @Column(unique = true)
    private String email;

    private String country;
    private String city;

    @Column(unique = true)
    private String phoneNumber;

    @ElementCollection
    private List<String> socialMedia = new ArrayList<>();

    @ElementCollection
    private List<String> websites = new ArrayList<>();


    public Person() {}

    public Person(String nick, String email, String country, String city, String phoneNumber) {
        this.nick = nick;
        this.email = email;
        this.country = country;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }


    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(List<String> socialMedia) {
        this.socialMedia = socialMedia;
    }

    public List<String> getWebsites() {
        return websites;
    }

    public void setWebsites(List<String> websites) {
        this.websites = websites;
    }
}
