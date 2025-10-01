package com.orb.gateway.auth.config.security;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RestAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Value("${orb.security.rsa.key-location}")
    private String rsaKeyLocation;

    public static final String[] PUBLIC_URIS = {
            "/api-docs/**",
            "/swagger-ui/**",
            "/error",
            "/oauth2/jwks",
            "/v1/auth/**"
    };

    @Qualifier("daoAuthenticationProvider")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenAuthenticationFilter tokenAuthenticationFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(handler -> handler
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler))
            .authorizeHttpRequests(
                (requests) -> requests
                .requestMatchers(PUBLIC_URIS).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public KeyPair keyPair() throws Exception {
        String keysDirectoryPath = rsaKeyLocation;
        File keysDirectory = new File(keysDirectoryPath);
        if (!keysDirectory.exists()) {
            keysDirectory.mkdirs();
        }

        String privateKeyPath = keysDirectoryPath + File.separator + "rsa_private.pem";
        String publicKeyPath = keysDirectoryPath + File.separator + "rsa_public.pem";

        File privateKeyFile = new File(privateKeyPath);
        File publicKeyFile = new File(publicKeyPath);

        KeyPair keyPair;

        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            // Load existing key pair
            try (FileReader privateKeyReader = new FileReader(privateKeyFile);
                 FileReader publicKeyReader = new FileReader(publicKeyFile)) {

                // Read private key
                PemReader pemReader = new PemReader(privateKeyReader);
                PemObject pemObject = pemReader.readPemObject();
                byte[] privateKeyBytes = pemObject.getContent();
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

                // Read public key
                pemReader = new PemReader(publicKeyReader);
                pemObject = pemReader.readPemObject();
                byte[] publicKeyBytes = pemObject.getContent();
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

                keyPair = new KeyPair(publicKey, privateKey);
                System.out.println("Loaded existing RSA KeyPair from files.");

            }
        } else {
            // Generate new key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // 2048-bit key size
            keyPair = keyPairGenerator.generateKeyPair();
            System.out.println("Generated new RSA KeyPair.");

            // Save private key to file
            try (FileWriter privateKeyWriter = new FileWriter(privateKeyFile);
                 FileWriter publicKeyWriter = new FileWriter(publicKeyFile)) {

                // Write private key
                PemWriter pemWriter = new PemWriter(privateKeyWriter);
                pemWriter.writeObject(new PemObject("RSA PRIVATE KEY", keyPair.getPrivate().getEncoded()));
                pemWriter.flush();

                // Write public key
                pemWriter = new PemWriter(publicKeyWriter);
                pemWriter.writeObject(new PemObject("RSA PUBLIC KEY", keyPair.getPublic().getEncoded()));
                pemWriter.flush();

                System.out.println("Saved new RSA KeyPair to files.");
            }
        }
        return keyPair;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("HEAD","GET", "POST", "PUT", "DELETE", "PATCH"));
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type", "Set-Cookie"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}