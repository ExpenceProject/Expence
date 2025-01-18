package ug.edu.pl.server.infrastructure.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.infrastructure.security.auth.AuthFacade;
import ug.edu.pl.server.infrastructure.security.auth.CurrentUserContext;
import ug.edu.pl.server.infrastructure.security.auth.dto.AuthDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.LoginDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.RegisterUserDto;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final AuthFacade authFacade;
    private final CurrentUserContext currentUserContext;

    AuthController(AuthFacade authFacade, CurrentUserContext currentUserContext) {
        this.authFacade = authFacade;
        this.currentUserContext = currentUserContext;
    }

    @PostMapping("/register")
    ResponseEntity<UserDto> register(@RequestBody RegisterUserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authFacade.register(dto));
    }

    @PostMapping("/login")
    ResponseEntity<AuthDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authFacade.authenticateAndGenerateToken(loginDto));
    }

    @GetMapping("/me")
    ResponseEntity<UserDto> me() {
        return ResponseEntity.ok(currentUserContext.getSignedInUser());
    }
}
