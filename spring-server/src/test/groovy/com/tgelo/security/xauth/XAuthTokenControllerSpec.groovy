package com.tgelo.security.xauth

import com.google.gson.Gson
import com.tgelo.security.CredentialsDTO
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.nio.charset.Charset

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class XAuthTokenControllerSpec extends Specification {

    public static final MediaType TEXT_PLAIN_ISO = new MediaType(
            MediaType.TEXT_PLAIN.getType(),
            MediaType.TEXT_PLAIN.getSubtype(), Charset.forName("iso-8859-1"));

    private AuthenticationManager authenticationManager = Mock(AuthenticationManager)
    private TokenProvider tokenProvider = Mock(TokenProvider)

    private MockMvc mockMvc = standaloneSetup(
            new XAuthTokenController(
                    authenticationManager,
                    tokenProvider
            ))
            .build()

    def "Should return authorisation token"() {
        when:
            String userName = "admin"
            String password = "admin"

            CredentialsDTO credentials = new CredentialsDTO()
            credentials.username = userName
            credentials.password = password
            tokenProvider.createToken(userName) >> "admin:52b213a361ebd2cc1b6dc971b0c76766"
            Gson gson = new Gson();
            String credentialsAsJson = gson.toJson(credentials, CredentialsDTO.class)

        then:
            mockMvc.perform(post("/api/authenticate").content(credentialsAsJson)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(TEXT_PLAIN_ISO))
                    .andExpect(content().string("admin:52b213a361ebd2cc1b6dc971b0c76766"))


    }
}
