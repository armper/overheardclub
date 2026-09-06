package club.overheard.service;

import club.overheard.domain.*;
import club.overheard.dto.Dtos.*;
import club.overheard.repo.*;
import club.overheard.web.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class PostService {
    public static final int MAX_PAGE_SIZE = 50;

    private final PostRepository posts;
    private final TopicRepository topics;
    private final CommentRepository comments;
    private final ReactionRepository reactions;

    public PostService(PostRepository posts, TopicRepository topics, CommentRepository comments, ReactionRepository reactions) {
        this.posts = posts;
        this.topics = topics;
        this.comments = comments;
        this.reactions = reactions;
    }

    @Transactional(readOnly = true)
    public PageView<PostView> list(String sort, Long topicId, int page, int size, User viewer) {
        PageRequest pr = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE));
        Page<Post> result = "top".equals(sort)
            ? posts.findTop(Instant.now().minus(7, ChronoUnit.DAYS), topicId, pr)
            : posts.findLatest(topicId, pr);
        List<PostView> views = decorate(result.getContent(), viewer);
        return new PageView<>(views, result.getNumber(), result.getSize(), result.getTotalElements(), result.hasNext());
    }

    @Transactional(readOnly = true)
    public PostView get(Long id, User viewer) {
        Post p = posts.findWithDetailsById(id).orElseThrow(() -> ApiException.notFound("Post"));
        return decorate(List.of(p), viewer).get(0);
    }

    public PostView create(PostRequest req, User author) {
        Topic topic = topics.findById(req.topicId()).orElseThrow(() -> ApiException.notFound("Topic"));
        Post p = new Post();
        p.setTitle(req.title().trim());
        p.setContent(req.content().trim());
        p.setTopic(topic);
        p.setAuthor(author);
        p = posts.save(p);
        return get(p.getId(), author);
    }

    public PostView update(Long id, PostRequest req, User actor) {
        Post p = editable(id, actor);
        Topic topic = topics.findById(req.topicId()).orElseThrow(() -> ApiException.notFound("Topic"));
        p.setTitle(req.title().trim());
        p.setContent(req.content().trim());
        p.setTopic(topic);
        p.setUpdatedAt(Instant.now());
        posts.save(p);
        return get(id, actor);
    }

    public void delete(Long id, User actor) {
        Post p = editable(id, actor);
        reactions.deleteByPostId(id);
        comments.deleteByPostId(id);
        posts.delete(p);
    }

    /** Set (or change) the viewer's single reaction on a post. */
    public PostView react(Long postId, ReactionType type, User user) {
        Post p = posts.findById(postId).orElseThrow(() -> ApiException.notFound("Post"));
        Reaction r = reactions.findByPostIdAndUserId(postId, user.getId()).orElseGet(() -> {
            Reaction n = new Reaction();
            n.setPost(p);
            n.setUser(user);
            return n;
        });
        r.setType(type);
        reactions.save(r);
        return get(postId, user);
    }

    public PostView unreact(Long postId, User user) {
        reactions.findByPostIdAndUserId(postId, user.getId()).ifPresent(reactions::delete);
        return get(postId, user);
    }

    private Post editable(Long id, User actor) {
        Post p = posts.findWithDetailsById(id).orElseThrow(() -> ApiException.notFound("Post"));
        if (!canEdit(p, actor)) {
            throw ApiException.forbidden("Only the author or an admin can change this post.");
        }
        return p;
    }

    private static boolean canEdit(Post p, User viewer) {
        return viewer != null && (viewer.isAdmin() || p.getAuthor().getId().equals(viewer.getId()));
    }

    /** Attach reaction totals, the viewer's reaction and comment counts to a batch of posts. */
    private List<PostView> decorate(List<Post> batch, User viewer) {
        if (batch.isEmpty()) {
            return List.of();
        }
        List<Long> ids = batch.stream().map(Post::getId).toList();

        Map<Long, Map<ReactionType, Long>> totals = new HashMap<>();
        for (Object[] row : reactions.countByPostIds(ids)) {
            totals.computeIfAbsent((Long) row[0], k -> new EnumMap<>(ReactionType.class)).put((ReactionType) row[1], (Long) row[2]);
        }
        Map<Long, ReactionType> mine = new HashMap<>();
        if (viewer != null) {
            for (Object[] row : reactions.findMine(viewer.getId(), ids)) {
                mine.put((Long) row[0], (ReactionType) row[1]);
            }
        }
        Map<Long, Long> commentCounts = new HashMap<>();
        for (Object[] row : comments.countByPostIds(ids)) {
            commentCounts.put((Long) row[0], (Long) row[1]);
        }

        return batch.stream().map(p -> new PostView(
            p.getId(), p.getTitle(), p.getContent(),
            new AuthorView(p.getAuthor().getId(), p.getAuthor().getDisplayName()),
            new TopicRef(p.getTopic().getId(), p.getTopic().getTitle()),
            p.getCreatedAt(), p.getUpdatedAt(),
            totals.getOrDefault(p.getId(), Map.of()),
            mine.get(p.getId()),
            commentCounts.getOrDefault(p.getId(), 0L),
            canEdit(p, viewer))).toList();
    }
}
