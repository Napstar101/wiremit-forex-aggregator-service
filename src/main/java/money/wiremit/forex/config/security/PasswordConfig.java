package money.wiremit.forex.config.security;

@Configuration
public class PasswordConfig {
    @Bean public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
}
