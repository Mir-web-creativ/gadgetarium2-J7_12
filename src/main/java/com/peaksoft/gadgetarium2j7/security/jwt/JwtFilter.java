package com.peaksoft.gadgetarium2j7.security.jwt;

import com.peaksoft.gadgetarium2j7.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    final String tokeHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    String username;
    String jwt;
    if(tokeHeader != null && tokeHeader.startsWith("Bearer ")){
        jwt = tokeHeader.substring(7);
        username  = jwtUtil.getUsernameFromToken(jwt);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails user = userDetailsService.loadUserByUsername(username);
            Collection<? extends GrantedAuthority> role = user.getAuthorities();
            if(jwtUtil.tokeIsValidation(jwt,user)){
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(user,null,role);
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
    }
    filterChain.doFilter(request,response);

    }
}
