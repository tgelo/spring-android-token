package com.tgelo.domain;

import com.tgelo.security.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(path = "api/user", method = RequestMethod.GET)
    public ResponseEntity<?> getUser() {
        return userRepository.findOneByLogin(SecurityUtils.getCurrentLoggedUserLogin())
            .map(u -> new ResponseEntity<User>(u,
                HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
