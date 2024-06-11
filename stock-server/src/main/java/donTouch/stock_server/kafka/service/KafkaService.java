package donTouch.stock_server.kafka.service;

import donTouch.stock_server.kafka.dto.UsersDto;
import donTouch.stock_server.stock.dto.FindStocksReq;
import donTouch.stock_server.stock.dto.StockRes;
import donTouch.stock_server.stock.service.StockServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public KafkaService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "user_response_test", groupId = "stock_group")
    public void getResponse(UsersDto data) {
        log.info("이게 가져온 데이터 : " + data.toString());
    }

    @KafkaListener(topics = "find_stocks", groupId = "stock_group")
    public void findStocks(FindStocksReq findStocksReq) {
        List<StockRes> stockList = stockService.findStocks(findStocksReq);
        log.info(findStocksReq.toString());
    }
}
