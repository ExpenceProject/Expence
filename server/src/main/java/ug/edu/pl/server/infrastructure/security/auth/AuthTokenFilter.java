package ug.edu.pl.server.infrastructure.security.auth;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
class AuthTokenFilter extends OncePerRequestFilter {

    private final AuthFacade authFacade;
    private final UserDetailsService userDetailsService;

    AuthTokenFilter(AuthFacade authFacade, UserDetailsService userDetailsService) {
        this.authFacade = authFacade;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        try {
            var jwt = parseToken(request);

            if (jwt != null && authFacade.validateToken(jwt)) {
                var username = authFacade.getUsernameFromToken(jwt);
                var userDetails = userDetailsService.loadUserByUsername(username);
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        var headerAuth = request.getHeader("Authorization");

        if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
            return null;
        }

        return headerAuth.substring(7);
    }
}
