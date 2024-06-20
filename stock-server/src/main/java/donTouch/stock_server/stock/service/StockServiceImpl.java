package donTouch.stock_server.stock.service;

import donTouch.stock_server.krStock.domain.*;
import donTouch.stock_server.stock.domain.Combination;
import donTouch.stock_server.stock.domain.MonthDividend;
import donTouch.stock_server.stock.domain.Stock;
import donTouch.stock_server.stock.domain.StockPrice;
import donTouch.stock_server.stock.dto.*;
import donTouch.stock_server.usStock.domain.*;
import donTouch.utils.exchangeRate.ExchangeRate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {
    private final KrStockJpaRepository krStockJpaRepository;
    private final UsStockJpaRepository usStockJpaRepository;

    private final KrStockDetailJpaRepository krStockDetailJpaRepository;
    private final UsStockDetailJpaRepository usStockDetailJpaRepository;

    private final KrStockPriceJpaRepository krStockPriceJpaRepository;
    private final UsStockPriceJpaRepository usStockPriceJpaRepository;

    private final KrLatestCloseJpaRepository krLatestCloseJpaRepository;
    private final UsLatestCloseJpaRepository usLatestCloseJpaRepository;

    @Override
    public List<StockDTO> findStocks(FindStocksForm findStocksForm) {
        List<Stock> combinedStockList = getCombinedStockList(findStocksForm.getSearchWord(), findStocksForm.getDividendMonth());

        List<StockDTO> stockDTOList = getStockDTOList(combinedStockList, findStocksForm);

        stockDTOList.sort(Comparator.comparingDouble(StockDTO::getPersonalizedScore).reversed());

        return getPagedStockDTOList(stockDTOList, findStocksForm.getPage(), findStocksForm.getSize());
    }

    @Override
    public Map<String, Object> findStockDetail(FindStockDetailForm findStockDetailForm) throws InstanceNotFoundException {
        Map<String, Object> stockDetail = new LinkedHashMap<>();

        if (findStockDetailForm.getExchange().equals("KSC")) {
            Optional<KrStock> krStock = krStockJpaRepository.findById(findStockDetailForm.getStockId());
            if (krStock.isEmpty()) {
                throw new InstanceNotFoundException();
            }

            Optional<KrStockDetail> krStockDetail = krStockDetailJpaRepository.findByKrStockId(findStockDetailForm.getStockId());
            if (krStockDetail.isEmpty()) {
                throw new InstanceNotFoundException();
            }

            Stock stock = krStock.get();
            stockDetail.put("basic_info", stock.convertToDTO());
            stockDetail.put("detail_info", krStockDetail.get().convertToDTO());

            return stockDetail;
        }

        Optional<UsStock> usStock = usStockJpaRepository.findById(findStockDetailForm.getStockId());
        if (usStock.isEmpty()) {
            throw new InstanceNotFoundException();
        }

        Optional<UsStockDetail> usStockDetail = usStockDetailJpaRepository.findByUsStockId(findStockDetailForm.getStockId());
        if (usStockDetail.isEmpty()) {
            throw new InstanceNotFoundException();
        }

        Stock stock = usStock.get();
        stockDetail.put("basic_info", stock.convertToDTO());
        stockDetail.put("detail_info", usStockDetail.get().convertToDTO());

        return stockDetail;
    }

    @Override
    public Map<String, Object> findStockPrices(FindStockPricesForm findStockPricesForm) throws InstanceNotFoundException {
        Map<String, Object> response = new LinkedHashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(findStockPricesForm.getMonth());

        List<StockPrice> stockPriceList;

        if (findStockPricesForm.getExchange().equals("KSC")) {
            stockPriceList = new ArrayList<>(krStockPriceJpaRepository.findAllByKrStockIdAndDateGreaterThanEqual(findStockPricesForm.getStockId(), startDate));
        } else {
            stockPriceList = new ArrayList<>(usStockPriceJpaRepository.findAllByUsStockIdAndDateGreaterThanEqual(findStockPricesForm.getStockId(), startDate));
        }

        if (stockPriceList.isEmpty()) {
            throw new InstanceNotFoundException();
        }

        response.put("exchange", findStockPricesForm.getExchange());
        response.put("stock_id", findStockPricesForm.getStockId());
        response.put("symbol", stockPriceList.get(0).getSymbol());

        stockPriceList.sort(Comparator.comparing(StockPrice::getDate).reversed());
        response.put("prices", applyIntervalAndConvertToDTO(stockPriceList, findStockPricesForm.getInterval()));

        return response;
    }

    @Override
    public Map<String, Object> findCombination(FindCombinationForm findCombinationForm) {
        List<List<StockDTO>> fixedStockList = getFixedCombinations(findCombinationForm);

        List<List<Combination>> distirbutedStockList = distributeStock(fixedStockList, findCombinationForm.getInvestmentAmount());

        return convertToMap(distirbutedStockList);
    }

    @Override
    public Map<String, Object> distributeCombination(DistributeCombinationForm distributeCombinationForm) {
        if (countStocks(distributeCombinationForm) == 0) {
            throw new IllegalStateException();
        }

        List<List<StockDTO>> fixedStockList = convertToFixedStockList(distributeCombinationForm);
        List<List<Combination>> distirbutedStockList = distributeStock(fixedStockList, distributeCombinationForm.getInvestmentAmount());

        return convertToMap(distirbutedStockList);
    }

    @Override
    public Map<String, Object> findLikeStocks(List<LikeStockDTO> likeStockDTOList) {
        Map<String, Object> response = new LinkedHashMap<>();
        List<StockDTO> krStockDTOList = new ArrayList<>();
        List<StockDTO> usStockDTOList = new ArrayList<>();

        System.out.println("first get ex : " + likeStockDTOList.get(0).getExchange());
        for (LikeStockDTO likeStockDTO : likeStockDTOList) {
            if (likeStockDTO.getExchange().equals("KSC")) {
                Optional<KrStock> KrStock = krStockJpaRepository.findById(likeStockDTO.getStockId());

                if (KrStock.isPresent()) {
                    Stock stock = KrStock.get();
                    krStockDTOList.add(stock.convertToDTO());
                }
                continue;
            }

            Optional<UsStock> usStock = usStockJpaRepository.findById(likeStockDTO.getStockId());
            if (usStock.isPresent()) {
                Stock stock = usStock.get();
                usStockDTOList.add(stock.convertToDTO());
            }
        }

        response.put("krLikeStocks", krStockDTOList);
        response.put("usLikeStocks", usStockDTOList);

        return response;
    }

    int countStocks(DistributeCombinationForm distributeCombinationForm) {
        int cnt = 0;
        cnt += countStocksOfList(distributeCombinationForm.getCombination1());
        cnt += countStocksOfList(distributeCombinationForm.getCombination2());
        cnt += countStocksOfList(distributeCombinationForm.getCombination3());
        return cnt;
    }

    int countStocksOfList(List<FindStockDetailForm> findStockDetailFormList) {
        int cnt = 0;
        for (FindStockDetailForm findStockDetailForm : findStockDetailFormList) {
            if (findStockDetailForm.getExchange() != null && findStockDetailForm.getStockId() != null) {
                cnt++;
            }
        }
        return cnt;
    }

    List<List<StockDTO>> convertToFixedStockList(DistributeCombinationForm distributeCombinationForm) {
        List<List<StockDTO>> response = new ArrayList<>();
        response.add(createCombination(distributeCombinationForm.getCombination1()));
        response.add(createCombination(distributeCombinationForm.getCombination2()));
        response.add(createCombination(distributeCombinationForm.getCombination3()));
        return response;
    }

    List<StockDTO> createCombination(List<FindStockDetailForm> findStockDetailForm) {
        List<StockDTO> combination = new ArrayList<>();

        for (FindStockDetailForm findStockDetailForm1 : findStockDetailForm) {
            StockDTO stock = findStockAndConvertToStockDTO(findStockDetailForm1.getExchange(), findStockDetailForm1.getStockId());

            if (stock != null) {
                combination.add(stock);
            }
        }
        return combination;
    }

    StockDTO findStockAndConvertToStockDTO(String exchange, Integer stockId) {
        if (exchange == null || stockId == null) {
            return null;
        }

        if (exchange.equals("KSC")) {
            Optional<KrStock> foundKrStock = krStockJpaRepository.findById(stockId);
            if (foundKrStock.isEmpty()) {
                return null;
            }
            Stock krStock = foundKrStock.get();
            return krStock.convertToDTO();
        }

        Optional<UsStock> foundUsStock = usStockJpaRepository.findById(stockId);
        if (foundUsStock.isEmpty()) {
            return null;
        }
        Stock usStock = foundUsStock.get();
        return usStock.convertToDTO();
    }

    List<List<Combination>> distributeStock(List<List<StockDTO>> fixedStockList, Long investmentAmount) {
        List<List<Combination>> combinationDTOList = convertToCombinationDTO(fixedStockList);
        PriorityQueue<Combination> queueSortedByPrice = getQueueSortedByPrice(combinationDTOList);
        long boughtStockPrice = 0;
        long[] dividend = {-1, 0, 0, 0};

        while (!queueSortedByPrice.isEmpty()) {
            Combination combination = queueSortedByPrice.poll();

            if (dividend[combination.getStock().getDividendMonth()] > 0) {
                continue;
            }

            if (boughtStockPrice + combination.getPrice() > investmentAmount) {
                break;
            }

            combination.addQuantity();
            dividend[combination.getStock().getDividendMonth()] += combination.getDividendPerShareAndQuarter();

            boughtStockPrice += combination.getPrice();
        }

        PriorityQueue<MonthDividend> queueSortedByDividend = new PriorityQueue<>(new Comparator<MonthDividend>() {
            @Override
            public int compare(MonthDividend o1, MonthDividend o2) {
                return Long.compare(o1.getDividend(), o2.getDividend());
            }
        });

        for (int i = 1; i <= 3; i++) {
            if (dividend[i] > 0) {
                queueSortedByDividend.add(new MonthDividend(i, dividend[i]));
            }
        }

        while (true) {
            MonthDividend lowestDividendMonth = queueSortedByDividend.poll();
            List<Combination> combinationListToBuy = combinationDTOList.get(lowestDividendMonth.getMonth() - 1);
            Combination combinationToBuy = combinationListToBuy.get(0);

            if (combinationListToBuy.size() == 1 && boughtStockPrice + combinationToBuy.getPrice() > investmentAmount) {
                queueSortedByDividend.add(lowestDividendMonth);
                break;
            }

            if (combinationListToBuy.size() >= 2) {
                long amount0 = combinationListToBuy.get(0).getAmount();
                long amount1 = combinationListToBuy.get(1).getAmount();

                if (amount0 > amount1) {
                    combinationToBuy = combinationListToBuy.get(1);
                }
            }

            boughtStockPrice += combinationToBuy.getPrice();
            combinationToBuy.addQuantity();

            lowestDividendMonth.addDividend(combinationToBuy.getDividendPerShareAndQuarter());
            queueSortedByDividend.add(lowestDividendMonth);
        }

        return combinationDTOList;
    }

    PriorityQueue<Combination> getQueueSortedByPrice(List<List<Combination>> combinationDTOList) {
        PriorityQueue<Combination> pq = new PriorityQueue<>((o1, o2) -> Integer.compare(o1.getPrice(), o2.getPrice()));

        for (List<Combination> combinations : combinationDTOList) {
            pq.addAll(combinations);
        }

        return pq;
    }

    List<List<Combination>> convertToCombinationDTO(List<List<StockDTO>> fixedStockList) {
        List<List<Combination>> combinationDTOList = new ArrayList<>();

        for (List<StockDTO> stockDTOList : fixedStockList) {
            List<Combination> combination = new ArrayList<>();

            for (StockDTO stockDTO : stockDTOList) {
                combination.add(new Combination(stockDTO, getLatestClosePrice(stockDTO.getExchange(), stockDTO.getId()), 0));
            }
            combinationDTOList.add(combination);
        }
        return combinationDTOList;
    }

    int getLatestClosePrice(String exchange, Integer stockId) {
        if (exchange.equals("KSC")) {
            Optional<KrLatestClose> price = krLatestCloseJpaRepository.findByKrStockId(stockId);
            if (price.isPresent()) {
                double closePrice = price.get().getClose();
                return (int) closePrice;
            }

            throw new NullPointerException();
        }

        Optional<UsLatestClose> price = usLatestCloseJpaRepository.findByUsStockId(stockId);
        if (price.isPresent()) {
            double closePrice = price.get().getClose();
            double krwPrice = ExchangeRate.USD.getBuying() * closePrice;
            return (int) krwPrice;
        }

        throw new NullPointerException();
    }

    Map<String, Object> convertToMap(List<List<Combination>> distirbutedStockList) {
        Map<String, Object> response = new LinkedHashMap<>();
        int group = 1;

        for (List<Combination> combinationList : distirbutedStockList) {
            Map<String, Object> combinationInfo = new LinkedHashMap<>();
            List<CombinationDTO> combinationDTOList = new ArrayList<>();
            long totalDividend = 0;

            for (Combination combination : combinationList) {
                if (combination.getQuantity() > 0) {
                    combinationDTOList.add(combination.convertToDTO());
                    totalDividend += combination.getTotalDividendPerQuarter();
                }
            }

            combinationInfo.put("stocks", combinationDTOList);
            combinationInfo.put("totalDividend", totalDividend);

            response.put("combination" + group++, combinationInfo);
        }

        return response;
    }

    List<List<StockDTO>> getFixedCombinations(FindCombinationForm findCombinationForm) {
        List<List<StockDTO>> fixedStockList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            fixedStockList.add(findStocks(new FindStocksForm(null, i + 1, findCombinationForm.getSafeScore(), findCombinationForm.getGrowthScore(), findCombinationForm.getDividendScore(), 0, 2)));
        }

        double minScore = fixedStockList.get(0).get(0).getPersonalizedScore();
        for (int i = 1; i < fixedStockList.size(); i++) {
            if (minScore > fixedStockList.get(i).get(0).getPersonalizedScore()) {
                minScore = fixedStockList.get(i).get(0).getPersonalizedScore();
            }
        }

        for (List<StockDTO> stockDTOS : fixedStockList) {
            if (stockDTOS.get(1).getPersonalizedScore() < minScore) {
                stockDTOS.remove(1);
            }
        }
        return fixedStockList;
    }

    List<StockPriceDTO> applyIntervalAndConvertToDTO(List<StockPrice> stockPriceList, int interval) {
        List<StockPriceDTO> filteredStockPriceDTOList = new ArrayList<>();
        for (int i = 0; i < stockPriceList.size(); i++) {
            if (i % interval == 0) {
                StockPrice stockPrice = stockPriceList.get(i);
                filteredStockPriceDTOList.add(stockPrice.convertToDTO());
            }
        }
        return filteredStockPriceDTOList;
    }

    List<Stock> getCombinedStockList(String searchWord, Integer dividendMonth) {
        List<Stock> combinedStockList = new ArrayList<>();

        if (dividendMonth != null) {
            combinedStockList.addAll(krStockJpaRepository.findAllByDividendMonth(dividendMonth));
            combinedStockList.addAll(usStockJpaRepository.findAllByDividendMonth(dividendMonth));

            if (searchWord != null) {
                return combinedStockList.stream()
                        .filter(stock -> stock.getName().toLowerCase().contains(searchWord.toLowerCase())
                                || stock.getSymbol().toLowerCase().contains(searchWord.toLowerCase())
                                || stock.getEnglishName().toLowerCase().contains(searchWord.toLowerCase()))
                        .collect(Collectors.toList());
            }
            return combinedStockList;
        }

        if (searchWord != null) {
            combinedStockList.addAll(krStockJpaRepository.findDistinctBySymbolContainingOrNameContainingOrEnglishNameContaining(searchWord, searchWord, searchWord));
            combinedStockList.addAll(usStockJpaRepository.findDistinctBySymbolContainingOrNameContainingOrEnglishNameContaining(searchWord, searchWord, searchWord));
            return combinedStockList;
        }

        combinedStockList.addAll(krStockJpaRepository.findAll());
        combinedStockList.addAll(usStockJpaRepository.findAll());
        return combinedStockList;
    }

    List<StockDTO> getPagedStockDTOList(List<StockDTO> stockDTOList, int page, int size) {
        int start = size * page;
        int end = start + size;

        if (start >= stockDTOList.size()) {
            return List.of();
        }

        return stockDTOList.subList(start, Math.min(end, stockDTOList.size() - 1));
    }

    List<StockDTO> getStockDTOList(List<Stock> stockList, FindStocksForm findStocksForm) {
        return stockList.stream()
                .map(stock -> stock.convertToDTO(
                        findStocksForm.getSafeScore(), findStocksForm.getGrowthScore(), findStocksForm.getDividendScore()))
                .collect(Collectors.toList());
    }
}
