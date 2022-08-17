package springbatch.springbatchproject.job.notify.dto;

import lombok.Builder;
import lombok.Getter;
import springbatch.springbatchproject.core.service.dto.ApartDto;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class NotificationDto {
    private String email;
    private String guName;
    private Integer count;
    private List<ApartDto> apartDeals;

    public String toMessage() {
        DecimalFormat decimalFormat = new DecimalFormat();
        return String.format("%s 아파트 실거래가 알림\n " +
                "총 %d개 거래가 발생했습니다.\n ", guName, count)
                +
                apartDeals.stream()
                        .map(deal -> String.format("- %s : %s원", deal.getName(), decimalFormat.format(deal.getPrice())))
                        .collect(Collectors.joining());
    }
}
