package donTouch.stock_server.krStock.domain;

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
@Table(name = "kr_stocks")
public class KrStock extends Stock {
    String corpCode;

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
//
//    double dividendRate;
//    String type;
//    String exchange;
//    short dividendCount;
//
//    @Column(name = "dividend_yield_TTM")
//    double dividendYieldTTM;
//
//    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
//    LocalDateTime updatedDate;
}
