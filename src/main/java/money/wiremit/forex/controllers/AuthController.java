package money.wiremit.forex.controllers;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final UserAccountRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuthenticationManager authMgr;

    public AuthController(UserAccountRepository users, PasswordEncoder encoder, JwtService jwt, AuthenticationManager authMgr){
        this.users=users; this.encoder=encoder; this.jwt=jwt; this.authMgr=authMgr;
    }

    @PostMapping("/signup")
    public void signup(@Valid @RequestBody SignupRequest req){
        if(users.existsByEmail(req.email())) throw new RuntimeException("Email already used");
        var u = new UserAccount();
        u.setEmail(req.email());
        u.setPasswordHash(encoder.encode(req.password()));
        users.save(u);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req){
        authMgr.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        return new AuthResponse(jwt.generate(req.email()));
    }
}
