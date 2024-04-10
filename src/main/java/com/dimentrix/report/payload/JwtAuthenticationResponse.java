package com.dimentrix.report.payload;

import java.util.Set;

public class JwtAuthenticationResponse {
    private String firstName;
    private String lastName;
    private String email;
    private Set roles;
    private String accessToken;
    private String tokenType = "Bearer";
    private String privilege;
    private String username;
    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public JwtAuthenticationResponse(
            String accessToken,
            String firstName,
            String lastName,
            String email,
            Set roles,
            String privilege,
            String username
    ){
        this.accessToken = accessToken;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
        this.privilege = privilege;
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set getRoles() {
        return roles;
    }

    public void setRoles(Set roles) {
        this.roles = roles;
    }

    public String getPrivilege() {
        return privilege;
    }

    public String getUsername() {
        return username;
    }
}

