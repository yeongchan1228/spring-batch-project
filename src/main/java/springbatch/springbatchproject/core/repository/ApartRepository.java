package springbatch.springbatchproject.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbatch.springbatchproject.core.entity.Apart;

import java.util.Optional;

public interface ApartRepository extends JpaRepository<Apart, Long> {

    Optional<Apart> findApartByApartNameAndJibun(String apartName, String jibun);
}
