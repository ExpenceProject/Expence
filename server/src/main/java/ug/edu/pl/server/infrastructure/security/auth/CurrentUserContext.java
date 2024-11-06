package ug.edu.pl.server.infrastructure.security.auth;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import ug.edu.pl.server.domain.user.dto.UserDto;

public class CurrentUserContext {

    public UserDto getSignedInUser() {
        var context = SecurityContextHolder.getContext();
        if (context == null) {
            throw new AccessDeniedException("User details are unavailable or access denied");
        }

        var authentication = context.getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("User details are unavailable or access denied");
        }

        var principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetailsImpl userDetails)) {
            throw new AccessDeniedException("User details are unavailable or access denied");
        }

        if (userDetails.getUser() == null) {
            throw new AccessDeniedException("User details are unavailable or access denied");
        }

        return userDetails.getUser();
    }
}
