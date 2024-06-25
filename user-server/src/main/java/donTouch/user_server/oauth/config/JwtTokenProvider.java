package donTouch.user_server.oauth.config;

import donTouch.user_server.oauth.dto.UserForTokenFormer;
import donTouch.user_server.user.domain.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@PropertySource(value = {"application.properties"})
@Component
public class JwtTokenProvider {

    // 환경변수로 설정해서 불러오기
    @Value("${LOGIN_JWT_SECRET_KEY}")
    private String LOGIN_JWT_SECRET_KEY;
    private Key key;
    private long tokenValidTime = 30 * 60 * 1000L; // 30min

    private UserDetailsService userDetailsService;

    // 시크릿 키 초기화
    @PostConstruct
    protected void init() {
//        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        byte[] keyBytes = Decoders.BASE64URL.decode(LOGIN_JWT_SECRET_KEY);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String createToken(UserForTokenFormer user) {
        Claims claims = Jwts.claims();
        Date now = new Date();
        log.info("createToken - userNickname : " + user.getNickname());

        // Custom claims
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("snsType", user.getSnsType());
        claims.put("investmentType", user.getInvestmentType());
        claims.put("safeScore", user.getSafeScore());
        claims.put("dividendScore", user.getDividendScore());
        claims.put("growthScore", user.getGrowthScore());

        // header에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
        // payload에 들어갈 내용
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(key)
                .compact();
    }

    // 헤더에서 토큰 얻기
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }


    // 내가 만든 토큰인지 & username 기반으로 DB 에서 객체 가져오기
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }


    // 토큰에서 username 가져오기
    public String getUserEmail(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return claims.get("email", String.class);
    }
}

