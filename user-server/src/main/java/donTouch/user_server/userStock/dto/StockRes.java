package donTouch.user_server.userStock.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonDeserialize
public class StockRes {
    String symbol;
    String name;
    String type;
    String exchange;
    double dividendYieldTTM;
}
