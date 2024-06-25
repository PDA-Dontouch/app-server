package donTouch.stock_server.krStock.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KrStockDetailJpaRepository extends JpaRepository<KrStockDetail, Integer> {
    Optional<KrStockDetail> findByKrStockId(Integer krStockId);

    Optional<KrStockDetail> findBySymbol(String symbol);
}
