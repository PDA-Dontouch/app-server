package donTouch.stock_server.kafka.service;

import donTouch.stock_server.kafka.dto.UsersDto;
import donTouch.stock_server.stock.dto.FindStocksReq;
import donTouch.stock_server.stock.dto.StockRes;
import donTouch.stock_server.stock.service.StockServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaService {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private StockServiceImpl stockService;

    @KafkaListener(topics = "user_response_test", groupId = "stock_group")
    public void getResponse(UsersDto data) {
        log.info("이게 가져온 데이터 : " + data.toString());
    }

    @KafkaListener(topics = "find_stocks_request", groupId = "stock_group")
    public void findStocks(FindStocksReq findStocksReq) {
        log.info(findStocksReq.getToken());
        List<StockRes> stockList = stockService.findStocks(findStocksReq);

        kafkaTemplate.send("find_stocks_response", "user_group", stockList);
    }
}
