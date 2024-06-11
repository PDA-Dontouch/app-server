package donTouch.user_server.userStock.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonSerialize
@JsonDeserialize
public class FindStocksReq {
    String token;

    @NotNull(message = "page를 입력하세요")
    @Min(0)
    int page;

    @NotNull(message = "size를 입력하세요")
    @Min(1)
    int size;
}
