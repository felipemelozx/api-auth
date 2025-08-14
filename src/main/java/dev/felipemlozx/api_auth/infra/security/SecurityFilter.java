package dev.felipemlozx.api_auth.infra.security;

import dev.felipemlozx.api_auth.dto.UserJwtDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  @Autowired
  TokenService tokenService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    var token = this.recoverToken(request);
    var decodedJWT = tokenService.validateToken(token);

    if(decodedJWT != null){
      String email = decodedJWT.getClaim("email").asString();
      String name = decodedJWT.getClaim("name").asString();
      Long id = decodedJWT.getClaim("id").asLong();
      List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
      UserJwtDTO userJwtDTO = new UserJwtDTO(id, name, email);
      var authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .toList();

      var authentication = new UsernamePasswordAuthenticationToken(userJwtDTO,null, authorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request){
    var authHeader = request.getHeader("Authorization");
    if(authHeader == null) return null;
    return authHeader.replace("Bearer ", "");
  }
}