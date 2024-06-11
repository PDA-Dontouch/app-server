package donTouch.stock_server.stock.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize
public class FindStocksReq {
    String token;
    int page;
    int size;
}
