package donTouch.user_server.oauth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {

    JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if(token != null) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 사용자 email 추출 및 SecurityContext에 저장
            String userEmail = jwtTokenProvider.getUserEmail(token);
            request.setAttribute("email", userEmail);
        }
        chain.doFilter(request, response);
    }
}