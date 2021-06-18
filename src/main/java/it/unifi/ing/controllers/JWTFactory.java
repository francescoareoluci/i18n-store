package it.unifi.ing.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTFactory {

    public static final String key = "i18n-store-private-key";

    public static String createJWT(String id, String issuer, String subject, String role, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Map<String, Object> claimList = new HashMap<>();
        claimList.put("issuedAt", now);
        claimList.put("subject", subject);
        claimList.put("issuer", issuer);
        claimList.put("userRole", role);

        //Let's set the JWT Claims
        /*
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);
        */
        JwtBuilder builder = Jwts.builder().setId(id)
                .setClaims(claimList)
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
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
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
        }
    }

}