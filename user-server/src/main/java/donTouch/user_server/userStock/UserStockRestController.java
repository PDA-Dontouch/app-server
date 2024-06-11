package donTouch.user_server.userStock;

import donTouch.user_server.kafka.service.KafkaService;
import donTouch.user_server.userStock.dto.FindStocksReq;
import donTouch.utils.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class UserStockRestController {
    private final KafkaService kafkaService;

    @GetMapping("/api/user/stocks")
    public ApiUtils.ApiResult<String> findStocks(@Valid @RequestBody FindStocksReq findStocksReq) {
        try {
            kafkaService.findStocks(findStocksReq);
            return ApiUtils.success("sent kafka message");
        } catch (Exception e) {
            return ApiUtils.error("fail to send kafka message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
