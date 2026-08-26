package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ConcertHall extends Room {

    private double stageSize;
    private int acousticRating;

    private boolean lightingSystem;
    private boolean soundSystem;

    @OneToMany(mappedBy = "concertHall")
    private Set<Concert> concerts = new HashSet<>();

    protected ConcertHall() {}

    ConcertHall(String name,
                int capacity,
                double area,
                BigDecimal pricePerHour,
                Building building,
                double stageSize,
                int acousticRating,
                boolean lightingSystem,
                boolean soundSystem) {

        super(name, capacity, area, pricePerHour, building);
        setStageSize(stageSize);
        setAcousticRating(acousticRating);
        this.lightingSystem = lightingSystem;
        this.soundSystem = soundSystem;
    }

    public double getStageSize() {
        return stageSize;
    }

    public void setStageSize(double stageSize) {
        if (stageSize <= 0) {
            throw new IllegalArgumentException("Stage size must be greater than 0");
        }
        this.stageSize = stageSize;
    }

    public int getAcousticRating() {
        return acousticRating;
    }

    public void setAcousticRating(int acousticRating) {
        if (acousticRating < 1 || acousticRating > 10) {
            throw new IllegalArgumentException("Acoustic rating must be in range 1..10");
        }
        this.acousticRating = acousticRating;
    }

    public boolean isLightingSystem() {
        return lightingSystem;
    }

    public void setLightingSystem(boolean lightingSystem) {
        this.lightingSystem = lightingSystem;
    }

    public boolean isSoundSystem() {
        return soundSystem;
    }

    public void setSoundSystem(boolean soundSystem) {
        this.soundSystem = soundSystem;
    }

    public Set<Concert> getConcerts() {
        return concerts;
    }

    protected void setConcerts(Set<Concert> concerts) {
        this.concerts = concerts;
    }

    public boolean isAvailable(LocalDate date, LocalDateTime start, LocalDateTime end) {
        for (Concert existingConcert : getConcerts()) {
            if (existingConcert.getDate().equals(date) && existingConcert.getStatus() != ConcertStatus.CANCELLED) {
                boolean overlaps = start.isBefore(existingConcert.getEnd()) && end.isAfter(existingConcert.getStart());
                if (overlaps) return false;
            }
        }
        return true;
    }
}
