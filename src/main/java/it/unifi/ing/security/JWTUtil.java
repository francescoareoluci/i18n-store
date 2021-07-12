package it.unifi.ing.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {

    private static final Logger logger = LogManager.getLogger(JWTUtil.class);

    private static final String key = "i18n-store-private-key";
    private static final long ttlMs = 600 * 1000;

    public static String createJWT(String id, String issuer, String subject, String role, String language) {

        // Signature algorithm (HMAC + SHA256)
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long actualMs = System.currentTimeMillis();
        Date now = new Date(actualMs);

        // Create the signing key
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // Create claims
        Map<String, Object> claimList = new HashMap<>();
        claimList.put("issuedAt", now);
        claimList.put("subject", subject);
        claimList.put("issuer", issuer);
        claimList.put("userRole", role);
        claimList.put("lang", language);

        // Set JWT claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setClaims(claimList)
                .signWith(signatureAlgorithm, signingKey);

        // Add the expiration
        long expMs = actualMs + ttlMs;
        Date expirationDate = new Date(expMs);
        builder.setExpiration(expirationDate);

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static Claims extractClaimsFromToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                .parseClaimsJws(token).getBody();
    }

    public static String getUsernameFromToken(String token)
    {
        try {
            // Validate the token
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                    .parseClaimsJws(token).getBody();

            return String.valueOf(claims.get("subject"));
        }
        catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static String getUserRoleFromToken(String token)
    {
        try {
            // Validate the token
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                    .parseClaimsJws(token).getBody();

            return String.valueOf(claims.get("userRole"));
        }
        catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static String extractBearerHeader(HttpHeaders headers)
    {
        // Get the HTTP Authorization header from the request
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Extract the token from the HTTP Authorization header
        return authorizationHeader.substring("Bearer".length()).trim();
    }

}