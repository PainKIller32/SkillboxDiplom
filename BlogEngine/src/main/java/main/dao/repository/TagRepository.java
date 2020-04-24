package main.dao.repository;

import main.domain.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {
    Iterable<Tag> findTagsByNameIsStartingWith(String query);
}