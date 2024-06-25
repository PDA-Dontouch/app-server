package donTouch.user_server.user;

import donTouch.user_server.kafka.service.KafkaService;
import donTouch.user_server.oauth.domain.OauthServerType;
import donTouch.user_server.oauth.dto.LoginResponse;
import donTouch.user_server.oauth.dto.UserForTokenFormer;
import donTouch.user_server.oauth.service.OauthService;
import donTouch.user_server.user.dto.*;
import donTouch.user_server.user.service.BankAccountService;
import donTouch.user_server.user.service.UserService;
import donTouch.utils.utils.ApiUtils;
import donTouch.utils.utils.ApiUtils.ApiResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@Slf4j
public class UserRestController {
    private final OauthService oauthService;
    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final KafkaService kafkaService;

    @GetMapping("/test")
    public void test() {
        kafkaService.sendResponse();
    }

    @GetMapping("/api/user/{email}")
    public ApiResult<UsersDto> getUser(@PathVariable String email) {
        try {
            UsersDto user = userService.findUserByEmail(email);
            return ApiUtils.success(user);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/user/bank/{userId}")
    public ApiResult<BankAccountDto> getBank(@PathVariable Long userId) {
        try {
            BankAccountDto result = bankAccountService.getBank(userId);
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/user/bank/new")
    public ApiResult<String> createBankAccount(@RequestBody @Valid BankCreateForm bankCreateForm) {
        try {
            String result = bankAccountService.createBank(bankCreateForm);
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/user/account/cal")
    public ApiResult<BankAccountDto> calBankUserAccount(@RequestBody @Valid BankCalculateForm bankCalculateForm) {
        try {
            BankAccountDto result = bankAccountService.calculateAccountMoney(bankCalculateForm);
            if (result == null) {
                return ApiUtils.error("잔고가 부족합니다.", HttpStatus.BAD_REQUEST);
            }
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/user/bank/cal")
    public ApiResult<BankAccountDto> calBankAccount(@RequestBody @Valid BankCalculateForm bankCalculateForm) {
        try {
            BankAccountDto result = bankAccountService.calculateMoney(bankCalculateForm);
            if (result == null) {
                return ApiUtils.error("잔고가 부족합니다.", HttpStatus.BAD_REQUEST);
            }
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/api/user/oauth/login/{oauthServerType}")
    public ApiResult<LoginDto> login(
            @PathVariable OauthServerType oauthServerType,
            @RequestParam("code") String code
    ) {
        //LoginResponse loginUser = oauthService.login(oauthServerType, code);
        LoginDto loginUser = oauthService.login(oauthServerType, code);
        log.info(loginUser.toString());
        return ApiUtils.success(loginUser);
    }

    @SneakyThrows
    @GetMapping("/api/user/oauth/{oauthServerType}")
    public ApiResult<String> redirectAuthCodeRequestUrl(
            @PathVariable OauthServerType oauthServerType,
            HttpServletResponse response
    ) {
        String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthServerType);

        log.info(redirectUrl);
        response.sendRedirect(redirectUrl);
        return ApiUtils.success("토큰 요청 성공");
    }


    @PostMapping("/api/user/type")
    public ApiResult<UsersDto> saveUserInvestmentType(
            @RequestBody @Valid InvestmentTypeForm investmentTypeForm
    ) {
        try {
            UsersDto result = userService.updateInvestmentType(investmentTypeForm);
            return ApiUtils.success(result);
        } catch (NullPointerException e) {
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/user/token")
    public ApiResult<String> makeMyToken(
            @RequestBody @Valid UserForTokenFormer inputUser
    ){
        try{
            String newToken = oauthService.makeToken(inputUser);
            return ApiUtils.success(newToken);
        }catch(NullPointerException e){
            return ApiUtils.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/user/score")
    public ApiResult<ScoreDto> getUserScore(@RequestParam("userId") Long userId) {
        try {
            ScoreDto scoreDto = userService.findUserScore(userId);

            if (scoreDto == null) {
                return ApiUtils.error("server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ApiUtils.success(scoreDto);
        } catch (NullPointerException e) {
            return ApiUtils.error("check user id", HttpStatus.NOT_FOUND);
        }
    }
}
