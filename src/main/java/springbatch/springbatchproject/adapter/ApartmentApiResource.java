package springbatch.springbatchproject.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 아파트 실거래가 API 호출을 위한 파라미터
 * 1. serviceKey - API 호출을 위한 인증 키
 * 2. LAWD_CD - 법정동 코드 앞 5자리. 예) 41135
 * 3. DEAL_YMD - 거래가 발생한 년월. 예) 202107
 */
@Slf4j
@Component
public class ApartmentApiResource {

    @Value("${external.apartment-api.path}")
    private String path;
    @Value("${external.apartment-api.service-key}")
    private String serviceKey = "CFwUyQt9QydvOxASayoqsfpnDc83hEv71DGLJQjFP6AubrxNKfdd8JkFNmvhGX5JjWLeNCCa2ZoJlI%2BdXQu0pg%3D%3D";

    public Resource getResource(String lawdCd, YearMonth yearMonth) {
        String url = String.format("%s?serviceKey=%s&LAWD_CD=%s&DEAL_YMD=%s",
                path, serviceKey, lawdCd, yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")));

        log.info("Resource URL = {}", url);
        try {
            return new UrlResource(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Failed to created UrlResource");
        }
    }
}
