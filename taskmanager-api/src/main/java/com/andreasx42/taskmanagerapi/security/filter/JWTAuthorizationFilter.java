package com.andreasx42.taskmanagerapi.security.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.security.SecurityConstants;
import com.andreasx42.taskmanagerapi.security.manager.CustomUserDetails;
import com.andreasx42.taskmanagerapi.service.api.IUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

        private IUserService userService;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String header = request.getHeader("Authorization");

                if (header == null || !header.startsWith(SecurityConstants.BEARER_PREFIX)) {
                        filterChain.doFilter(request, response);
                        return;
                }

                String token = header.replace(SecurityConstants.BEARER_PREFIX, "");

                DecodedJWT jwt = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
                                .build()
                                .verify(token);

                String username = jwt.getSubject();

                String role = jwt.getClaim("role").asString();

                if (role == null) {
                        filterChain.doFilter(request, response);
                        return;
                }

                User user = userService.getByName(username);

                Set<SimpleGrantedAuthority> authority = Collections
                                .singleton(new SimpleGrantedAuthority(user.getRole().toString()));

                org.springframework.security.core.userdetails.User userDetails = new CustomUserDetails(user.getId(),
                                user.getUsername(), user.getPassword(),
                                authority);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                                authority);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);

        }

}
