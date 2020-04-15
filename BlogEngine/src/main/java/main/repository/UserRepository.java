package main.repository;

import main.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmailEqualsAndPasswordEquals(String email, String password);
    Optional<User> findByEmailEquals(String email);
    Optional<User> findByNameEquals(String name);
    Optional<User> findByCodeEquals(String code);
}