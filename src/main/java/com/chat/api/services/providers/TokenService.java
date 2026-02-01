package com.chat.api.services.providers;

import com.chat.api.configs.parameters.JwtParameter;
import com.chat.api.models.UserModel;
import com.chat.api.services.interfaces.ITokenService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {

    private final JwtParameter jwtParameter;

    public String generateRefreshToken(UserModel user) {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(this.genExpirationDateRefreshToken()))
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);

        try {
            signedJWT.sign(new MACSigner(jwtParameter.jwt().secret().getBytes()));
            return signedJWT.serialize();
        } catch (KeyLengthException e) {
            log.error("Error the assign refresh token! Error: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao assinar o refresh token.");
        }
    }

    public String generateToken(UserModel user) {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(this.genExpirationDate()))
                .claim("roles", user.getRoles())
                .jwtID(UUID.randomUUID().toString())
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.parse(jwtParameter.jwt().algorithm()));
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);

        try {
            signedJWT.sign(new MACSigner(jwtParameter.jwt().secret().getBytes()));
            return signedJWT.serialize();
        } catch (KeyLengthException e) {
            log.error("Error the assign token! Error: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao assinar o refresh token.");
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now()
                .plusDays(this.jwtParameter.exp().token())
                .toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant genExpirationDateRefreshToken() {
        return LocalDateTime.now()
                .plusDays(this.jwtParameter.exp().refresh())
                .toInstant(ZoneOffset.of("-03:00"));
    }

}
