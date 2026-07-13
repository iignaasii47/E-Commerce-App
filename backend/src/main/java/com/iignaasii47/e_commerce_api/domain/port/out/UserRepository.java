package com.iignaasii47.e_commerce_api.domain.port.out;

import com.iignaasii47.e_commerce_api.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
