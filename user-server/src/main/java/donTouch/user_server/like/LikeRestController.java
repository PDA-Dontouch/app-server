package donTouch.user_server.like;

import donTouch.user_server.like.dto.LikeFundForm;
import donTouch.user_server.like.dto.LikeStockDTO;
import donTouch.user_server.like.dto.LikeStockForm;
import donTouch.user_server.like.service.LikeFundService;
import donTouch.user_server.like.service.LikeStockService;
import donTouch.utils.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user/like")
public class LikeRestController {
    LikeStockService likeStockService;
    LikeFundService likeFundService;

    @PostMapping("/stocks")
    public ApiUtils.ApiResult<LikeStockDTO> likeStock(@RequestBody @Valid LikeStockForm likeStockForm) {
        LikeStockDTO savedlikeStockDTO = likeStockService.likeStock(likeStockForm);

        if (savedlikeStockDTO == null) {
            return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success(savedlikeStockDTO);
    }

    @PostMapping("/energy")
    public ApiUtils.ApiResult<Map<String, String>> likeEnergyFund(@RequestBody @Valid LikeFundForm likeFundForm) {
        Map<String, String> savedLikeEnergyFund = likeFundService.likeEnergyFund(likeFundForm);

        if (savedLikeEnergyFund == null) {
            return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success(savedLikeEnergyFund);
    }

    @PostMapping("/estate")
    public ApiUtils.ApiResult<Map<String, Integer>> likeEstateFund(@RequestBody @Valid LikeFundForm likeFundForm) {
        Map<String, Integer> savedLikeEstateFund = likeFundService.likeEstateFund(likeFundForm);

        if (savedLikeEstateFund == null) {
            return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success(savedLikeEstateFund);
    }

    @GetMapping("/stocks")
    public ApiUtils.ApiResult<List<LikeStockDTO>> findLikeStocks(@RequestParam("userId") Long userId) {
        List<LikeStockDTO> likeStockDTOList = likeStockService.findLikeStocks(userId);

        if (likeStockDTOList == null) {
            return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success(likeStockDTOList);
    }

    @GetMapping("/energy")
    public ApiUtils.ApiResult<List<String>> findLikeEnergyFunds(@RequestParam("userId") Long userId) {
        List<String> likeStockDTOList = likeFundService.findLikeEnergyFunds(userId);

        if (likeStockDTOList == null) {
            return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success(likeStockDTOList);
    }

    @GetMapping("/estate")
    public ApiUtils.ApiResult<List<Integer>> findLikeEstateFunds(@RequestParam("userId") Long userId) {
        List<Integer> likeStockDTOList = likeFundService.findLikeEstateFunds(userId);

        if (likeStockDTOList == null) {
            return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success(likeStockDTOList);
    }

    @DeleteMapping("/stocks")
    public ApiUtils.ApiResult<String> deleteStock(@RequestBody @Valid LikeStockForm likeStockForm) {
        Boolean isDeleted = likeStockService.dislikeStock(likeStockForm);

        if (isDeleted == null || !isDeleted) {
            return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success("deleted completely");
    }

    @DeleteMapping("/energy")
    public ApiUtils.ApiResult<String> deleteEnergyFunds(@RequestBody @Valid LikeFundForm likeFundForm) {
        Boolean isDeleted = likeFundService.dislikeEnergyFund(likeFundForm);

        if (isDeleted == null || !isDeleted) {
            return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success("deleted completely");
    }

    @DeleteMapping("/estate")
    public ApiUtils.ApiResult<String> deleteEstateFunds(@RequestBody @Valid LikeFundForm likeFundForm) {
        Boolean isDeleted = likeFundService.dislikeEstateFund(likeFundForm);

        if (isDeleted == null || !isDeleted) {
            return ApiUtils.error("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiUtils.success("deleted completely");
    }
}
