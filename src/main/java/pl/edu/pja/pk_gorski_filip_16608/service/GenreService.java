package pl.edu.pja.pk_gorski_filip_16608.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pja.pk_gorski_filip_16608.model.Band;
import pl.edu.pja.pk_gorski_filip_16608.model.Genre;
import pl.edu.pja.pk_gorski_filip_16608.repository.GenreRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        genreRepository.findAll().forEach(genres::add);
        return genres;
    }
    
    @Transactional
    public Set<Band> getBandsByGenres(Set<Genre> genres) {
        Set<Band> bands = new HashSet<>();
        for (Genre genre : genres) {
            Genre managedGenre = genreRepository.findById(genre.getId()).orElseThrow();
            bands.addAll(managedGenre.getBands());
        }
        return bands;
    }
}
