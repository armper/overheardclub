package club.overheard.web;

import club.overheard.config.CurrentUser;
import club.overheard.domain.User;
import club.overheard.dto.Dtos.*;
import club.overheard.service.CommentService;
import club.overheard.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {
    private final PostService posts;
    private final CommentService comments;

    public PostController(PostService posts, CommentService comments) {
        this.posts = posts;
        this.comments = comments;
    }

    private static User viewer(CurrentUser me) {
        return me == null ? null : me.user();
    }

    @GetMapping("/posts")
    public PageView<PostView> list(@RequestParam(defaultValue = "latest") String sort,
                                   @RequestParam(required = false) Long topicId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @AuthenticationPrincipal CurrentUser me) {
        return posts.list(sort, topicId, page, size, viewer(me));
    }

    @GetMapping("/posts/{id}")
    public PostView get(@PathVariable Long id, @AuthenticationPrincipal CurrentUser me) {
        return posts.get(id, viewer(me));
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostView create(@Valid @RequestBody PostRequest req, @AuthenticationPrincipal CurrentUser me) {
        return posts.create(req, me.user());
    }

    @PutMapping("/posts/{id}")
    public PostView update(@PathVariable Long id, @Valid @RequestBody PostRequest req, @AuthenticationPrincipal CurrentUser me) {
        return posts.update(id, req, me.user());
    }

    @DeleteMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal CurrentUser me) {
        posts.delete(id, me.user());
    }

    @PutMapping("/posts/{id}/reaction")
    public PostView react(@PathVariable Long id, @Valid @RequestBody ReactionRequest req, @AuthenticationPrincipal CurrentUser me) {
        return posts.react(id, req.type(), me.user());
    }

    @DeleteMapping("/posts/{id}/reaction")
    public PostView unreact(@PathVariable Long id, @AuthenticationPrincipal CurrentUser me) {
        return posts.unreact(id, me.user());
    }

    @GetMapping("/posts/{id}/comments")
    public List<CommentView> comments(@PathVariable Long id, @AuthenticationPrincipal CurrentUser me) {
        return comments.list(id, viewer(me));
    }

    @PostMapping("/posts/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentView comment(@PathVariable Long id, @Valid @RequestBody CommentRequest req, @AuthenticationPrincipal CurrentUser me) {
        return comments.add(id, req, me.user());
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id, @AuthenticationPrincipal CurrentUser me) {
        comments.delete(id, me.user());
    }
}
