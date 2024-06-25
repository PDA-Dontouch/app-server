package donTouch.energy_server.utils;

import donTouch.utils.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@PropertySource(value = {"application.properties"})
public class Web {
    private static String userUrl;

    @Value("${USER_URL}")
    public void setUserUrl(String userUrl) {
        Web.userUrl = userUrl;
    }

    public static List<String> getLikeEnergyFundIds(Long userId) {
        try {
            WebClient webClient = WebClient.create();

            String getLikeEnergyFundIdsUrl = userUrl + "/api/user/like/energy?userId=" + userId;
            ResponseEntity<ApiUtils.ApiResult<List<String>>> responseEntity = webClient.get()
                    .uri(getLikeEnergyFundIdsUrl)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiUtils.ApiResult<List<String>>>() {
                    })
                    .block();

            return responseEntity.getBody().getResponse();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch data from API", e);
        }
    }
}
