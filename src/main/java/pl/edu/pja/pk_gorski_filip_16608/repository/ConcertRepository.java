package pl.edu.pja.pk_gorski_filip_16608.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pja.pk_gorski_filip_16608.model.Concert;

import java.util.List;

@Repository
public interface ConcertRepository extends CrudRepository<Concert, Long> {
    List<Concert> findByConcertHallIsNull();
    List<Concert> findByBookerIdOrderByDateAscStartAsc(Long bookerId);
}
