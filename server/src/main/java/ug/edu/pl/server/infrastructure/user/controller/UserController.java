package ug.edu.pl.server.infrastructure.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UserDto;

@RestController
@RequestMapping("/api/users")
class UserController {

    private final UserFacade userFacade;

    UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/{id}")
    ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userFacade.getById(id));
    }

    @PreAuthorize("@currentUserContext.getSignedInUser().id() == #id")
    @PostMapping("/image/{id}")
    ResponseEntity<UserDto> uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        return ResponseEntity.ok(userFacade.uploadImage(id, file));
    }

    @PreAuthorize("@currentUserContext.getSignedInUser().id() == #id")
    @DeleteMapping("/image/{id}")
    ResponseEntity<UserDto> deleteImage(@PathVariable Long id) {
        return ResponseEntity.ok(userFacade.deleteImage(id));
    }
}
