package pl.edu.pja.pk_gorski_filip_16608.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pja.pk_gorski_filip_16608.model.Owner;

@Repository
public interface OwnerRepository extends CrudRepository<Owner, Long> {
}
