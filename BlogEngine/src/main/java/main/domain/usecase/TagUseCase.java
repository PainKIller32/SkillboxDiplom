package main.domain.usecase;

import main.domain.model.Tag;
import main.domain.port.PostRepositoryPort;
import main.domain.port.TagRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.TreeSet;

@Component
public class TagUseCase {
    private final PostRepositoryPort postRepositoryPort;
    private final TagRepositoryPort tagRepositoryPort;

    public TagUseCase(PostRepositoryPort postRepositoryPort,
                      TagRepositoryPort tagRepositoryPort) {
        this.postRepositoryPort = postRepositoryPort;
        this.tagRepositoryPort = tagRepositoryPort;
    }

    public HashMap<Tag, Double> getTagsWithWeight(String query) {
        HashMap<Tag, Double> tagsWithWeight = new HashMap<>();
        Iterable<Tag> tags;
        if (query.isEmpty()) {
            tags = tagRepositoryPort.findAll();
        } else {
            tags = tagRepositoryPort.findTagsByNameIsStartingWith(query.trim());
        }
        if (!tags.iterator().hasNext()) {
            return tagsWithWeight;
        }
        TreeSet<Double> frequency = new TreeSet<>();
        for (Tag tag : tags) {
            frequency.add(tag.getFrequencyOfOccurrence());
        }
        double postCount = postRepositoryPort.getViewPostCount();
        double ratio = 1 / (frequency.last() / postCount);

        for (Tag tag : tags) {
            tagsWithWeight.put(tag, (tag.getFrequencyOfOccurrence() / postCount) * ratio);
        }
        return tagsWithWeight;
    }
}