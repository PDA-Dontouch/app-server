package donTouch.estate_server.estate;

import donTouch.estate_server.estate.domain.EstateFundDetail;
import donTouch.estate_server.estate.dto.BuyEstateFundForm;
import donTouch.estate_server.estate.dto.EstateFundDto;
import donTouch.estate_server.estate.service.DataAccessService;
import donTouch.estate_server.estate.service.EstateFundDetailService;
import donTouch.estate_server.estate.service.EstateFundService;
import donTouch.estate_server.utils.Web;
import donTouch.utils.utils.ApiUtils;
import donTouch.utils.utils.ApiUtils.ApiResult;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.util.List;

@AllArgsConstructor
@RestController
public class EstateRestController {
    private final EstateFundService estateFundService;
    private final EstateFundDetailService estateFundDetailService;
    private final DataAccessService dataAccessService;
    private final View error;

//    @GetMapping("/dataInit")
//    public void initData(){
//        dataAccessService.saveEstate();
//    }

    @GetMapping("/api/estates")
    public ApiResult<List<EstateFundDto>> getAllEstates() {
        try {
            List<EstateFundDto> resultList = estateFundService.getAllEstateFund();
            return ApiUtils.success(resultList);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/estates/{estateId}")
    public ApiResult<EstateFundDetail> getEstate(@PathVariable int estateId) {
        try {
            EstateFundDetail result = estateFundDetailService.findEstateInfoById(estateId);
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/estates/buy")
    public ApiResult<Boolean> buyEstate(@RequestBody BuyEstateFundForm buyEstateFundForm) {
        try {
            Boolean result = estateFundService.buyEstateFund(buyEstateFundForm);
            if (!result) {
                return ApiUtils.error("거래에 실패하였습니다.", HttpStatus.BAD_REQUEST);
            }
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/estates/sell")
    public ApiResult<Boolean> sellEstate(@RequestBody BuyEstateFundForm buyEstateFundForm) {
        try {
            Boolean result = estateFundService.sellEstateFund(buyEstateFundForm);
            if (result == false) {
                return ApiUtils.error("판매에 실패하였습니다.", HttpStatus.BAD_REQUEST);
            }
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/estates/like")
    public ApiResult<List<EstateFundDto>> getLikeEstates(@RequestParam Long userId) {
        try {
            List<Integer> likedEstateFundIds = Web.getLikeEstateFundIds(userId);
            if (likedEstateFundIds == null) {
                return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            List<EstateFundDto> estateFundDtoList = estateFundService.getEstateDtoList(likedEstateFundIds);
            if (estateFundDtoList == null) {
                return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ApiUtils.success(estateFundDtoList);
        } catch (Exception e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
