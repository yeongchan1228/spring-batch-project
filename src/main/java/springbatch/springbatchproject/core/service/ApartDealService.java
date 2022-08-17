package springbatch.springbatchproject.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbatch.springbatchproject.core.entity.Apart;
import springbatch.springbatchproject.core.entity.ApartDeal;
import springbatch.springbatchproject.core.repository.ApartDealRepository;
import springbatch.springbatchproject.core.repository.ApartRepository;
import springbatch.springbatchproject.core.service.dto.ApartDto;
import springbatch.springbatchproject.job.apt.dto.AptDealDto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ApartDealDto에 있는 값을 Apart, ApartDeal 엔티티로 저장한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApartDealService {

    private final ApartRepository apartRepository;
    private final ApartDealRepository apartDealRepository;

    @Transactional
    public void upsert(AptDealDto dto) {
        Apart apart = getApartOrNew(dto);
        saveApartDeal(dto, apart);
    }

    private void saveApartDeal(AptDealDto dto, Apart apart) {
        ApartDeal apartDeal = apartDealRepository.findApartDealByApartAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
                apart, dto.getExclusiveArea(), dto.getDealDate(), dto.getDealAmount(), dto.getFloor()
        ).orElseGet(() -> ApartDeal.of(dto, apart));

        apartDeal.changeDealCanceled(dto.isDealCanceled(), dto.getDealCanceledDate());
        apartDealRepository.save(apartDeal);
    }

    private Apart getApartOrNew(AptDealDto dto) {
        Apart apart = apartRepository.findApartByApartNameAndJibun(dto.getAptName(), dto.getJibun())
                .orElseGet(() -> Apart.from(dto));
        apartRepository.save(apart);
        return apart;
    }

    public List<ApartDto> findByLawdCdAndDealDate(String lawdCd, LocalDate dealDate) {
        return apartDealRepository.findByDealCanceledIsFalseAndDealDateEquals(dealDate)
                .stream()
                .filter(apartDeal -> apartDeal.getApart().getLawdCd().equals(lawdCd))
                .map(apartDeal -> new ApartDto(apartDeal.getApart().getApartName(), apartDeal.getDealAmount()))
                .collect(Collectors.toList());
    }
}
