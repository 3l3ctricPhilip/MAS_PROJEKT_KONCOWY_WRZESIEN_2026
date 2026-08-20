package pl.edu.pja.pk_gorski_filip_16608.service;

import org.springframework.stereotype.Service;
import pl.edu.pja.pk_gorski_filip_16608.model.Band;
import pl.edu.pja.pk_gorski_filip_16608.model.Genre;

import java.util.Collections;
import java.util.Set;

@Service
public class BandService {

    private final GenreService genreService;

    public BandService(GenreService genreService) {
        this.genreService = genreService;
    }
    
    public Set<Band> getBandsByGenres(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return Collections.emptySet();
        }
        return genreService.getBandsByGenres(genres);
    }
}
