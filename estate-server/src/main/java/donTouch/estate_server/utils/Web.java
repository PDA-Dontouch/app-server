package donTouch.estate_server.utils;

import donTouch.utils.utils.ApiUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class Web {
    private static WebClient webClient;

    public static List<Integer> getLikeEstateFundIds(Long userId) {
        try {
            WebClient webClient = getWebClient();

            String getLikeEstateFundIdsUrl = "http://15.165.74.129:8081" + "/api/user/like/estate?userId=" + userId;
            ResponseEntity<ApiUtils.ApiResult<List<Integer>>> responseEntity = webClient.get()
                    .uri(getLikeEstateFundIdsUrl)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiUtils.ApiResult<List<Integer>>>() {
                    })
                    .block();

            if (responseEntity != null && responseEntity.getBody() != null) {
                return responseEntity.getBody().getResponse();
            } else {
                throw new IllegalStateException("Failed to retrieve data from API");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch data from API", e);
        }
    }

    private static WebClient getWebClient() {
        if (webClient == null) {
            webClient = WebClient.create();
        }
        return webClient;
    }
}
