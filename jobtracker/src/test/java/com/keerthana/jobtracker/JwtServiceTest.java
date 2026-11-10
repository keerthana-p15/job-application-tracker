package com.keerthana.jobtracker;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "TestSecretKeyThatIsAtLeast32CharactersLong123";

    private final JwtService jwtService = new JwtService(SECRET, 60000);

    @Test
    void generatedToken_containsTheEmail() {
        String token = jwtService.generateToken("a@example.com");

        assertEquals("a@example.com", jwtService.extractEmail(token));
    }

    @Test
    void tokenWithWrongSignature_isRejected() {
        String token = jwtService.generateToken("a@example.com");
        String[] parts = token.split("\\.");
        String tampered = parts[0] + "." + parts[1] + ".invalidsignature";

        assertThrows(JwtException.class, () -> jwtService.extractEmail(tampered));
    }

    @Test
    void expiredToken_isRejected() {
        JwtService shortLived = new JwtService(SECRET, -1000);
        String token = shortLived.generateToken("a@example.com");

        assertThrows(JwtException.class, () -> jwtService.extractEmail(token));
    }

    @Test
    void tokenSignedWithAnotherSecret_isRejected() {
        JwtService other = new JwtService("AnotherDifferentSecretKeyWith32PlusChars456", 60000);
        String token = other.generateToken("a@example.com");

        assertThrows(JwtException.class, () -> jwtService.extractEmail(token));
    }
}