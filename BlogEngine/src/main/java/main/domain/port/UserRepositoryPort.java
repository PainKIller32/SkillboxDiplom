package main.domain.port;

import main.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    Optional<User> findByCode(String code);

    Optional<User> findById(int id);

    void save(User user);
}