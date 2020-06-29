package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.util.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public void saveUsers(List<User> users, Organization organization) {
        users.forEach(user -> {
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setOrganization(organization);
            Constants.ORGANIZATION_SCHEMA_MAP.put(user.getMobileNumber(), user.getOrganization().getName());
        });
        userRepository.saveAll(users);
    }
}
