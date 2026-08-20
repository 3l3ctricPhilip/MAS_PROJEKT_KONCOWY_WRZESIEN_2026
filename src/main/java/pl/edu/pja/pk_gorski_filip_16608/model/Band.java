package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Entity
public class Band {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    private String country;
    private String city;
    private LocalDate formedDate;

    @ElementCollection
    private List<String> socialMedia = new ArrayList<>();

    @ElementCollection
    private List<String> websites = new ArrayList<>();

    private boolean openToRecruitment;

    @ManyToMany
    private Set<Musician> musicians = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "band_rehearsal",
            joinColumns = @JoinColumn(name = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "rehearsal_id"))
    private Set<Rehearsal> rehearsals = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "band_genres",
            joinColumns = @JoinColumn(name = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "band")
    private Set<BandConcertParticipation> concertParticipations = new HashSet<>();


    public Band() {}

    public Band(String name) {
        this.name = name;
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
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDate getFormedDate() {
        return formedDate;
    }

    public void setFormedDate(LocalDate formedDate) {
        this.formedDate = formedDate;
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

    public boolean isOpenToRecruitment() {
        return openToRecruitment;
    }

    public void setOpenToRecruitment(boolean openToRecruitment) {
        this.openToRecruitment = openToRecruitment;
    }

    public Set<Musician> getMusicians() {
        return musicians;
    }

    public void setMusicians(Set<Musician> musicians) {
        this.musicians = musicians;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Set<BandConcertParticipation> getConcertParticipations() {
        return concertParticipations;
    }

    public void setConcertParticipations(Set<BandConcertParticipation> concertParticipations) {
        this.concertParticipations = concertParticipations;
    }

    public Set<Rehearsal> getRehearsals() {
        return rehearsals;
    }

    public void setRehearsals(Set<Rehearsal> rehearsals) {
        this.rehearsals = rehearsals;
    }

    public void addMusician(Musician musician) {
        if (musician == null)
            throw new NullPointerException("musician is null");
        if (!(musicians.contains(musician))) {
            musicians.add(musician);
            musician.addBand(Band.this);
        }
    }

    public void removeMusician(Musician musician) {
        if (musician == null)
            throw new NullPointerException("musician is null");
        if (musicians.contains(musician)) {
            musicians.remove(musician);
            musician.removeBand(this);
        } else {
            System.out.println("Musician is not in this band");
        }
    }

    public void addGenre(Genre genre) {
        if (genre == null) return;
        if (genres.add(genre)) {
            genre.getBands().add(this);
        }
    }

    public void removeGenre(Genre genre) {
        if (genre == null) return;
        if (genres.remove(genre)) {
            genre.getBands().remove(this);
        }
    }

    public boolean isAvailable(LocalDate date, LocalTime start, LocalTime end) {
        if (date == null || start == null || end == null) {
            throw new IllegalArgumentException("Date, start and end are required");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        for (BandConcertParticipation participation : getConcertParticipations()) {
            Concert concert = participation.getConcert();
            if (concert == null) continue;

            if (date.equals(concert.getDate()) && concert.getStatus() != ConcertStatus.CANCELLED) {
                boolean overlaps = start.isBefore(participation.getEnd())
                        && end.isAfter(participation.getStart());
                if (overlaps) return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Band band = (Band) o;
        return Objects.equals(name, band.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
