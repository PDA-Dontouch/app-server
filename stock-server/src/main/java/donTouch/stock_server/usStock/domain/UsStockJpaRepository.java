package donTouch.stock_server.usStock.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsStockJpaRepository extends JpaRepository<UsStock, Integer> {
}
