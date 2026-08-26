package pl.edu.pja.pk_gorski_filip_16608.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pja.pk_gorski_filip_16608.model.Concert;
import pl.edu.pja.pk_gorski_filip_16608.model.ConcertHall;
import pl.edu.pja.pk_gorski_filip_16608.model.BandConcertParticipation;
import pl.edu.pja.pk_gorski_filip_16608.repository.BookerRepository;
import pl.edu.pja.pk_gorski_filip_16608.repository.ConcertHallRepository;
import pl.edu.pja.pk_gorski_filip_16608.repository.ConcertRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final BookerRepository bookerRepository;
    private final ConcertHallRepository concertHallRepository;

    public ConcertService(ConcertRepository concertRepository,
                          BookerRepository bookerRepository,
                          ConcertHallRepository concertHallRepository) {
        this.concertRepository = concertRepository;
        this.bookerRepository = bookerRepository;
        this.concertHallRepository = concertHallRepository;
    }

    @Transactional
    public Concert saveConcert(Concert concert, Long bookerId) {
        var booker = bookerRepository.findById(bookerId).orElseThrow();
        concert.setBooker(booker);
        return concertRepository.save(concert);
    }

    @Transactional(readOnly = true)
    public List<Concert> findConcertsWithoutHall() {
        return concertRepository.findByConcertHallIsNull();
    }

    @Transactional(readOnly = true)
    public List<ConcertHall> findAvailableHalls(Long concertId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow();
        List<ConcertHall> availableConcertHalls = new ArrayList<>();
        for (ConcertHall hall : concertHallRepository.findAll()) {
            if (hall.isAvailable(concert.getDate(), concert.getStart(), concert.getEnd())) {
                availableConcertHalls.add(hall);
            }
        }
        return availableConcertHalls;
    }

    @Transactional(readOnly = true)
    public List<Concert> findConcertsByBooker(Long bookerId) {
        return concertRepository.findByBookerIdOrderByDateAscStartAsc(bookerId);
    }

    @Transactional(readOnly = true)
    public Concert findConcertWithDetails(Long concertId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow();
        for (BandConcertParticipation cp : concert.getParticipations()) {
            cp.getBand().getGenres().size();
        }
        return concert;
    }
    
    @Transactional
    public void assignConcertHall(Long concertId, Long concertHallId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow();
        ConcertHall hall = concertHallRepository.findById(concertHallId).orElseThrow();
        concert.reserveConcertHall(hall);
        concertRepository.save(concert);
    }
}