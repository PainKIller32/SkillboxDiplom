package main.domain.port;

import main.domain.model.Tag;

public interface TagRepositoryPort {
    Iterable<Tag> findTagsByNameIsStartingWith(String query);

    Iterable<Tag> findAll();
}
