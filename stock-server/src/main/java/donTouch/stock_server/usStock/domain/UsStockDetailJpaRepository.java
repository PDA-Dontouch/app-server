package donTouch.stock_server.usStock.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsStockDetailJpaRepository extends JpaRepository<UsStockDetail, Integer> {
    Optional<UsStockDetail> findByUsStockId(Integer id);

    Optional<UsStockDetail> findBySymbol(String symbol);
}
