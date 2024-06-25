package donTouch.order_server.holding;

import donTouch.order_server.bankAccount.dto.UserBankAccountLogDto;
import donTouch.order_server.bankAccount.service.BankAccountService;
import donTouch.order_server.holding.domain.HoldingKrStock;
import donTouch.order_server.holding.domain.HoldingUsStock;
import donTouch.order_server.holding.dto.*;
import donTouch.order_server.holding.service.*;
import donTouch.utils.utils.ApiUtils;
import donTouch.utils.utils.ApiUtils.ApiResult;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
public class HoldingRestController {
    private final HoldingEstateFundService holdingEstateFundService;
    private final HoldingKrStockService holdingKrStockService;
    private final HoldingUsStockService holdingUsStockService;
    private final HoldingEnergyFundService holdingEnergyFundService;
    private final BankAccountService bankAccountService;
    private final KrStockTradingLogService krStockTradingLogService;
    private final UsStockTradingLogService usStockTradingLogService;

    @GetMapping("/api/holding/account/{userId}")
    public ApiResult<List<UserBankAccountLogDto>> allBankLog(@PathVariable Long userId, @RequestParam int page, @RequestParam int size) {
        try {
            List<UserBankAccountLogDto> result = bankAccountService.getUserBankAccountLog(userId, page, size);
            if (result == null) {
                return ApiUtils.error("입출금 내역이 없습니다.", HttpStatus.NOT_FOUND);
            }
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/holding/allEstate/{userId}")
    public ApiResult<List<HoldingEstateFundDto>> allEstate(@PathVariable Long userId) {
        try {
            List<HoldingEstateFundDto> result = holdingEstateFundService.getAllEstate(userId);
            if (result == null) {
                return ApiUtils.error("보유 부동산 상품이 없습니다.", HttpStatus.NOT_FOUND);
            }
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/holding/allEnergy/{userId}")
    public ApiResult<List<HoldingEnergyFundDto>> allEnergy(@PathVariable Long userId) {
        try {
            List<HoldingEnergyFundDto> result = holdingEnergyFundService.getAllEnergy(userId);
            if (result == null) {
                return ApiUtils.error("보유 에너지 상품이 없습니다.", HttpStatus.NOT_FOUND);
            }
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/holding/estate/sell")
    public ResponseEntity<Object> findEstateAndDelete(@RequestBody HoldingEstateFundForm holdingEstateFundForm) {
        try {
            HoldingEstateFundDto result = holdingEstateFundService.findByUserIdAndEstateFundId(holdingEstateFundForm);
            return ResponseEntity.ok(result);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND));
        }
    }

    @PostMapping("/api/holding/sell/krStock")
    public ApiResult<Object> findByUserIdAndStockId(@RequestBody @Valid HoldingKrStockFindForm holdingKrStockFindForm) {
        try {
            HoldingKrStock result = holdingKrStockService.sellStockUpdate(holdingKrStockFindForm);
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/holding/energy/sell")
    public ResponseEntity<Object> findEnergyAndDelete(@RequestBody HoldingEnergyFundForm holdingEnergyFundForm) {
        try {
            HoldingEnergyFundDto result = holdingEnergyFundService.findByUserIdAndEnergyFundId(holdingEnergyFundForm);
            return ResponseEntity.ok(result);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND));
        }
    }

    @GetMapping("/api/holding/energy/totalCash/{userId}")
    public ApiResult<Integer> totalEnergyCash(@PathVariable Long userId) {
        try {
            Integer result = holdingEnergyFundService.getEnergyTotalCash(userId);
            if (result == null) {
                return ApiUtils.error("보유 주식이 없습니다.", HttpStatus.NOT_FOUND);
            }
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/holding/estate/totalCash/{userId}")
    public ApiResult<Integer> totalEstateCash(@PathVariable Long userId) {
        try {
            Integer result = holdingEstateFundService.getEstateTotalCash(userId);
            if (result == null) {
                return ApiUtils.error("보유 주식이 없습니다.", HttpStatus.NOT_FOUND);
            }
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/holding/energy/calendar")
    public ApiUtils.ApiResult<List<DividendP2PDto>> getEnergyCanlendar(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid CalendarReqForm calendarReqForm) {
        try {
            List<DividendP2PDto> result = holdingEnergyFundService.getEnergyDividend(calendarReqForm, token);
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/holding/estate/calendar")
    public ApiUtils.ApiResult<List<DividendP2PDto>> getEstateCanlendar(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid CalendarReqForm calendarReqForm) {
        try {
            List<DividendP2PDto> result = holdingEstateFundService.getEstateDividend(calendarReqForm, token);
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/api/holding/stocks")
    public ApiResult<Map<String, Object>> getHoldingStockIds(@RequestParam("userId") Long userId, @RequestParam("getPrice") Boolean getPrice) {
        Map<String, Object> result = new LinkedHashMap<>();

        List<String> holdingKrStockSymbols = holdingKrStockService.findHoldingStockIds(userId);
        List<String> holdingUsStockSymbols = holdingUsStockService.findHoldingStockIds(userId);

        if (holdingKrStockSymbols == null || holdingUsStockSymbols == null) {
            return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!getPrice) {
            result.put("krSymbols", holdingKrStockSymbols);
            result.put("usSymbols", holdingUsStockSymbols);
            return ApiUtils.success(result);
        }

        List<PurchaseInfoDTO> holdingKrStockPurchaseInfoDTOList = holdingKrStockService.findHoldingStockInfos(userId, holdingKrStockSymbols);
        List<PurchaseInfoDTO> holdingUsStockPurchaseInfoDTOList = holdingUsStockService.findHoldingStockInfos(userId, holdingUsStockSymbols);

        if (holdingKrStockPurchaseInfoDTOList == null || holdingUsStockPurchaseInfoDTOList == null) {
            return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        result.put("krHoldingStocks", holdingKrStockPurchaseInfoDTOList);
        result.put("usHoldingStocks", holdingUsStockPurchaseInfoDTOList);
        return ApiUtils.success(result);
    }

    @GetMapping("/api/holding/combination")
    public ApiResult<List<PurchasedStockDTO>> getCombinations(@RequestParam("userId") Long userId) {
        List<PurchasedStockDTO> purchasedStockDTOList = new ArrayList<>();
        purchasedStockDTOList.addAll(krStockTradingLogService.getPurchasedCombinationStocks(userId));
        purchasedStockDTOList.addAll(usStockTradingLogService.getPurchasedCombinationStocks(userId));
        return ApiUtils.success(purchasedStockDTOList);
    }

    @PostMapping("/api/holding/sell/usStock")
    public ApiResult<Object> findByUserIdAndStockIdUs(@RequestBody @Valid HoldingUsStockFindForm holdingUsStockFindForm) {
        try {
            HoldingUsStock result = holdingUsStockService.sellStockUpdate(holdingUsStockFindForm);
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/api/holding/krStock/hold")
    public ApiResult<String> addMyHoldingKrStock(@RequestBody @Valid MyHoldingForm myHoldingForm) {
        try {
            Long userId = myHoldingForm.getUserId();
            String code = myHoldingForm.getStockCode();
            int price = myHoldingForm.getStockPrice();
            int amount = myHoldingForm.getStockAmount();
            HoldingKrStock savedResult = holdingKrStockService.save(new HoldingKrStockDto(userId, code, amount));
            if (savedResult != null) {
                KrStockTradingLogDto entity = new KrStockTradingLogDto(userId, code, savedResult.getId(), price, amount, 0, 1);
                krStockTradingLogService.save(entity);
            }
            return ApiUtils.success("ok");
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/holding/usStock/hold")
    public ApiResult<String> addMyHoldingUsStock(@RequestBody @Valid MyHoldingForm myHoldingForm) {
        try {
            Long userId = myHoldingForm.getUserId();
            String code = myHoldingForm.getStockCode();
            int price = myHoldingForm.getStockPrice();
            int amount = myHoldingForm.getStockAmount();
            HoldingUsStock savedResult = holdingUsStockService.save(new HoldingUsStockDto(userId, code, amount));
            if (savedResult != null) {
                UsStockTradingLogDto entity = new UsStockTradingLogDto(userId, code, savedResult.getId(), price, amount, 0, 1);
                usStockTradingLogService.save(entity);
            }
            return ApiUtils.success("ok");
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
