package donTouch.stock_server.stock.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StockRes {
    String symbol;
    String name;
    String type;
    String exchange;
    double dividendYieldTTM;
}
