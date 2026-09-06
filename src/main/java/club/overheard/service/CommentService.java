package club.overheard.service;

import club.overheard.domain.Comment;
import club.overheard.domain.Post;
import club.overheard.domain.User;
import club.overheard.dto.Dtos.AuthorView;
import club.overheard.dto.Dtos.CommentRequest;
import club.overheard.dto.Dtos.CommentView;
import club.overheard.repo.CommentRepository;
import club.overheard.repo.PostRepository;
import club.overheard.web.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {
    private final CommentRepository comments;
    private final PostRepository posts;

    public CommentService(CommentRepository comments, PostRepository posts) {
        this.comments = comments;
        this.posts = posts;
    }

    @Transactional(readOnly = true)
    public List<CommentView> list(Long postId, User viewer) {
        if (!posts.existsById(postId)) {
            throw ApiException.notFound("Post");
        }
        return comments.findByPostIdOrderByCreatedAtAscIdAsc(postId).stream().map(c -> view(c, viewer)).toList();
    }

    public CommentView add(Long postId, CommentRequest req, User author) {
        Post post = posts.findById(postId).orElseThrow(() -> ApiException.notFound("Post"));
        Comment c = new Comment();
        c.setPost(post);
        c.setAuthor(author);
        c.setContent(req.content().trim());
        return view(comments.save(c), author);
    }

    public void delete(Long commentId, User actor) {
        Comment c = comments.findById(commentId).orElseThrow(() -> ApiException.notFound("Comment"));
        if (!canDelete(c, actor)) {
            throw ApiException.forbidden("Only the comment's author or an admin can delete it.");
        }
        comments.delete(c);
    }

    private static boolean canDelete(Comment c, User viewer) {
        return viewer != null && (viewer.isAdmin() || c.getAuthor().getId().equals(viewer.getId()));
    }

    private static CommentView view(Comment c, User viewer) {
        User a = c.getAuthor();
        return new CommentView(c.getId(), c.getContent(), new AuthorView(a.getId(), a.getDisplayName()), c.getCreatedAt(), canDelete(c, viewer));
    }
}
