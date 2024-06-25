package donTouch.user_server.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import donTouch.user_server.oauth.dto.UserForTokenFormer;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    UserForTokenFormer user;
    String token;
}
