package pl.edu.pja.pk_gorski_filip_16608.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.pja.pk_gorski_filip_16608.model.Band;
import pl.edu.pja.pk_gorski_filip_16608.model.Genre;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface BandRepository extends CrudRepository<Band, Long> {

    List<Band> findByGenresContaining(Set<Genre> genres);

    List<Band> findDistinctByGenresIn(Collection<Genre> genres);

    @Query("""
    SELECT DISTINCT b
    FROM Band b
    JOIN b.genres g
    JOIN b.concertParticipations cp
    JOIN cp.concert c
    WHERE g.name IN :genreNames
      AND c.date != :concertDate
      AND c.start >= :start
      AND c.end <= :end
""")
    List<Band> findDistinctByGenresAndConcertDateAndTimeRange(
            @Param("genreNames") Collection<String> genreNames,
            @Param("concertDate") LocalDate concertDate,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
