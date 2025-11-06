package tedxlcu.ticketing.payments.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import tedxlcu.ticketing.payments.security.user.AdminUserDetails;

@Service
@Component
public class JwtService {
  @Value("${security.jwt.secret}")
  private String jwtSecret;

  @Value("${security.jwt.expiration-ms}")
  private long jwtExpirationMs;

  public String generateToken(
    Authentication authentication
  ) {
    AdminUserDetails userPrincipal = (AdminUserDetails) authentication.getPrincipal();
    String role = userPrincipal.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .findFirst().orElseThrow(
        () -> new RuntimeException("User has no roles assigned")
      );

      long expirationInMillis = jwtExpirationMs * 1000;
    
      return Jwts.builder()
        .setSubject(userPrincipal.getUsername())
        .claim("id", userPrincipal.getId())
        .claim("role", role)
        .signWith(getSignInKey())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis()+ expirationInMillis))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token){
    return extractClaim(token, Claims::getSubject);
  }

  public boolean validateToken(String token){
    try {
      Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token);
      return true;
    } catch 
    (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) 
    {
      throw new JwtException(e.getMessage());
    }
  }

  public <T> T extractClaim(String token , Function<Claims, T> claimsResolver){
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token){
    return Jwts
      .parserBuilder()
      .setSigningKey(getSignInKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes); 
  }
}
