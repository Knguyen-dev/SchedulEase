package com.knguyendev.api.domain.dto.User;

import com.knguyendev.api.domain.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


/**
 * An implementation of the UserDetails instance. This will be returned from our implementation of the UserDetailsService,
 * and it'll be the Principal. The Principal is then contained in the Authentication object, and the Authentication object
 * is contained by the Spring Security Context.
 * @param user
 */
public record UserDetailsImpl(UserEntity user) implements UserDetails {

    /**
     * Returns the UserEntity associated with the UserDetailsImpl instance
     *
     * @return The UserEntity linked with the UserDetailsImpl instance
     */
    public UserEntity getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Addressing the methods below:
     * We currently don't have plans to implement any an account
     * being expired, locked, enabled, etc.
     * <p>
     * So as a result, I think we're just going to default these to true so that they don't mess with things.
     * Spring Security's AuthenticationManager will use these methods to determine whether an account should be
     * authenticated in the case that it's found in the database. By defaulting these to
     * 'true', we ensure that the authentication proceeds as long as the credentials are good.
     */

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
