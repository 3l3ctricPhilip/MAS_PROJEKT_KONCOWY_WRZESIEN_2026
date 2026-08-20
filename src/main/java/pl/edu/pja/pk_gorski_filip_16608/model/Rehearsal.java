package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Rehearsal extends Event {

    @ManyToMany(mappedBy = "rehearsals")
    private Set<Band> bands = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private RehearsalType rehearsalType;

    @ElementCollection
    private List<String> tracksPlanned = new ArrayList<>();

    private String description;

    @ManyToOne
    private RehearsalRoom rehearsalRoom;

    public Rehearsal() {}

    public Rehearsal(String name, java.time.LocalDate date, java.time.LocalDateTime start, java.time.LocalDateTime end, RehearsalType rehearsalType) {
        super(name, date, start, end);
        setRehearsalType(rehearsalType);
    }

    public Set<Band> getBands() {
        return bands;
    }

    public void setBands(Set<Band> bands) {
        this.bands = bands;
    }

    public RehearsalType getRehearsalType() {
        return rehearsalType;
    }

    public void setRehearsalType(RehearsalType rehearsalType) {
        if (rehearsalType == null) {
            throw new IllegalArgumentException("Rehearsal type is required");
        }
        this.rehearsalType = rehearsalType;
    }

    public List<String> getTracksPlanned() {
        return tracksPlanned;
    }

    public void setTracksPlanned(List<String> tracksPlanned) {
        this.tracksPlanned = tracksPlanned;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RehearsalRoom getRehearsalRoom() {
        return rehearsalRoom;
    }

    public void setRehearsalRoom(RehearsalRoom rehearsalRoom) {
        if (this.rehearsalRoom == rehearsalRoom) return;

        RehearsalRoom previousRehearsalRoom = this.rehearsalRoom;
        this.rehearsalRoom = rehearsalRoom;

        if (previousRehearsalRoom != null) {
            previousRehearsalRoom.getRehearsals().remove(this);
        }
        if (rehearsalRoom != null) {
            rehearsalRoom.getRehearsals().add(this);
        }
    }

    public void reserveRehearsalRoom(RehearsalRoom rehearsalRoom) {
        if (rehearsalRoom == null) {
            throw new IllegalArgumentException("Rehearsal room is required");
        }
        if (!rehearsalRoom.isAvailable(getDate(), getStart(), getEnd())) {
            throw new IllegalStateException("Rehearsal room is not available for the selected time");
        }
        setRehearsalRoom(rehearsalRoom);
    }
}