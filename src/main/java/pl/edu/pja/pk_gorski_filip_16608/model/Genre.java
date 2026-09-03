package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Genre {

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    @Column(unique = true)
    private String name;

    @Column(length = 2000)
    private String description;

    @ManyToMany(mappedBy = "genres")
    private Set<Musician> musicians = new HashSet<>();

    @ManyToMany(mappedBy = "genres")
    private Set<Band> bands = new HashSet<>();

    public Genre() {}


    public Genre(String name) {
        setName(name);
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
            throw new IllegalArgumentException("Genre name is required");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Musician> getMusicians() {
        return musicians;
    }

    public void setMusicians(Set<Musician> musicians) {
        this.musicians = musicians;
    }

    public Set<Band> getBands() {
        return bands;
    }

    public void setBands(Set<Band> bands) {
        this.bands = bands;
    }

    @Override
    public String toString() {
        return name;
    }
}
