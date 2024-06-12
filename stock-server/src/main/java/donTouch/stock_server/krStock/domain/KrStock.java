package donTouch.stock_server.krStock.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import donTouch.stock_server.stock.domain.Stock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "kr_stocks")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KrStock extends Stock {
    @Id
    @Column(name = "id")
    int id;
    String corpCode;
    LocalDateTime updatedDate;
}
