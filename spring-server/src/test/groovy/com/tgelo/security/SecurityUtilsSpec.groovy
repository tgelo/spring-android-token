package com.tgelo.security

import com.tgelo.domain.User
import com.tgelo.domain.UserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification

class SecurityUtilsSpec extends Specification {

    UserRepository userRepository = Mock(UserRepository)
    UserDetailsService detailsService = new UserDetailsService(userRepository)

    def "Should return user name of current logged user"() {
        given:
            String userName = "admin"
            String password = "admin"

            User user = new User()
            user.setLogin(userName)
            user.setPassword(password)

            userRepository.findOneByLogin(userName) >> Optional.of(user)
        when:
            UserDetails userDetails = detailsService.loadUserByUsername(userName)
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, password);
            SecurityContextHolder.getContext().setAuthentication(authToken)
        then:
            userName == SecurityUtils.currentLoggedUserLogin
    }
}
