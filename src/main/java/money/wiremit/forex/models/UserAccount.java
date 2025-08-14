package money.wiremit.forex.models;

import jakarta.persistence.*;
@Entity
@Table(name="users")
public class UserAccount {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true, nullable=false) private String email;
    @Column(nullable=false) private String passwordHash;
    private String role = "USER";
}
