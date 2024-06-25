package donTouch.user_server.oauth.service;

import donTouch.user_server.oauth.OauthMemberClientComposite;
import donTouch.user_server.oauth.config.JwtTokenProvider;
import donTouch.user_server.oauth.domain.AuthCodeRequestUrlProviderComposite;
import donTouch.user_server.oauth.domain.OauthMember;
import donTouch.user_server.oauth.domain.OauthMemberRepository;
import donTouch.user_server.oauth.domain.OauthServerType;
import donTouch.user_server.oauth.dto.LoginResponse;
import donTouch.user_server.oauth.dto.UserForTokenFormer;
import donTouch.user_server.user.domain.JpaUserRepository;
import donTouch.user_server.user.domain.Users;
import donTouch.user_server.user.dto.LoginDto;
import donTouch.user_server.user.dto.UsersDto;
import donTouch.user_server.user.utils.UsersMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class OauthServiceImpl implements OauthService, UserDetailsService {
    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final OauthMemberRepository oauthMemberRepository;
    private final JpaUserRepository jpaUserRepository;
    @Lazy
    private final JwtTokenProvider jwtTokenProvider;
    private final UsersMapper usersMapper = UsersMapper.INSTANCE;


    @Override
    public String getAuthCodeRequestUrl(OauthServerType oauthServerType) {
        return authCodeRequestUrlProviderComposite.provide(oauthServerType);
    }

    @Override
    public LoginDto login(OauthServerType oauthServerType, String authCode) {
        OauthMember oauthMember = oauthMemberClientComposite.fetch(oauthServerType, authCode);
        Users isExist = jpaUserRepository.findByEmail(oauthMember.email())
                .orElseGet(() -> {
                    Users newUser = Users.builder()
                            .email(oauthMember.email())
                            .nickname(oauthMember.nickname())
                            .snsType(oauthMember.snsType())
                            .investmentType(1)
                            .safeScore(0)
                            .dividendScore(0)
                            .growthScore(0)
                            .birthday(new Date())
                            .build();
                    jpaUserRepository.save(newUser);
                    return newUser;
                });

        UserForTokenFormer userForToken = new UserForTokenFormer(
                isExist.getId(),
                isExist.getEmail(),
                isExist.getSnsType(),
                isExist.getNickname(),
                isExist.getInvestmentType(),
                isExist.getSafeScore(),
                isExist.getDividendScore(),
                isExist.getGrowthScore()
        );

        String token = jwtTokenProvider.createToken(userForToken);
        return new LoginDto(userForToken,token);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> user = jpaUserRepository.findByEmail(email);
        return (UserDetails) user.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public String makeToken(UserForTokenFormer inputUser){
        String token = jwtTokenProvider.createToken(inputUser);
        //String token = "test";
        return token;
    }
}
