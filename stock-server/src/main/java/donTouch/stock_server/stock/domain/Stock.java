package donTouch.stock_server.stock.domain;

import donTouch.stock_server.stock.dto.StockDTO;
import donTouch.utils.exchangeRate.ExchangeRate;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class Stock {
    @Id
    @Column(name = "id")
    int id;
    private String symbol;
    private String name;
    private String englishName;
    private String type;
    private String exchange;
    private Double closePrice;

    private Integer dividendMonth;
    private Double dividendYieldTtm;

    private Double safeScore;
    private Double growthScore;
    private Double dividendScore;

    private LocalDateTime updatedDate;

    public StockDTO convertToDTO(Integer userSafeScore, Integer userGrowthScore, Integer userDividendScore) {
        Double personalizedScore = safeScore * (double) userSafeScore * 4
                + growthScore * (double) userGrowthScore
                + dividendScore * (double) userDividendScore * 3;

        double krwClosePrice = closePrice;
        if (!exchange.equals("KSC")) {
            krwClosePrice *= ExchangeRate.USD.getBuying();
        }


        return new StockDTO(id, symbol, name, type, exchange, (int) krwClosePrice, dividendMonth, dividendYieldTtm, personalizedScore);
    }

    public StockDTO convertToDTO() {
        double krwClosePrice = closePrice;
        if (!exchange.equals("KSC")) {
            krwClosePrice *= ExchangeRate.USD.getBuying();
        }

        return new StockDTO(id, symbol, name, type, exchange, (int) krwClosePrice, dividendMonth, dividendYieldTtm, null);
    }
}
