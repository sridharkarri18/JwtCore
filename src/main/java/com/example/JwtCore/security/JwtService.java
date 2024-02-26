package com.example.JwtCore.security;

import com.example.JwtCore.entity.Users;
import com.example.JwtCore.exceptions.UserDefinedException;
import com.example.JwtCore.model.requests.AccessTokenRequest;
import com.example.JwtCore.exceptions.CustomException;
import com.example.JwtCore.model.responses.RefreshTokenResponse;
import com.example.JwtCore.repositories.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);


    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JwtService.SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //RefreshToken creation starts from  here
    private int jwtRefreshExpirationMs = 86400000;

    public String generateJwtRefreshToken(Authentication authentication) {

        Users userPrincipal = (Users) authentication.getPrincipal();

        return Jwts.builder()
                .claim("id", userPrincipal.getId())
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public RefreshTokenResponse validateRefreshToken(AccessTokenRequest tokenRequest) throws CustomException, UserDefinedException {
        String refreshToken = tokenRequest.getRefreshToken();
        validateJwtToken(refreshToken, true);
        int id = getIdFromJwtToken(refreshToken);
        Users user = userRepository.findById(id).orElseThrow(() -> new UserDefinedException("User not found"));
        String JwtToken = generateToken(user.getUsername());
        return RefreshTokenResponse.builder()
                .token(JwtToken)
                .refreshtoken(refreshToken)
                .build();
    }

    public boolean validateJwtToken(String authToken, Boolean isRefreshToken) throws CustomException {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parse(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw isRefreshToken ? new CustomException("Invalid JWT token", HttpStatus.FORBIDDEN) : new CustomException("Invalid JWT token", HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw isRefreshToken ? new CustomException("JWT token is unsupported", HttpStatus.FORBIDDEN) : new CustomException("JWT token is unsupported", HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException expiredException) {
            logger.error("JWT token has expired: {}", expiredException.getMessage());
            throw isRefreshToken ? new CustomException("JWT token has expired", HttpStatus.FORBIDDEN) : new CustomException("JWT token has expired", HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            throw isRefreshToken ? new CustomException("JWT claims string is empty", HttpStatus.FORBIDDEN) : new CustomException("JWT claims string is empty", HttpStatus.UNAUTHORIZED);
        }
    }


    public int getIdFromJwtToken(String token) {
        final Claims claims = extractAllClaims(token);
        int id = (Integer) claims.get("id");
        return id;
    }


}
