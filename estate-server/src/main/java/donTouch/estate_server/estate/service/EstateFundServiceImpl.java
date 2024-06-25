package donTouch.estate_server.estate.service;

import donTouch.estate_server.estate.domain.EstateFund;
import donTouch.estate_server.estate.domain.EstateFundDetail;
import donTouch.estate_server.estate.domain.EstateFundDetailJpaRepository;
import donTouch.estate_server.estate.domain.EstateFundJpaRepository;
import donTouch.estate_server.estate.dto.BankCalculateForm;
import donTouch.estate_server.estate.dto.BuyEstateFundForm;
import donTouch.estate_server.estate.dto.EstateFundDto;
import donTouch.estate_server.estate.dto.HoldingEstateFundDto;
import donTouch.estate_server.estate.utils.EstateFundMapper;
import donTouch.estate_server.kafka.dto.BankAccountLogDto;
import donTouch.estate_server.kafka.dto.HoldingEstateFundForm;
import donTouch.estate_server.kafka.service.KafkaProducerService;
import donTouch.utils.utils.ApiUtils.ApiResult;
import donTouch.utils.utils.Sort;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@PropertySource(value = {"application.properties"})
public class EstateFundServiceImpl implements EstateFundService {

    private final EstateFundJpaRepository estateFundRepository;
    private final EstateFundDetailJpaRepository estateFundDetailRepository;
    private final EstateFundMapper estateFundMapper = EstateFundMapper.INSTANCE;
    private final RestTemplate restTemplate = new RestTemplate();
    private final KafkaProducerService kafkaProducerService;

    @Value("${USER_URL}")
    private static String USER_URL;
    @Value("${HOLDING_URL}")
    private static String HOLDING_URL;

    @Override
    public List<EstateFundDto> getAllEstateFund() {
        List<EstateFund> estateFundList = estateFundRepository.findAll();
        if (estateFundList.isEmpty()) {
            throw new NullPointerException("EstateFund List is empty");
        }
        List<EstateFundDto> estateFundDtoList = new ArrayList<>();
        estateFundList.forEach(estateFund -> {
            EstateFundDto dto = estateFundMapper.toDto(estateFund);
            estateFundDtoList.add(dto);
        });

        estateFundDtoList.sort(Comparator.comparingInt(fund -> Sort.mapGrade(fund.getEightCreditGrade())));

        return estateFundDtoList;
    }


    @Override
    public Boolean buyEstateFund(BuyEstateFundForm buyEstateFundForm) {
        Long userId = buyEstateFundForm.getUserId();
        int estateFundId = buyEstateFundForm.getEstateFundId();
        int inputCash = buyEstateFundForm.getInputCash();
        String estateName = buyEstateFundForm.getEstateName();
        double estateEarningRate = buyEstateFundForm.getEstateEarningRate();

        EstateFund findedEstateFund = estateFundRepository.findById(estateFundId)
                .orElseThrow(() -> new NullPointerException("부동산 id 가 잘못되었습니다."));
        Long possibleInvest = findedEstateFund.getTotalAmountInvestments() - findedEstateFund.getCurrentInvest();

        if (possibleInvest < inputCash) {
            throw new NullPointerException("투자 가능한 금액이 아닙니다.");
        }

        BankCalculateForm requestBody = new BankCalculateForm(buyEstateFundForm.getUserId(), (long) inputCash * -1);
        ApiResult result = restTemplate.postForEntity(USER_URL + "/api/user/bank/cal", requestBody, ApiResult.class).getBody();
        System.out.println("result ======================== :" + result.getResponse());
        if (result.getResponse().equals("잔고가 부족합니다.")) {
            throw new NullPointerException("잔고가 부족합니다.");
        }
        if (result.getResponse().equals("계좌를 찾을 수 없습니다.")) {
            throw new NullPointerException("계좌를 찾을 수 없습니다.");
        }

        findedEstateFund.setCurrentInvest(findedEstateFund.getCurrentInvest() + (long) buyEstateFundForm.getInputCash());
        EstateFund savedEstateFund = estateFundRepository.save(findedEstateFund);
        System.out.println("현재 투자 금액 =================== " + savedEstateFund.getCurrentInvest());

        EstateFundDetail estateFundDetail = estateFundDetailRepository.findByEstateId(estateFundId);
        String titleImageUrl = savedEstateFund.getTitleMainImageUrl();
        int investmentPeriod = savedEstateFund.getLength();
        LocalDateTime startPeriod = estateFundDetail.getStartDatetime();

        kafkaProducerService.requestAddEstate(new HoldingEstateFundForm(userId, estateFundId, titleImageUrl, estateName, estateEarningRate, investmentPeriod, inputCash, startPeriod));
        kafkaProducerService.requestAddBankLog(new BankAccountLogDto(userId, (long) inputCash, 0, estateName, LocalDateTime.now()));
        return true;
    }

    @Override
    public Boolean sellEstateFund(BuyEstateFundForm buyEstateFundForm) {
        Long userId = buyEstateFundForm.getUserId();
        int estateFundId = buyEstateFundForm.getEstateFundId();
        int inputCash = buyEstateFundForm.getInputCash();
        String estateName = buyEstateFundForm.getEstateName();
        double estateEarningRate = buyEstateFundForm.getEstateEarningRate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        EstateFund findedEstateFund = estateFundRepository.findById(estateFundId)
                .orElseThrow(() -> new NullPointerException("부동산 id 가 잘못되었습니다."));

        EstateFundDetail estateFundDetail = estateFundDetailRepository.findByEstateId(estateFundId);
        findedEstateFund.setCurrentInvest(findedEstateFund.getCurrentInvest() - (long) buyEstateFundForm.getInputCash());
        EstateFund savedEstateFund = estateFundRepository.save(findedEstateFund);
        System.out.println("현재 투자 금액 =================== " + savedEstateFund.getCurrentInvest());

        String titleImageUrl = findedEstateFund.getTitleMainImageUrl();
        int investmentPeriod = findedEstateFund.getLength();
        LocalDateTime startPeriod = estateFundDetail.getStartDatetime();
        HoldingEstateFundForm requestBody = new HoldingEstateFundForm(userId, estateFundId, titleImageUrl, estateName, estateEarningRate, investmentPeriod, inputCash, startPeriod);
        HttpEntity<HoldingEstateFundForm> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<HoldingEstateFundDto> responseEntity = restTemplate.postForEntity(
                HOLDING_URL + "/api/holding/estate/sell",
                requestEntity,
                HoldingEstateFundDto.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            HoldingEstateFundDto responseBody = responseEntity.getBody();

            BankCalculateForm requestBodyBank = new BankCalculateForm(responseBody.getUserId(), (long) responseBody.getInputCash());
            ApiResult result = restTemplate.postForEntity(USER_URL + "/api/user/bank/cal", requestBodyBank, ApiResult.class).getBody();
            if (result.getResponse().equals("잔고가 부족합니다.")) {
                throw new NullPointerException("입금이 되지 않았습니다.");
            }
            if (result.getResponse().equals("계좌를 찾을 수 없습니다.")) {
                throw new NullPointerException("계좌를 찾을 수 없습니다.");
            }

            kafkaProducerService.requestAddBankLog(new BankAccountLogDto(
                    responseBody.getUserId(),
                    (long) responseBody.getInputCash(), 1,
                    responseBody.getTitle(), LocalDateTime.now())
            );
        } else {
            throw new NullPointerException("판매할 상품이 없습니다.");
        }

        return true;
    }

    @Override
    public List<EstateFundDto> getEstateDtoList(List<Integer> ids) {
        List<EstateFund> estateFundList = estateFundRepository.findByIdIn(ids);

        List<EstateFundDto> estateFundDtoList = new ArrayList<>();
        estateFundList.forEach(estateFund -> {
            EstateFundDto dto = estateFundMapper.toDto(estateFund);
            estateFundDtoList.add(dto);
        });

        return estateFundDtoList;
    }
}
