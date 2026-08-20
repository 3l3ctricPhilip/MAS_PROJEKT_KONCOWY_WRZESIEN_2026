package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;

@Entity
public class Instrument {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private InstrumentType type;

    private String description;

    @ManyToOne
    private Musician musician;

    public Instrument() {}

    public Instrument(String name, InstrumentType type) {
        setName(name);
        setType(type);
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
            throw new IllegalArgumentException("Instrument name is required");
        }
        this.name = name;
    }

    public InstrumentType getType() {
        return type;
    }

    public void setType(InstrumentType type) {
        if (type == null) {
            throw new IllegalArgumentException("Instrument type is required");
        }
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Musician getMusician() {
        return musician;
    }

    public void setMusician(Musician musician) {
        this.musician = musician;
    }
}
