package donTouch.user_server.kafka.service;

import donTouch.user_server.kafka.dto.UsersDto;
import donTouch.user_server.userStock.dto.FindStocksReq;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaService {

    private KafkaTemplate<String, Object> kafkaTemplate;


    public void sendResponse() {
        kafkaTemplate.send("user_response_test", new UsersDto(1L, "123@naver.com", 1, new Date(), "Tomkid", 1, 1, 1, 1));
    }

    public void findStocks(FindStocksReq findStocksReq) {
        log.info("send kafka message");
        kafkaTemplate.send("find_stocks_request", "stock_group", findStocksReq);
    }

    @KafkaListener(topics = "find_stocks_response", groupId = "user_group")
    public void getResponseOfFindStocks(FindStocksReq findStocksReq) {

    }
}
