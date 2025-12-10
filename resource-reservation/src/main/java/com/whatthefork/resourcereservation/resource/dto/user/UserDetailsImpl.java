package com.whatthefork.resourcereservation.resource.dto.user;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Log4j2
public class UserDetailsImpl implements UserDetails {

    private final String userId;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String userId, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        log.info("in UserDetailsImpl userId = {}", userId);
        return this.userId;
    }
}
