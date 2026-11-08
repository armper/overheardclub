package com.perea.overheard.web.rest;

import com.perea.overheard.domain.Post;
import com.perea.overheard.domain.User;
import com.perea.overheard.security.SecurityUtils;
import com.perea.overheard.service.PostService;
import com.perea.overheard.service.UserService;
import com.perea.overheard.web.rest.errors.BadRequestAlertException;
import com.perea.overheard.service.dto.PostCriteria;
import com.perea.overheard.service.PostQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.perea.overheard.domain.Post}.
 */
@RestController
@RequestMapping("/api")
public class PostResource {

    private final Logger log = LoggerFactory.getLogger(PostResource.class);

    private static final String ENTITY_NAME = "post";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostService postService;

    private final PostQueryService postQueryService;

    private final UserService userService;

    public PostResource(PostService postService, PostQueryService postQueryService, UserService userService) {
        this.postService = postService;
        this.postQueryService = postQueryService;
        this.userService = userService;
    }

    private User currentUser() {
        return userService.getUserWithAuthorities()
            .orElseThrow(() -> new AccessDeniedException("You must be signed in"));
    }

    /**
     * {@code POST  /posts} : Create a new post.
     *
     * @param post the post to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new post, or with status {@code 400 (Bad Request)} if the post has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) throws URISyntaxException {
        log.debug("REST request to save Post : {}", post);
        if (post.getId() != null) {
            throw new BadRequestAlertException("A new post cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (!SecurityUtils.isCurrentUserAdmin() || post.getUser() == null) {
            // Regular users always post as themselves, with fresh reactions.
            post.setUser(currentUser());
        }
        if (!SecurityUtils.isCurrentUserAdmin()) {
            post.setDate(Instant.now());
            post.setRankOne(0);
            post.setRankTwo(0);
            post.setRankThree(0);
            post.setRankFour(0);
            post.setRankFive(0);
            post.setUserUpranks(new HashSet<>());
        } else if (post.getDate() == null) {
            post.setDate(Instant.now());
        }
        Post result = postService.save(post);
        return ResponseEntity.created(new URI("/api/posts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /posts} : Updates an existing post.
     *
     * @param post the post to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated post,
     * or with status {@code 400 (Bad Request)} if the post is not valid,
     * or with status {@code 500 (Internal Server Error)} if the post couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/posts")
    public ResponseEntity<Post> updatePost(@Valid @RequestBody Post post) throws URISyntaxException {
        log.debug("REST request to update Post : {}", post);
        if (post.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Post existing = postService.findOne(post.getId())
            .orElseThrow(() -> new BadRequestAlertException("Post not found", ENTITY_NAME, "idnotfound"));
        if (!SecurityUtils.canModifyContentOf(existing.getUser().getLogin())) {
            throw new AccessDeniedException("You can only edit your own posts");
        }
        Post toSave = post;
        if (!SecurityUtils.isCurrentUserAdmin()) {
            // Owners may only change the text and the topic; reactions and authorship are server managed.
            existing.setTitle(post.getTitle());
            existing.setContent(post.getContent());
            existing.setTopic(post.getTopic());
            toSave = existing;
        }
        Post result = postService.save(toSave);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, post.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /posts} : get all the posts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of posts in body.
     */
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts(PostCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Posts by criteria: {}", criteria);
        Page<Post> page = postQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /posts/count} : count all the posts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/posts/count")
    public ResponseEntity<Long> countPosts(PostCriteria criteria) {
        log.debug("REST request to count Posts by criteria: {}", criteria);
        return ResponseEntity.ok().body(postQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /posts/top} : the most reacted posts of the last days.
     *
     * @param days how many days back to look.
     * @param page the page number.
     * @param size the page size.
     * @return the posts ordered by total reactions.
     */
    @GetMapping("/posts/top")
    public ResponseEntity<List<Post>> getTopPosts(
        @RequestParam(defaultValue = "7") int days,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("REST request to get top Posts of the last {} days", days);
        Page<Post> result = postService.findTop(days, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50)));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), result);
        return ResponseEntity.ok().headers(headers).body(result.getContent());
    }

    /**
     * {@code GET  /posts/search?q=} : search posts by title or content.
     *
     * @param q the text to search for.
     * @param page the page number.
     * @param size the page size.
     * @return the matching posts, newest first.
     */
    @GetMapping("/posts/search")
    public ResponseEntity<List<Post>> searchPosts(
        @RequestParam(defaultValue = "") String q,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.debug("REST request to search Posts for : {}", q);
        Page<Post> result = postService.search(q, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50)));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), result);
        return ResponseEntity.ok().headers(headers).body(result.getContent());
    }

    /**
     * {@code POST  /posts/:id/reactions/:reaction} : react to a post. Each user can react once per post.
     *
     * @param id the id of the post.
     * @param reaction the reaction, from 1 to 5.
     * @return the updated post, or {@code 400 (Bad Request)} if the user already reacted.
     */
    @PostMapping("/posts/{id}/reactions/{reaction}")
    public ResponseEntity<Post> reactToPost(@PathVariable Long id, @PathVariable int reaction) {
        log.debug("REST request to react {} to Post : {}", reaction, id);
        if (reaction < 1 || reaction > 5) {
            throw new BadRequestAlertException("Unknown reaction", ENTITY_NAME, "badreaction");
        }
        User user = currentUser();
        Optional<Post> post = postService.findOne(id);
        if (!post.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        if (postService.hasUserReacted(id, user.getLogin())) {
            throw new BadRequestAlertException("You already reacted to this post", ENTITY_NAME, "alreadyreacted");
        }
        return ResponseEntity.ok().body(postService.react(post.get(), user, reaction));
    }

    /**
     * {@code GET  /posts/:id} : get the "id" post.
     *
     * @param id the id of the post to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the post, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        log.debug("REST request to get Post : {}", id);
        Optional<Post> post = postService.findOne(id);
        return ResponseUtil.wrapOrNotFound(post);
    }

    /**
     * {@code DELETE  /posts/:id} : delete the "id" post.
     *
     * @param id the id of the post to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        log.debug("REST request to delete Post : {}", id);
        Optional<Post> post = postService.findOne(id);
        if (post.isPresent() && !SecurityUtils.canModifyContentOf(post.get().getUser().getLogin())) {
            throw new AccessDeniedException("You can only delete your own posts");
        }
        postService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
