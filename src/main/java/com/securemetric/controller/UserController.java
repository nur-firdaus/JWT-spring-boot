package com.securemetric.controller;

import com.securemetric.model.AuthRequest;
import com.securemetric.model.UserInfo;
import com.securemetric.service.JwtService;
import com.securemetric.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/new")
    public ResponseEntity<String> addNewUser(@RequestBody UserInfo userInfo) {

        if(userInfo == null || userInfo.getEmail()==null || userInfo.getName()==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error authenticating user");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.addUser(userInfo));
    }


    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        if(authRequest == null ||authRequest.getUsername() == null || authRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error authenticating user");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.OK).body(jwtService.generateToken(authRequest.getUsername()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error authenticating user");
        }


    }

}
