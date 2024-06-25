package donTouch.stock_server.stock;

import donTouch.stock_server.kafka.service.KafkaService;
import donTouch.stock_server.stock.dto.*;
import donTouch.stock_server.stock.service.StockService;
import donTouch.stock_server.web.Web;
import donTouch.stock_server.web.dto.LikeStockDTO;
import donTouch.stock_server.web.dto.PurchaseInfoDTO;
import donTouch.stock_server.web.dto.PurchasedStockDTO;
import donTouch.stock_server.web.dto.ScoreDto;
import donTouch.utils.exchangeRate.ExchangeRate;
import donTouch.utils.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/stocks")
public class StockRestController {
    private final KafkaService kafkaService;
    private final StockService stockService;

    @PostMapping("")
    public ApiUtils.ApiResult<List<StockDTO>> findStocks(@RequestBody @Valid FindStocksForm findStocksForm) {
        ScoreDto scoreDto = Web.getUserScore(findStocksForm.getUserId());

        List<StockDTO> stockDTOList = stockService.findStocks(findStocksForm, scoreDto);

        if (stockDTOList == null) {
            return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success(stockDTOList);
    }

    @PostMapping("/detail")
    public ApiUtils.ApiResult<Map<String, Object>> findStockDetail(@RequestBody @Valid FindStockDetailForm findStockDetailForm) {
        try {
            Map<String, Object> stockDetail = stockService.findStockDetail(findStockDetailForm);

            if (stockDetail == null) {
                return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ApiUtils.success(stockDetail);

        } catch (InstanceNotFoundException e) {
            return ApiUtils.error("stock detail is not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/combination/create")
    public ApiUtils.ApiResult<Map<String, Object>> findCombination(@Valid @RequestBody FindCombinationForm findCombinationForm) {
        try {
            ScoreDto scoreDto = Web.getUserScore(findCombinationForm.getUserId());

            Map<String, Object> combination = stockService.findCombination(findCombinationForm, scoreDto);

            if (combination == null) {
                return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ApiUtils.success(combination);
        } catch (NullPointerException e) {
            return ApiUtils.error("user not found", HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/combination/distribute")
    public ApiUtils.ApiResult<Map<String, Object>> distributeCombination(@Valid @RequestBody DistributeCombinationForm distributeCombinationForm) {
        try {
            Map<String, Object> distribution = stockService.distributeCombination(distributeCombinationForm);

            if (distribution == null) {
                return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ApiUtils.success(distribution);
        } catch (NullPointerException e) {
            return ApiUtils.error("price not found", HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return ApiUtils.error("combination should have 1 or more stocks", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/like")
    public ApiUtils.ApiResult<Map<String, Object>> findLikeStock(@RequestParam("userId") Long userId) {
        try {
            List<LikeStockDTO> likeStockDTOList = Web.getLikeStockDTOList(userId);
            if (likeStockDTOList == null) {
                return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> response = stockService.findLikeStocks(likeStockDTOList);
            if (response == null) {
                return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ApiUtils.success(response);
        } catch (IllegalStateException e) {
            return ApiUtils.error("get like stock id error", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/holding")
    public ApiUtils.ApiResult<Map<String, Object>> findHoldingStock(@RequestParam("userId") Long userId) {
        try {
            Map<String, List<PurchaseInfoDTO>> likeStockDTOList = Web.getHoldingStockPurchaseInfos(userId);

            if (likeStockDTOList == null) {
                return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> response = stockService.findHoldingStocks(likeStockDTOList);

            if (response == null) {
                return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ApiUtils.success(response);
        } catch (IllegalStateException e) {
            return ApiUtils.error("get like stock id error", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/combination/purchased")
    public ApiUtils.ApiResult<Object> findCombinationPurchased(@RequestParam("userId") Long userId, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        List<PurchasedStockDTO> purchasedStockDTOList = Web.getCombinationPurchased(userId);

        List<Map<String, Object>> response = stockService.findCombinationInfos(purchasedStockDTOList, page, size);

        if (response == null) {
            return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success(response);
    }

    @GetMapping("/exchange/usd")
    public ApiUtils.ApiResult<ExchangeRateDTO> findExchangeRate() {
        ExchangeRate usd = ExchangeRate.USD;
        return ApiUtils.success(new ExchangeRateDTO(usd.getCurrency(), usd.getBuying(), usd.getSelling()));
    }
}

