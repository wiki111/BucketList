package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository repository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.rawik.bucketlist.demo.model.User user = repository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("No user found with email " + email);
        }

        boolean enabled = true;
        boolean accountNotExpired = true;
        boolean credentialsNotExpired = true;
        boolean accountNotLocked = true;

        return new User(
                user.getEmail(),
                user.getPassword(),
                enabled,
                accountNotExpired,
                credentialsNotExpired,
                accountNotLocked,
                getAuthorities(user.getRole())
        );
    }


    private static List<GrantedAuthority> getAuthorities (String role){
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(role));

        return authorities;
    }
}
