package com.maxi.nutrition.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${api.secret.key}")
  private String KEYSECRET;

  @Value("${api.tokenExpirationTimeInMinutes}")
  private int tokenExpirationTimeInMinutes;


  public void addAuthentication(HttpServletResponse res, String username) {
    String token = Jwts.builder()
        .setSubject(username)
        .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationTimeInMinutes * 6000))
        .signWith(SignatureAlgorithm.HS512, KEYSECRET)
        .compact();
    res.addHeader("Authorization", token);
  }

  public String getUsernameFromToken(String token) {
    try {
      return Jwts.parser()
          .setSigningKey(KEYSECRET)
          .parseClaimsJws(token)
          .getBody()
          .getSubject();
    } catch (MalformedJwtException e) {
      return null;
    }
  }
}