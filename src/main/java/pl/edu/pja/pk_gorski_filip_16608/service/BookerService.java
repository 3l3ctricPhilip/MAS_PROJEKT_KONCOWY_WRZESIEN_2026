package pl.edu.pja.pk_gorski_filip_16608.service;

import org.springframework.stereotype.Service;
import pl.edu.pja.pk_gorski_filip_16608.model.Booker;
import pl.edu.pja.pk_gorski_filip_16608.repository.BookerRepository;

@Service
public class BookerService {

    private final BookerRepository bookerRepository;

    public BookerService(BookerRepository bookerRepository) {
        this.bookerRepository = bookerRepository;
    }
    
    public Booker getBookerById(Long id) {
        return bookerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booker not found: " + id));
    }
}
