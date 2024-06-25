package donTouch.stock_server.web;

import donTouch.stock_server.web.dto.LikeStockDTO;
import donTouch.stock_server.web.dto.PurchaseInfoDTO;
import donTouch.stock_server.web.dto.PurchasedStockDTO;
import donTouch.stock_server.web.dto.ScoreDto;
import donTouch.utils.utils.ApiUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@PropertySource(value = {"application.properties"})
public class Web {
    private static String USER_URL;
    private static String HOLDING_URL;

    @Value("${USER_URL}")
    public void setUserUrl(String userUrl) {
        USER_URL = userUrl;
    }

    @Value("${HOLDING_URL}")
    public void setHoldingUrl(String holdingUrl) {
        HOLDING_URL = holdingUrl;
    }

    public static List<LikeStockDTO> getLikeStockDTOList(Long userId) {
        try {
            WebClient webClient = WebClient.create();

            String getLikeStockIdsUrl = USER_URL + "/api/user/like/stocks?userId=";
            ResponseEntity<ApiUtils.ApiResult<List<LikeStockDTO>>> responseEntity = webClient.get()
                    .uri(getLikeStockIdsUrl + userId)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiUtils.ApiResult<List<LikeStockDTO>>>() {
                    })
                    .block();

            return responseEntity.getBody().getResponse();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static List<PurchasedStockDTO> getCombinationPurchased(Long userId) {
        try {
            WebClient webClient = WebClient.create();

            String getCombinationPurchasedUrl = HOLDING_URL + "/api/holding/combination?userId=" + userId;

            ResponseEntity<ApiUtils.ApiResult<List<PurchasedStockDTO>>> responseEntity = webClient.get()
                    .uri(getCombinationPurchasedUrl)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiUtils.ApiResult<List<PurchasedStockDTO>>>() {
                    })
                    .block();

            return responseEntity.getBody().getResponse();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static Map<String, List<PurchaseInfoDTO>> getHoldingStockPurchaseInfos(Long userId) {
        try {
            WebClient webClient = WebClient.create();

            String getLikeStockIdsUrl = HOLDING_URL + "/api/holding/stocks?&userId=" + userId + "&getPrice=true";

            ResponseEntity<ApiUtils.ApiResult<Map<String, List<PurchaseInfoDTO>>>> responseEntity = webClient.get()
                    .uri(getLikeStockIdsUrl)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiUtils.ApiResult<Map<String, List<PurchaseInfoDTO>>>>() {
                    })
                    .block();

            return responseEntity.getBody().getResponse();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static ScoreDto getUserScore(Long userId) {
        try {
            WebClient webClient = WebClient.create();

            String getLikeStockIdsUrl = USER_URL + "/api/user/score?userId=" + userId;

            ResponseEntity<ApiUtils.ApiResult<ScoreDto>> responseEntity = webClient.get()
                    .uri(getLikeStockIdsUrl)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiUtils.ApiResult<ScoreDto>>() {
                    })
                    .block();

            return responseEntity.getBody().getResponse();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
