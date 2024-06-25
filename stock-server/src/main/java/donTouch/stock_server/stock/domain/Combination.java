package donTouch.stock_server.stock.domain;

import donTouch.stock_server.stock.dto.CombinationDTO;
import donTouch.stock_server.stock.dto.StockDTO;
import donTouch.utils.exchangeRate.ExchangeRate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Combination {
    StockDTO stockDTO;
    Integer quantity;

    public void addQuantity() {
        quantity++;
    }

    public int getDividendPerShareAndQuarter() {
        double dividendPerShare = stockDTO.getClosePrice() * stockDTO.getDividendYieldTtm();

        if (stockDTO.getExchange().equals("KSC")) {
            return (int) dividendPerShare / 4;
        }
        double exchangedDividendPerShare = ExchangeRate.USD.getSelling();
        return (int) exchangedDividendPerShare / 4;
    }

    public Long getTotalDividendPerQuarter() {
        return (long) getDividendPerShareAndQuarter() * quantity;
    }

    public long getAmount() {
        return (long) stockDTO.getClosePrice() * quantity;
    }

    public CombinationDTO convertToDTO() {
        return new CombinationDTO(stockDTO.getId(), stockDTO.getName(), stockDTO.getSymbol(), stockDTO.getExchange(), stockDTO.getDividendYieldTtm(), stockDTO.getClosePrice(), quantity, getTotalDividendPerQuarter());
    }
}
