package main.repository;

import main.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {
    Iterable<Tag> findTagsByNameIsStartingWith(String query);
}
