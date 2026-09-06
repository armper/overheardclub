package club.overheard.service;

import club.overheard.domain.Topic;
import club.overheard.domain.User;
import club.overheard.dto.Dtos.AuthorView;
import club.overheard.dto.Dtos.TopicRequest;
import club.overheard.dto.Dtos.TopicView;
import club.overheard.repo.PostRepository;
import club.overheard.repo.TopicRepository;
import club.overheard.web.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TopicService {
    private final TopicRepository topics;
    private final PostRepository posts;

    public TopicService(TopicRepository topics, PostRepository posts) {
        this.topics = topics;
        this.posts = posts;
    }

    @Transactional(readOnly = true)
    public List<TopicView> list() {
        return topics.findAllWithPostCounts().stream()
            .map(row -> view((Topic) row[0], (Long) row[1]))
            .toList();
    }

    @Transactional(readOnly = true)
    public TopicView get(Long id) {
        Topic t = topics.findById(id).orElseThrow(() -> ApiException.notFound("Topic"));
        return view(t, posts.countByTopicId(id));
    }

    public TopicView create(TopicRequest req, User by) {
        String title = req.title().trim();
        if (topics.existsByTitleIgnoreCase(title)) {
            throw ApiException.conflict("A topic with that name already exists.");
        }
        Topic t = new Topic();
        t.setTitle(title);
        t.setCreatedBy(by);
        return view(topics.save(t), 0);
    }

    public TopicView rename(Long id, TopicRequest req, User actor) {
        Topic t = topics.findById(id).orElseThrow(() -> ApiException.notFound("Topic"));
        if (!actor.isAdmin() && !t.getCreatedBy().getId().equals(actor.getId())) {
            throw ApiException.forbidden("Only the topic's creator or an admin can rename it.");
        }
        String title = req.title().trim();
        if (!title.equalsIgnoreCase(t.getTitle()) && topics.existsByTitleIgnoreCase(title)) {
            throw ApiException.conflict("A topic with that name already exists.");
        }
        t.setTitle(title);
        return view(topics.save(t), posts.countByTopicId(id));
    }

    public void delete(Long id, User actor) {
        Topic t = topics.findById(id).orElseThrow(() -> ApiException.notFound("Topic"));
        if (!actor.isAdmin() && !t.getCreatedBy().getId().equals(actor.getId())) {
            throw ApiException.forbidden("Only the topic's creator or an admin can delete it.");
        }
        if (posts.countByTopicId(id) > 0) {
            throw ApiException.conflict("Move or delete the posts in this topic first.");
        }
        topics.delete(t);
    }

    private static TopicView view(Topic t, long count) {
        User by = t.getCreatedBy();
        return new TopicView(t.getId(), t.getTitle(), count, new AuthorView(by.getId(), by.getDisplayName()), t.getCreatedAt());
    }
}
