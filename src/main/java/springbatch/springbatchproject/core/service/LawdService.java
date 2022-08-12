package springbatch.springbatchproject.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbatch.springbatchproject.core.entity.Lawd;
import springbatch.springbatchproject.core.repository.LawdRepository;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LawdService {

    private final LawdRepository lawdRepository;

    @Transactional
    public void upsert(Lawd lawd){
        Lawd saved = lawdRepository.findByLawdCd(lawd.getLawdCd())
                .orElseGet(Lawd::of);

        saved.updateLawd(lawd.getLawdCd(), lawd.getLawdDong(), lawd.getExist());
        lawdRepository.save(saved);
    }
}
