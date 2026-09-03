package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Entity
public class BandConcertParticipation {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne
    private Concert concert;

    @NotNull
    @ManyToOne
    private Band band;

    @Column(name = "start_time", nullable = false)
    private LocalTime start;

    @Column(name = "end_time", nullable = false)
    private LocalTime end;

    public BandConcertParticipation() {}

    public BandConcertParticipation(Concert concert, Band band, LocalTime start, LocalTime end) {
        setConcert(concert);
        setBand(band);
        setStart(start);
        setEnd(end);
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public Concert getConcert() {
        return concert;
    }

    public void setConcert(Concert concert) {
        if (concert == null) throw new IllegalArgumentException("Concert is required");
        this.concert = concert;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        if (band == null) throw new IllegalArgumentException("Band is required");
        this.band = band;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        if (start == null) throw new IllegalArgumentException("Start time is required");
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        if (end == null) throw new IllegalArgumentException("End time is required");
        this.end = end;
    }

    private void validateTimeRange() {
        if (start != null && end != null && !end.isAfter(start)) {
            throw new IllegalArgumentException("Participation end time must be after start time");
        }
    }

    @Transient
    public java.time.LocalDate getConcertDate() {
        return getConcert() != null ? getConcert().getDate() : null;
    }
}