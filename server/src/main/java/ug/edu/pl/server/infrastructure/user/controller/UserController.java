package ug.edu.pl.server.infrastructure.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ug.edu.pl.server.domain.user.UserFacade;
import ug.edu.pl.server.domain.user.dto.UpdateUserDto;
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

    @PreAuthorize("@currentUserContext.getSignedInUser().id() == #id.toString()")
    @PostMapping("/image/{id}")
    ResponseEntity<Void> uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        userFacade.uploadImage(id, file);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@currentUserContext.getSignedInUser().id() == #id.toString()")
    @DeleteMapping("/image/{id}")
    ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        userFacade.deleteImage(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@currentUserContext.getSignedInUser().id() == #id.toString()")
    @PutMapping("/{id}")
    ResponseEntity<Void> updateEmail(@PathVariable Long id, @RequestBody UpdateUserDto dto) {
        userFacade.update(id, dto);
        return ResponseEntity.ok().build();
    }
}
