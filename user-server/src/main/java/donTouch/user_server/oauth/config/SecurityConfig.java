package donTouch.user_server.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
//        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
//                new DefaultOAuth2AuthorizationRequestResolver(
//                        clientRegistrationRepository,
//                        OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
//
//        authorizationRequestResolver.setAuthorizationRequestCustomizer(customizer ->
//                customizer.additionalParameters(params -> params.put("prompt", "none")));
//
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/user/**", "/login/**", "/error/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin((formLogin) -> formLogin.disable()) // formLogin.disable()
//              //  .oauth2Login(oauth2 -> oauth2.disable());
//                .logout((logoutConfig) -> logoutConfig.permitAll()) //  .logout().permitAll()// 로그아웃 아무나 못하게
//                // 사용할 필터와 시기 지정해주기
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider()),
//                        UsernamePasswordAuthenticationFilter.class);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
