package donTouch.stock_server.stock.domain;

import donTouch.stock_server.stock.dto.StockRes;
import jakarta.persistence.Column;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class Stock {
    @Column(unique = true)
    String symbol;

    String name;
    double safeScore;
    double growthScore;
    double dividendScore;
    short dividendMonth;

    double dividendRate;
    String type;
    String exchange;
    short dividendCount;

    @Column(name = "dividend_yield_TTM")
    double dividendYieldTTM;

    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    LocalDateTime updatedDate;

    public StockRes convertToStockRes() {
        return new StockRes(symbol, name, type, exchange, dividendYieldTTM);
    }
}
