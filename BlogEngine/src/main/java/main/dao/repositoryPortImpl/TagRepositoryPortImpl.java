package main.dao.repositoryPortImpl;

import main.dao.repository.TagRepository;
import main.domain.model.Tag;
import main.domain.port.TagRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class TagRepositoryPortImpl implements TagRepositoryPort {
    private final TagRepository tagRepository;

    public TagRepositoryPortImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Iterable<Tag> findTagsByNameIsStartingWith(String query) {
        return tagRepository.findTagsByNameIsStartingWith(query);
    }

    @Override
    public Iterable<Tag> findAll() {
        return tagRepository.findAll();
    }
}
