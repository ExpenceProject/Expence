package ug.edu.pl.server.infrastructure.security.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.user.dto.CreateUserDto;
import ug.edu.pl.server.domain.user.dto.UserDto;
import ug.edu.pl.server.infrastructure.security.auth.AuthFacade;
import ug.edu.pl.server.infrastructure.security.auth.CurrentUserContext;
import ug.edu.pl.server.infrastructure.security.auth.dto.AuthDto;
import ug.edu.pl.server.infrastructure.security.auth.dto.LoginDto;

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
    ResponseEntity<UserDto> register(@RequestBody @Valid CreateUserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authFacade.register(dto));
    }

    @PostMapping("/login")
    ResponseEntity<AuthDto> login(@RequestBody @Valid LoginDto loginDto) {
        return ResponseEntity.ok(authFacade.authenticateAndGenerateToken(loginDto));
    }

    @GetMapping("/me")
    ResponseEntity<UserDto> me() {
        return ResponseEntity.ok(currentUserContext.getSignedInUser());
    }
}
