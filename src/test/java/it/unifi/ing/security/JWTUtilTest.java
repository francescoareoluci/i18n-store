package it.unifi.ing.security;

import io.jsonwebtoken.Claims;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JWTUtilTest {

    @Test
    public void testGetUsernameFromToken()
    {
        String token = JWTUtil.createJWT("1", "i18n-store", "m.r@example.com", "ADMIN", "en");

        assertEquals("m.r@example.com", JWTUtil.getUsernameFromToken(token));
    }

    @Test
    public void testGetRoleFromToken()
    {
        String token = JWTUtil.createJWT("1", "i18n-store", "m.r@example.com", "ADMIN", "en");

        assertEquals("ADMIN", JWTUtil.getUserRoleFromToken(token));
    }

    @Test
    public void testExtractClaimsFromToken()
    {
        String token = JWTUtil.createJWT("1", "i18n-store", "m.r@example.com", "ADMIN", "en");
        Claims claims = JWTUtil.extractClaimsFromToken(token);

        assertEquals("ADMIN", claims.get("userRole"));
        assertEquals("m.r@example.com", claims.get("subject"));
        assertEquals("i18n-store", claims.get("issuer"));
        assertEquals("en", claims.get("lang"));
    }
}
