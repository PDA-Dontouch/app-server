package donTouch.stock_server.usStock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "us_stock_details")
public class UsStockDetail {
    @Id
    @Column(name = "id")
    int id;
    @Column(name="us_stock_id")
    int UsStockId;
    String symbol;

    long marketCap;
    @Column(name="altman_z_score")
    double altmanZScore;
    @Column(name="piotroski_score")
    short piotroskiScore;

    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    LocalDateTime updatedDate;
    @Column(name = "dividend_payment_per_year")
    short dividendPaymentPerYear;

    @Column(name = "ten_y_revenue_growth_per_share")
    double tenRevenueGrowth;
    @Column(name = "five_y_revenue_growth_per_share")
    double fiveRevenueGrowth;
    @Column(name = "three_y_revenue_growth_per_share")
    double threeRevenueGrowth;

    @Column(name = "ten_y_operating_cf_growth_per_share")
    double tenOperatingCfGrowth;
    @Column(name = "five_y_operating_cf_growth_per_share")
    double fiveOperatingCfGrowth;
    @Column(name = "three_y_operating_cf_growth_per_share")
    double threeOperatingCfGrowth;

    @Column(name = "ten_y_net_income_growth_per_share")
    double tenIncomeGrowth;
    @Column(name = "five_y_net_income_growth_per_share")
    double fiveIncomeGrowth;
    @Column(name = "three_y_net_income_growth_per_share")
    double threeIncomeGrowth;

    @Column(name = "ten_y_shareholders_equity_growth_per_share")
    double tenShareholdersEquityGrowth;
    @Column(name = "five_y_shareholders_equity_growth_per_share")
    double fiveShareholdersEquityGrowth;
    @Column(name = "three_y_shareholders_equity_growth_per_share")
    double threeShareholdersEquityGrowth;

    @Column(name = "ten_y_dividend_per_share_growth_per_share")
    double tenDividendGrowth;
    @Column(name = "five_y_dividend_per_share_growth_per_share")
    double fiveDividendGrowth;
    @Column(name = "three_y_dividend_per_share_growth_per_share")
    double threeDividendGrowth;
}
