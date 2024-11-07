package ug.edu.pl.server.infrastructure.security.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ug.edu.pl.server.Log;
import ug.edu.pl.server.domain.user.UserFacade;

@Log
class UserDetailsServiceImpl implements UserDetailsService {

    private final UserFacade userFacade;

    UserDetailsServiceImpl(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return UserDetailsImpl.build(userFacade.getByEmail(username));
    }
}
