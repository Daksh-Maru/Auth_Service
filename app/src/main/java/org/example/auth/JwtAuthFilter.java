package org.example.auth;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.service.JwtService;
import org.example.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Automatically detected and registered as bean in Spring Component.
@AllArgsConstructor
@Data
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtService jwtService; // Creating instance of jwtService

    @Autowired
    private final UserDetailsServiceImp userDetailsServiceImp; // Creating instance of userDetailsServiceImp;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        String token = null; // this is to hold token from authHeader.
        String username = null; // this is to hold username from authHeader.
        if(authHeader != null && authHeader.startsWith("Bearer ")) { // Check if authorization header is present and starts with Bearer.
            token = authHeader.substring(7); // After "Bearer " we have index 7 till end which will get us the token.
            username = jwtService.extractUsername(token); // From the token we will get the username.
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // if username is not null and security context holder doesn't hold any authentication.
            UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(username); // get user Details.
            if(jwtService.validateToken(token, userDetails)) { // it validates the jwt token. once valid we provide with new authentication.
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                SecurityContext context = SecurityContextHolder.createEmptyContext(); // Creates an empty security context.
                context.setAuthentication(authenticationToken); // this context will store the authentication object.
                SecurityContextHolder.setContext(context); // this context is then set to Security Context Holder which holds authentication of current user.
            }
        }
        filterChain.doFilter(request,response);
    }
}
