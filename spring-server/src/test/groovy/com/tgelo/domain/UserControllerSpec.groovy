package com.tgelo.domain

import com.tgelo.security.UserDetailsService
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class UserControllerSpec extends Specification {

    private UserRepository userRepository = Mock(UserRepository)
    private UserDetailsService detailsService = new UserDetailsService(userRepository)
    private MockMvc mockMvc = standaloneSetup(
            new UserController(
                    userRepository
            ))
            .build()

    def "Should return information about user"() {
        given:
            String userName = "admin";
            String password = "admin";
            User user = new User()
            user.setLogin(userName)
            user.setPassword(password)


            userRepository.findOneByLogin(userName) >> Optional.of(user)

        when:
            UserDetails userDetails = detailsService.loadUserByUsername(userName)
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, password);
            SecurityContextHolder.getContext().setAuthentication(authToken)

        then:
            mockMvc.perform(get("/api/user"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath($/$.login/$).value("admin"))
    }
}
