package donTouch.user_server.oauth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserForTokenFormer {
    @NotNull(message = "id가 없습니다.")
    private Long id;
    @Email
    @NotBlank(message = "이메일이 없습니다.")
    private String email;
    @NotNull
    @Min(value = 1, message = "sns 타입 확인하세요.")
    @Max(value = 3, message = "sns 타입 확인하세요.")
    private Integer snsType;
    @NotBlank(message = "닉네임이 없습니다.")
    private String nickname;
    @Min(value = 1, message = "타입을 확인하세요")
    @Max(value = 5, message = "타입을 확인하세요")
    private Integer investmentType;
    @Min(value = 1, message = "스코어 확인하세요. ")
    @Max(value = 150,message = "스코어 확인하세요. ")
    private Integer safeScore;
    @Min(value = 1, message = "스코어 확인하세요. ")
    @Max(value = 150,message = "스코어 확인하세요. ")
    private Integer dividendScore;
    @Min(value = 1, message = "스코어 확인하세요. ")
    @Max(value = 150,message = "스코어 확인하세요. ")
    private Integer growthScore;

}
