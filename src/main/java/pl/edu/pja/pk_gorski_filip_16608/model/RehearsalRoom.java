package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class RehearsalRoom extends Room {

    private boolean drumKit;
    private boolean bassAmp;
    private boolean guitarAmps;
    private boolean mixer;
    private boolean paSpeakers;
    private boolean stageMonitors;
    private boolean microphones;

    @OneToMany(mappedBy = "rehearsalRoom")
    private Set<Rehearsal> rehearsals = new HashSet<>();

    protected RehearsalRoom() {}

    RehearsalRoom(String name,
                  int capacity,
                  double area,
                  BigDecimal pricePerHour,
                  Building building,
                  boolean drumKit,
                  boolean bassAmp,
                  boolean guitarAmps,
                  boolean mixer,
                  boolean paSpeakers,
                  boolean stageMonitors,
                  boolean microphones) {

        super(name, capacity, area, pricePerHour, building);
        this.drumKit = drumKit;
        this.bassAmp = bassAmp;
        this.guitarAmps = guitarAmps;
        this.mixer = mixer;
        this.paSpeakers = paSpeakers;
        this.stageMonitors = stageMonitors;
        this.microphones = microphones;
    }

    public boolean isDrumKit() { return drumKit; }
    public void setDrumKit(boolean drumKit) { this.drumKit = drumKit; }

    public boolean isBassAmp() { return bassAmp; }
    public void setBassAmp(boolean bassAmp) { this.bassAmp = bassAmp; }

    public boolean isGuitarAmps() { return guitarAmps; }
    public void setGuitarAmps(boolean guitarAmps) { this.guitarAmps = guitarAmps; }

    public boolean isMixer() { return mixer; }
    public void setMixer(boolean mixer) { this.mixer = mixer; }

    public boolean isPaSpeakers() { return paSpeakers; }
    public void setPaSpeakers(boolean paSpeakers) { this.paSpeakers = paSpeakers; }

    public boolean isStageMonitors() { return stageMonitors; }
    public void setStageMonitors(boolean stageMonitors) { this.stageMonitors = stageMonitors; }

    public boolean isMicrophones() { return microphones; }
    public void setMicrophones(boolean microphones) { this.microphones = microphones; }

    public Set<Rehearsal> getRehearsals() {
        return rehearsals;
    }

    protected void setRehearsals(Set<Rehearsal> rehearsals) {
        this.rehearsals = rehearsals;
    }

    public void addRehearsal(Rehearsal rehearsal) {
        if (rehearsal == null) return;
        rehearsal.setRehearsalRoom(this);
    }

    public void removeRehearsal(Rehearsal rehearsal) {
        if (rehearsal == null) return;
        if (rehearsal.getRehearsalRoom() == this) {
            rehearsal.setRehearsalRoom(null);
        }
    }

    public boolean isAvailable(LocalDate date, LocalDateTime start, LocalDateTime end) {

        if (date == null || start == null || end == null) {
            throw new IllegalArgumentException("Date, start and end are required");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        for (Rehearsal existingRehearsal : getRehearsals()) {
            if (existingRehearsal.getDate().equals(date)) {
                boolean overlaps = start.isBefore(existingRehearsal.getEnd())
                        && end.isAfter(existingRehearsal.getStart());

                if (overlaps) {
                    return false;
                }
            }
        }
        return true;
    }
}
