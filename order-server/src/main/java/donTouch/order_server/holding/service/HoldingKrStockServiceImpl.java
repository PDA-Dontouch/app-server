package donTouch.order_server.holding.service;

import donTouch.order_server.holding.domain.HoldingKrStock;
import donTouch.order_server.holding.domain.HoldingKrStockJpaRepository;
import donTouch.order_server.holding.dto.HoldingKrStockDto;
import donTouch.order_server.holding.dto.HoldingKrStockFindForm;
import donTouch.order_server.utils.KrStockMapper;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Slf4j
@AllArgsConstructor
@Service
public class HoldingKrStockServiceImpl implements HoldingKrStockService {
    private final HoldingKrStockJpaRepository holdingKrStockRepository;
    private final KrStockMapper krStockMapper = KrStockMapper.INSTANCE;

    @Override
    public HoldingKrStock save(HoldingKrStockDto holdingKrStockDto) {
        Long userId = holdingKrStockDto.getUserId();
        String krStockId = holdingKrStockDto.getKrStockId();
        int orderAmount = holdingKrStockDto.getKrStockAmount();
        HoldingKrStock entity = krStockMapper.toEntity(holdingKrStockDto);

        Optional<HoldingKrStock> findHolding = holdingKrStockRepository.findByUserIdAndKrStockId(userId, krStockId);
        if (findHolding.isPresent()) {
            HoldingKrStock findEntity = findHolding.get();
            findEntity.setKrStockAmount(findEntity.getKrStockAmount() + orderAmount);
            return holdingKrStockRepository.save(findEntity);
        }else {
            return holdingKrStockRepository.save(entity);
        }
    }
    @Transactional
    @Override
    public HoldingKrStock sellStockUpdate(HoldingKrStockFindForm holdingKrStockFindForm) {
        Long userId = holdingKrStockFindForm.getUserId();
        String krStockId = holdingKrStockFindForm.getKrStockId();
        HoldingKrStock result = findHolding(userId, krStockId);

        int myAmount = result.getKrStockAmount();
        int orderAmount = holdingKrStockFindForm.getKrStockAmount();

        if (myAmount > orderAmount) {
            result.setKrStockAmount(myAmount - orderAmount);
            HoldingKrStock updateHolding = holdingKrStockRepository.save(result);
            System.out.println("이렇게 변했어요 : "+updateHolding);
            return updateHolding;
        } else if (myAmount == orderAmount) {
            holdingKrStockRepository.delete(result);
            result.setKrStockAmount(myAmount - orderAmount);
            return result;
        } else {
            throw new NullPointerException("주식 보유수량이 주문수량보다 적습니다.");
        }
    }
    public HoldingKrStock findHolding(Long userId, String krStockId) {
        return holdingKrStockRepository.findByUserIdAndKrStockId(userId, krStockId)
            .orElseThrow(()->new NullPointerException("해당 주식을 보유하고 있지 않습니다."));
    }
}