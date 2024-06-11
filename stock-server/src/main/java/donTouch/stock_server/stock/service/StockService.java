package donTouch.stock_server.stock.service;

import donTouch.stock_server.stock.dto.FindStocksReq;
import donTouch.stock_server.stock.dto.StockRes;

import java.util.List;

public interface StockService {
    public List<StockRes> findStocks(FindStocksReq findStocksReq);
}
