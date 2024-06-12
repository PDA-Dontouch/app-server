package donTouch.stock_server.krStock.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KrStockJpaRepository extends JpaRepository<KrStock, Integer> {
    List<KrStock> findByDividendMonth(int month);
}
