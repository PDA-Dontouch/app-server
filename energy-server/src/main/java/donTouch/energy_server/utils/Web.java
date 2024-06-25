package donTouch.energy_server.utils;

import donTouch.utils.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@PropertySource(value = {"env.properties"})
public class Web {
    @Value("${USER_URL}")
    private static String USER_URL;

    public static List<String> getLikeEnergyFundIds(Long userId) {
        try {
            WebClient webClient = WebClient.create();

            String getLikeEnergyFundIdsUrl = USER_URL + "/api/user/like/energy?userId=" + userId;
            ResponseEntity<ApiUtils.ApiResult<List<String>>> responseEntity = webClient.get()
                    .uri(getLikeEnergyFundIdsUrl)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiUtils.ApiResult<List<String>>>() {
                    })
                    .block();

            return responseEntity.getBody().getResponse();
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }
}
