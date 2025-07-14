package com.example.druguseprevention.config;

import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.exception.exceptions.AuthenticationException;
import com.example.druguseprevention.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    TokenService tokenService;

    private final List<String> PUBLIC_API = List.of(
            "POST:/api/register",
            "POST:/api/login",
            "POST:/api/forgot-password",
            "POST:/api/reset-password",
            "GET:/v3/api-docs/**",
            "GET:/swagger-ui/**",
            "GET:/api/consultant/public/**",
            "GET:/api/public/all",
            "GET:/swagger-ui.html"
    );

    public boolean isPublicAPI(String uri, String method) {
        AntPathMatcher matcher = new AntPathMatcher();
        return PUBLIC_API.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) return false;
            String allowedMethod = parts[0];
            String allowedUri = parts[1];
            return allowedMethod.equalsIgnoreCase(method) && matcher.match(allowedUri, uri);
        });
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Cho phép tất cả OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method) || isPublicAPI(uri, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);

        if (token == null) {
            resolver.resolveException(request, response, null, new AuthenticationException("Empty token!") {});
            return;
        }

        try {
            User user = tokenService.extractAccount(token);

            UsernamePasswordAuthenticationToken authenToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authenToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenToken);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            resolver.resolveException(request, response, null, new AuthException("Expired Token!"));
        } catch (MalformedJwtException e) {
            resolver.resolveException(request, response, null, new AuthException("Invalid Token!"));
        }
    }
}