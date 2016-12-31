package com.tgelo.security.xauth

import spock.lang.Specification
import spock.lang.Subject

class TokenProviderSpec extends Specification {

    @Subject
    private TokenProvider tokenProvider = new TokenProvider()

    def "Should compute signature when userName is filled"() {
        given:
            String userName = "admin"
        when:
            String token = tokenProvider.computeSignature(userName)
        then:
            token != null
            token.length() > 0
    }

    def "Should compute signature when userName is null"() {
        given:
            String userName
        when:
            String token = tokenProvider.computeSignature(userName)
        then:
            token != null
            token.length() > 0
    }

    def "Should compute signature when userName is filled with empty string"() {
        given:
            String userName = ""
        when:
            String token = tokenProvider.computeSignature(userName)
        then:
            token != null
            token.length() > 0
    }

    def "Should not throw exception when userName is null"() {
        given:
            String userName;
        when:
            tokenProvider.createToken(userName)
        then:
            notThrown Exception
    }

    def "Should return not null token when userName is null"() {
        given:
            String userName;
        when:
            String token = tokenProvider.createToken(userName)
        then:
            token != null
    }

    def "Should retrieve user name from proper token"() {
        given:
            String token = "admin:600af495f01c54a2adade8b3e73856d5"
        when:
            String userName = tokenProvider.getUserNameFromToken(token)
        then:
            userName == "admin"
    }

    def "Should throw TokenException when token is null"() {
        given:
            String token
        when:
            tokenProvider.getUserNameFromToken(token)
        then:
            TokenException e = thrown()
            e.getMessage() == "Token is empty"
    }

    def "Should throw TokenException when token has wrong format"() {
        given:
            String token = ":"
        when:
            tokenProvider.getUserNameFromToken(token)
        then:
            TokenException e = thrown()
            e.getMessage() == "Token has wrong format"
    }

    def "Should return false when cache doesn't contain token"() {
        given:
            String token = "admin:600af495f01c54a2adade8b3e73856d5"
        when:
            boolean result = tokenProvider.validateToken(token)
        then:
            !result
    }

}
