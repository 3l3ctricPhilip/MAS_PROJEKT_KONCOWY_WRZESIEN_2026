package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Event {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(name = "event_date", nullable = true)
    private LocalDate date;

    @Column(name = "start_time")
    private LocalDateTime start;

    @Column(name = "end_time")
    private LocalDateTime end;

    protected Event() {}

    protected Event(String name, LocalDateTime start, LocalDateTime end) {
        setName(name);
        setStart(start);
        setEnd(end);
        validateTimeRange();
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
            throw new IllegalArgumentException("Event name is required");
        }
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        if (start == null) throw new IllegalArgumentException("Event start time is required");
        this.start = start;
        this.date = start.toLocalDate();
        validateTimeRange();
    }


    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        if (end == null) throw new IllegalArgumentException("Event end time is required");
        this.end = end;
        validateTimeRange();
    }

    protected void validateTimeRange() {
        if (start != null && end != null && !end.isAfter(start)) {
            throw new IllegalArgumentException("Event end time must be after start time");
        }
    }
}
