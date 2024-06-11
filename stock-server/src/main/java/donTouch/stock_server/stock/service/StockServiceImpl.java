package donTouch.stock_server.stock.service;

import donTouch.stock_server.krStock.domain.KrStock;
import donTouch.stock_server.krStock.domain.KrStockJpaRepository;
import donTouch.stock_server.stock.domain.Stock;
import donTouch.stock_server.stock.dto.FindStocksReq;
import donTouch.stock_server.stock.dto.StockRes;
import donTouch.stock_server.usStock.domain.UsStock;
import donTouch.stock_server.usStock.domain.UsStockJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {
    private KrStockJpaRepository krStockJpaRepository;
    private UsStockJpaRepository usStockJpaRepository;

    public List<StockRes> findStocks(FindStocksReq findStocksReq) {
        List<KrStock> krStockList = krStockJpaRepository.findAll();
        List<UsStock> usStockList = usStockJpaRepository.findAll();

        List<Stock> stockList = new ArrayList<>();
        stockList.addAll(krStockList);
        stockList.addAll(usStockList);

        // sort

        // paging
        int start = findStocksReq.getPage() * findStocksReq.getSize();
        int end = Math.min(start + findStocksReq.getSize(), stockList.size());

        return convertStockListToRes(stockList.subList(start, end));
    }

    List<StockRes> convertStockListToRes(List<Stock> stocks) {
        List<StockRes> stockResList = new ArrayList<>();

        for (Stock stock : stocks) {
            stockResList.add(stock.convertToStockRes());
        }

        return stockResList;
    }

}
