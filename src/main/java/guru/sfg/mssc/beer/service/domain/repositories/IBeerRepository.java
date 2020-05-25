//: guru.sfg.mssc.beer.service.domain.repositories.IBeerRepository.java


package guru.sfg.mssc.beer.service.domain.repositories;


import guru.sfg.mssc.beer.service.domain.model.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface IBeerRepository extends JpaRepository<Beer, UUID> {

}///:~