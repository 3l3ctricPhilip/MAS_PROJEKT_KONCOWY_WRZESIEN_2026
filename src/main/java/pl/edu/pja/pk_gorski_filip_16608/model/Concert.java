package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Concert extends Event {

    private BigDecimal ticketPrice;

    @Enumerated(EnumType.STRING)
    private ConcertStatus status;

    @ManyToOne
    private Booker booker;

    @ManyToOne
    private ConcertHall concertHall;

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BandConcertParticipation> participations = new LinkedHashSet<>();

    public Concert() {}

    public Concert(String name,
                   java.time.LocalDate date,
                   java.time.LocalDateTime start,
                   java.time.LocalDateTime end,
                   ConcertStatus status) {
        super(name, date, start, end);
        setStatus(status);
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        if (ticketPrice != null && ticketPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative");
        }
        this.ticketPrice = ticketPrice;
    }

    public ConcertStatus getStatus() {
        return status;
    }

    public void setStatus(ConcertStatus status) {
        this.status = status;
    }

    public Set<BandConcertParticipation> getParticipations() {
        return participations;
    }

    public void setParticipations(Set<BandConcertParticipation> participations) {
        this.participations = participations;
    }

    public Booker getBooker() {
        return booker;
    }

    public void setBooker(Booker booker) {
        if (this.booker == booker) return;

        Booker previousBooker = this.booker;
        this.booker = booker;

        if (previousBooker != null) {
            previousBooker.getOrganizedConcerts().remove(this);
        }
        if (booker != null) {
            booker.getOrganizedConcerts().add(this);
        }
    }

    public ConcertHall getConcertHall() {
        return concertHall;
    }


    public void setConcertHall(ConcertHall concertHall) {
        if (this.concertHall == concertHall) return;

        ConcertHall previousConcertHall = this.concertHall;
        this.concertHall = concertHall;

        if (previousConcertHall != null) {
            previousConcertHall.getConcerts().remove(this);
        }
        if (concertHall != null) {
            concertHall.getConcerts().add(this);
        }
    }

    public BandConcertParticipation addBand(Band band, java.time.LocalTime start, java.time.LocalTime end) {
        BandConcertParticipation concertParticipation = new BandConcertParticipation(this, band, start, end);

        if (participations.add(concertParticipation)) {
            band.getConcertParticipations().add(concertParticipation);
        }
        return concertParticipation;
    }

    public void removeParticipation(BandConcertParticipation concertParticipation) {
        if (concertParticipation == null) return;

        if (participations.remove(concertParticipation)) {
            concertParticipation.getBand().getConcertParticipations().remove(concertParticipation);
        }
    }

    public void reserveConcertHall(ConcertHall concertHall) {
        if (concertHall == null) {
            throw new IllegalArgumentException("Concert hall is required");
        }
        if (!concertHall.isAvailable(getDate(), getStart(), getEnd())) {
            throw new IllegalStateException("Concert hall is not available for the selected time");
        }
        setConcertHall(concertHall);
    }
}
