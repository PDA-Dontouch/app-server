package donTouch.energy_server.energy.service;

import donTouch.energy_server.energy.domain.EnergyFund;
import donTouch.energy_server.energy.domain.EnergyFundDetail;
import donTouch.energy_server.energy.domain.EnergyFundDetailJpaRepository;
import donTouch.energy_server.energy.domain.EnergyFundJpaRepository;
import donTouch.energy_server.energy.dto.BankCalculateForm;
import donTouch.energy_server.energy.dto.BuyEnergyFundForm;
import donTouch.energy_server.energy.dto.EnergyFundDto;
import donTouch.energy_server.energy.dto.HoldingEnergyFundDto;
import donTouch.energy_server.kafka.dto.BankAccountLogDto;
import donTouch.energy_server.kafka.dto.HoldingEnergyFundForm;
import donTouch.energy_server.kafka.service.KafkaProducerService;
import donTouch.energy_server.utils.EnergyFundMapper;
import donTouch.utils.utils.ApiUtils;
import donTouch.utils.utils.Sort;
import lombok.AllArgsConstructor;
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
public class EnergyFundServiceImplement implements EnergyFundService {

    private final EnergyFundJpaRepository energyRepository;
    private final EnergyFundDetailJpaRepository energyFundDetailRepository;
    private final EnergyFundMapper energyMapper = EnergyFundMapper.INSTANCE;
    private final RestTemplate restTemplate = new RestTemplate();
    private final KafkaProducerService kafkaProducerService;

//    private static String userUrl;
//    private static String holdingUrl;
//    private static WebClient webClient;
//
//    @Value("${USER_URL}")
//    public void setUserUrl(String userUrl) {
//        EnergyFundServiceImplement.userUrl = userUrl;
//    }
//
//    @Value("${HOLDING_URL}")
//    public void setHoldingUrl(String holdingUrl) {
//        EnergyFundServiceImplement.holdingUrl = holdingUrl;
//    }

    @Override
    public List<EnergyFundDto> getAllEnergyFund() {
        List<EnergyFund> energyFundList = energyRepository.findAll();
        if (energyFundList.isEmpty()) {
            throw new NullPointerException("EstateFund List is empty");
        }

        List<EnergyFundDto> energyFundDtoList = new ArrayList<>();
        energyFundList.forEach(energyFund -> {
            EnergyFundDto dto = energyMapper.toDto(energyFund);
            energyFundDtoList.add(dto);
        });

        energyFundDtoList.sort(Comparator.comparingInt(fund -> Sort.mapGrade(fund.getCreditRating())));

        return energyFundDtoList;
    }

    @Override
    public Boolean buyEnergyFund(BuyEnergyFundForm buyEnergyFundForm) {
        Long userId = buyEnergyFundForm.getUserId();
        String energyFundId = buyEnergyFundForm.getEnergyFundId();
        int inputCash = buyEnergyFundForm.getInputCash();
        String energyName = buyEnergyFundForm.getEnergyName();
        double energyEarningRate = buyEnergyFundForm.getEnergyEarningRate();

        EnergyFund findedEnergyFund = energyRepository.findById(energyFundId)
                .orElseThrow(() -> new NullPointerException("에너지 id 가 잘못되었습니다."));
        Long possibleInvest = (long) (findedEnergyFund.getFundingAmount() * 100000000 - findedEnergyFund.getSumOfInvestmentAndReservation());

        if (possibleInvest < inputCash) {
            throw new NullPointerException("투자 가능한 금액이 아닙니다.");
        }

        BankCalculateForm requestBody = new BankCalculateForm(buyEnergyFundForm.getUserId(), (long) inputCash * -1);
        ApiUtils.ApiResult result = restTemplate.postForEntity("http://15.165.74.129:8081" + "/api/user/bank/cal", requestBody, ApiUtils.ApiResult.class).getBody();
        System.out.println("result ======================== :" + result.getResponse());
        if (result.getResponse().equals("잔고가 부족합니다.")) {
            throw new NullPointerException("잔고가 부족합니다.");
        }
        if (result.getResponse().equals("계좌를 찾을 수 없습니다.")) {
            throw new NullPointerException("계좌를 찾을 수 없습니다.");
        }

        findedEnergyFund.setSumOfInvestmentAndReservation(findedEnergyFund.getSumOfInvestmentAndReservation() + buyEnergyFundForm.getInputCash());
        EnergyFund savedEnergyFund = energyRepository.save(findedEnergyFund);
        System.out.println("현재 투자 금액 =================== " + savedEnergyFund.getSumOfInvestmentAndReservation());

        EnergyFundDetail energyFundDetail = energyFundDetailRepository.findEnergyInfoByEnergyId(energyFundId)
                .orElseThrow(() -> new NullPointerException("해당 종목의 상세 정보를 찾을 수 없습니다."));

        String titleImageUrl = savedEnergyFund.getTitleImageUrl();
        int investmentPeriod = savedEnergyFund.getInvestmentPeriod();
        LocalDateTime startPeriod = energyFundDetail.getStartPeriod();

        kafkaProducerService.requestAddEnergy(new HoldingEnergyFundForm(userId, energyFundId, titleImageUrl, energyName, energyEarningRate, investmentPeriod, inputCash, startPeriod));
        kafkaProducerService.requestAddBankLog(new BankAccountLogDto(userId, (long) inputCash, 0, energyName, LocalDateTime.now()));
        return true;
    }

    @Override
    public Boolean sellEnergyFund(BuyEnergyFundForm buyEnergyFundForm) {
        Long userId = buyEnergyFundForm.getUserId();
        String energyFundId = buyEnergyFundForm.getEnergyFundId();
        int inputCash = buyEnergyFundForm.getInputCash();
        String estateName = buyEnergyFundForm.getEnergyName();
        double estateEarningRate = buyEnergyFundForm.getEnergyEarningRate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        EnergyFund findedEnergyFund = energyRepository.findById(energyFundId)
                .orElseThrow(() -> new NullPointerException("부동산 id 가 잘못되었습니다."));


        findedEnergyFund.setSumOfInvestmentAndReservation(findedEnergyFund.getSumOfInvestmentAndReservation() - buyEnergyFundForm.getInputCash());
        EnergyFund savedEnergyFund = energyRepository.save(findedEnergyFund);
        System.out.println("현재 투자 금액 =================== " + savedEnergyFund.getSumOfInvestmentAndReservation());


        EnergyFundDetail energyFundDetail = energyFundDetailRepository.findEnergyInfoByEnergyId(energyFundId)
                .orElseThrow(() -> new NullPointerException("해당 상품의 상세 정보가 없습니다."));

        String titleImageUrl = findedEnergyFund.getTitleImageUrl();
        int investmentPeriod = findedEnergyFund.getInvestmentPeriod();
        LocalDateTime startPeriod = energyFundDetail.getStartPeriod();
        HoldingEnergyFundForm requestBody = new HoldingEnergyFundForm(userId, energyFundId, titleImageUrl, estateName, estateEarningRate, investmentPeriod, inputCash, startPeriod);
        HttpEntity<HoldingEnergyFundForm> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<HoldingEnergyFundDto> responseEntity = restTemplate.postForEntity(
                "http://13.125.214.60:8085" + "/api/holding/energy/sell",
                requestEntity,
                HoldingEnergyFundDto.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            HoldingEnergyFundDto responseBody = responseEntity.getBody();

            BankCalculateForm requestBodyBank = new BankCalculateForm(responseBody.getUserId(), (long) responseBody.getInputCash());
            ApiUtils.ApiResult result = restTemplate.postForEntity("http://15.165.74.129:8081" + "/api/user/bank/cal", requestBodyBank, ApiUtils.ApiResult.class).getBody();
            if (result.getResponse().equals("잔고가 부족합니다.")) {
                throw new NullPointerException("입금이 되지 않았습니다.");
            }
            if (result.getResponse().equals("계좌를 찾을 수 없습니다.")) {
                throw new NullPointerException("계좌를 찾을 수 없습니다.");
            }

            kafkaProducerService.requestAddBankLog(new BankAccountLogDto(responseBody.getUserId(), (long) responseBody.getInputCash(), 1, responseBody.getTitle(), LocalDateTime.now()));
        } else {
            throw new NullPointerException("판매할 상품이 없습니다.");
        }

        return true;
    }

    @Override
    public List<EnergyFundDto> getEnergyFundDtoList(List<String> ids) {
        List<EnergyFund> energyFundList = energyRepository.findAllByEnergyIdIn(ids);

        List<EnergyFundDto> energyFundDtoList = new ArrayList<>();
        energyFundList.forEach(energyFund -> {
            EnergyFundDto dto = energyMapper.toDto(energyFund);
            energyFundDtoList.add(dto);
        });

        return energyFundDtoList;
    }
}
