package springbatch.springbatchproject.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbatch.springbatchproject.core.entity.Lawd;

import java.util.Optional;

public interface LawdRepository extends JpaRepository<Lawd, Long> {

    Optional<Lawd> findByLawdCd(String lawdCd);
}
