package money.wiremit.forex.services.ifaces;

public interface JwtService {

    String generate(String subject);

    String validateAndGetSubject(String token);
}
