package money.wiremit.forex.dtos;

import jakarta.validation.constraints.*;

public class AuthDtos {
    public record SignupRequest(@Email @NotBlank String email,
                                @Size(min=8) String password) {}
    public record LoginRequest(@Email @NotBlank String email,
                               @NotBlank String password) {}
    public record AuthResponse(String token) {}
}
