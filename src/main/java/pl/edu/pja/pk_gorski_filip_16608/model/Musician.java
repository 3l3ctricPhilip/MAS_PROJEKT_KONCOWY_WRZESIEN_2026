package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Musician extends Person {

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<MusicianRole> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dateOfBirth;

    @Column(length = 2000)
    private String bio;

    private boolean openToPlay;

    @ManyToMany(mappedBy = "musicians")
    private Set<Band> bands = new HashSet<>();

    @ManyToMany
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "musician")
    private Set<Instrument> instruments = new HashSet<>();


    public Musician() {}

    public Musician(String nick, String email, String country, String city, String phoneNumber,
                    Gender gender, LocalDate dateOfBirth, String bio, boolean openToPlay) {
        super(nick, email, country, city, phoneNumber);
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bio = bio;
        this.openToPlay = openToPlay;
    }

    public Set<MusicianRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<MusicianRole> roles) {
        this.roles = roles;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Transient
    public int getAge() {
        return (dateOfBirth == null) ? 0 : Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isOpenToPlay() {
        return openToPlay;
    }

    public void setOpenToPlay(boolean openToPlay) {
        this.openToPlay = openToPlay;
    }

    public Set<Band> getBands() {
        return bands;
    }

    public void setBands(Set<Band> bands) {
        this.bands = bands;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public Set<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(Set<Instrument> instruments) {
        this.instruments = instruments;
    }


    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public void addBand(Band band) {
        if (bands == null)
            throw new NullPointerException("bands is null");
        if (!bands.contains(band)) {
            bands.add(band);
            band.addMusician(Musician.this);
        } else {
            System.out.println("The musician is added to " + band.getName());
        }
    }

    public void removeBand(Band band) {
        if (bands == null)
            throw new NullPointerException("bands is null");
        if (bands.contains(band)) {
            bands.remove(band);
            band.removeMusician(this);
        } else {
            System.out.println(band.getName() + "The band doesn't have this musician.");
        }
    }

    public void addGenre(Genre genre) {
        if (genre == null) return;
        if (genres.add(genre)) {
            genre.getMusicians().add(this);
        }
    }

    public void removeGenre(Genre genre) {
        if (genre == null) return;
        if (genres.remove(genre)) {
            genre.getMusicians().remove(this);
        }
    }

    public void addInstrument(Instrument instrument) {
        if (instrument == null) return;

        Musician oldOwner = instrument.getMusician();
        if (oldOwner != null && oldOwner != this) {
            oldOwner.getInstruments().remove(instrument);
        }

        if (instruments.add(instrument)) {
            instrument.setMusician(this);
        } else {
            instrument.setMusician(this);
        }
    }

    public void removeInstrument(Instrument instrument) {
        if (instrument == null) return;

        if (instruments.remove(instrument)) {
            instrument.setMusician(null);
        }
    }

    @Transient
    public Band createBand(String name, String description, String country, String city, boolean openToRecruitment) {

        Band band = new Band();
        band.setName(name);
        band.setDescription(description);
        band.setCountry(country);
        band.setCity(city);
        band.setFormedDate(LocalDate.now());
        band.setOpenToRecruitment(openToRecruitment);

        band.addMusician(this);

        return band;
    }

}
