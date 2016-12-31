package com.tgelo.security

import com.tgelo.domain.AuthoritiesConstants
import com.tgelo.domain.Authority
import com.tgelo.domain.User
import com.tgelo.domain.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification
import spock.lang.Subject

class UserDetailsServiceSpec extends Specification {

    UserRepository userRepository = Mock(UserRepository)

    @Subject
    UserDetailsService detailsService = new UserDetailsService(userRepository);

    def "Should return userDetails by username"() {
        given:
            String username = "admin"
            String password = "admin"

            User user = new User()
            user.login = username
            user.password = password

            Authority authority = new Authority()
            authority.name = AuthoritiesConstants.ADMIN
            user.authorities.add(authority)
        when:
            userRepository.findOneByLogin(username) >> Optional.of(user)
            UserDetails userDetails = detailsService.loadUserByUsername(username)
        then:
            userDetails.username == username
            userDetails.password == password
            userDetails.authorities.stream().filter { it -> (it.role == authority.name) }.count() == 1
    }

    def "Should throw exception when user doesn't exists in database"() {
        given:
            String username = "xyz"
        when:
            userRepository.findOneByLogin(username) >> Optional.empty()
            detailsService.loadUserByUsername(username)
        then:
            UsernameNotFoundException e = thrown()
            e.getMessage() == "User xyz was not found in the database"
    }
}
