package com.tgelo.security.xauth;


import com.tgelo.security.CredentialsDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XAuthTokenController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public XAuthTokenController(AuthenticationManager authenticationManager,
                                TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    private TokenProvider tokenProvider;
    private AuthenticationManager authenticationManager;

    /**
     * POST  /authenticate -> Authenticate user by username and password.
     */
    @RequestMapping(value = "api/authenticate",
        method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> authorize(@RequestBody CredentialsDTO credentialsDTO) {

        UsernamePasswordAuthenticationToken
            authenticationToken =
            new UsernamePasswordAuthenticationToken(credentialsDTO.getUsername(),
                credentialsDTO.getPassword());
        Authentication
            authentication =
            this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(credentialsDTO.getUsername());
        tokenProvider.store(token, authenticationToken);
        log.debug("User " + credentialsDTO.getUsername() + "authorised correctly");
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
