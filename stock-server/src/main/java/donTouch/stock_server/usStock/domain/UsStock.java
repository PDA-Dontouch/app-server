package donTouch.stock_server.usStock.domain;

import donTouch.stock_server.stock.domain.Stock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "us_stocks")
public class UsStock extends Stock {
    @Id
    @Column(name = "id")
    int id;
//
//    @Column(unique = true)
//    String symbol;
//
//    String name;
//    double safeScore;
//    double growthScore;
//    double dividendScore;
//    short dividendMonth;
//    double dividendRate;
//    String type;
//    String exchange;
//    short dividendCount;
//
//    @Column(name = "dividend_yield_TTM", columnDefinition = "TIMESTAMP")
//    double dividendYieldTTM;
//
//    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
//    LocalDateTime updatedDate;
}
