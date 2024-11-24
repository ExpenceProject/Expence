package ug.edu.pl.server.infrastructure.security.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.CreateUserDto;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.AuthDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.LoginDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.RegisterUserDto;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Log
@Slf4j
@Validated
@EnableConfigurationProperties(TokenProperties.class)
public class AuthFacade {

    private final TokenProperties tokenProperties;
    private final AuthenticationManager authenticationManager;
    private final UserFacade userFacade;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey secretKey;

    AuthFacade(TokenProperties tokenProperties, AuthenticationManager authenticationManager, UserFacade userFacade, PasswordEncoder passwordEncoder) {
        this.tokenProperties = tokenProperties;
        this.authenticationManager = authenticationManager;
        this.userFacade = userFacade;
        this.passwordEncoder = passwordEncoder;
        secretKey = Keys.hmacShaKeyFor((Decoders.BASE64.decode(tokenProperties.secret())));
    }

    public AuthDto authenticateAndGenerateToken(@Valid LoginDto dto) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var jwt = generateToken(userDetails);

        return new AuthDto(jwt, userDetails.getUser());
    }

    @Transactional
    public UserDto register(@Valid RegisterUserDto dto) {
        var encodedPassword = passwordEncoder.encode(dto.password());
        var createUserDto = CreateUserDto.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .phoneNumber(dto.phoneNumber())
                .password(encodedPassword)
                .email(dto.email())
                .build();

        return userFacade.create(createUserDto);
    }

    // Email is our username
    String getUsernameFromToken(String authToken) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(authToken)
                .getPayload()
                .getSubject();
    }

    boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(authToken);

            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    private String generateToken(UserDetailsImpl userDetails) {
        var now = Instant.now();
        var expirationTime = now.plus(tokenProperties.expirationMs(), ChronoUnit.MILLIS);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .issuer(tokenProperties.issuer())
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }
}
