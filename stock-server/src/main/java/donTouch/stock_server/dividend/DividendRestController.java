package donTouch.stock_server.dividend;

import donTouch.stock_server.dividend.dto.DividendDTO;
import donTouch.stock_server.dividend.dto.DividendForm;
import donTouch.stock_server.dividend.service.DividendService;
import donTouch.stock_server.web.Web;
import donTouch.stock_server.web.dto.PurchaseInfoDTO;
import donTouch.utils.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/stocks/calendar")
public class DividendRestController {
    private final DividendService dividendService;

    @PostMapping("")
    public ApiUtils.ApiResult<List<DividendDTO>> findCalendar(@RequestBody @Valid DividendForm dividendForm) {
        Map<String, List<PurchaseInfoDTO>> holdingPurchases = Web.getHoldingStockPurchaseInfos(dividendForm.getUserId());

        List<DividendDTO> result = dividendService.findCalendar(dividendForm, holdingPurchases);

        if (result == null) {
            return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success(result);
    }
}
