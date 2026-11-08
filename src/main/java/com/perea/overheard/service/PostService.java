package com.perea.overheard.service;

import com.perea.overheard.domain.Post;
import com.perea.overheard.domain.User;
import com.perea.overheard.repository.OverheardCommentRepository;
import com.perea.overheard.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Post}.
 */
@Service
@Transactional
public class PostService {

    private final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    private final OverheardCommentRepository overheardCommentRepository;

    public PostService(PostRepository postRepository, OverheardCommentRepository overheardCommentRepository) {
        this.postRepository = postRepository;
        this.overheardCommentRepository = overheardCommentRepository;
    }

    /**
     * Save a post.
     *
     * @param post the entity to save.
     * @return the persisted entity.
     */
    public Post save(Post post) {
        log.debug("Request to save Post : {}", post);
        return postRepository.save(post);
    }

    /**
     * Get all the posts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Post> findAll(Pageable pageable) {
        log.debug("Request to get all Posts");
        return postRepository.findAll(pageable);
    }

    /**
     * Get all the posts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Post> findAllWithEagerRelationships(Pageable pageable) {
        return postRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one post by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Post> findOne(Long id) {
        log.debug("Request to get Post : {}", id);
        return postRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Get the highest ranked posts published since a number of days ago.
     *
     * @param days how many days back to look.
     * @param pageable the pagination information.
     * @return the posts ordered by total reactions.
     */
    @Transactional(readOnly = true)
    public Page<Post> findTop(int days, Pageable pageable) {
        Instant since = Instant.now().minus(Duration.ofDays(Math.max(1, days)));
        return postRepository.findTopSince(since, pageable);
    }

    /**
     * Full text-ish search on post title and content.
     *
     * @param query the text to look for.
     * @param pageable the pagination information.
     * @return the matching posts, newest first.
     */
    @Transactional(readOnly = true)
    public Page<Post> search(String query, Pageable pageable) {
        return postRepository.search(query == null ? "" : query.trim(), pageable);
    }

    /**
     * Whether the given user already reacted to the post.
     *
     * @param postId the post id.
     * @param login the user login.
     * @return true if the user already reacted.
     */
    @Transactional(readOnly = true)
    public boolean hasUserReacted(Long postId, String login) {
        return postRepository.hasUserReacted(postId, login);
    }

    /**
     * Add a reaction from a user to a post. Each user can react once per post.
     *
     * @param post the post to react to.
     * @param user the reacting user.
     * @param reaction the reaction, from 1 to 5.
     * @return the updated post.
     */
    public Post react(Post post, User user, int reaction) {
        switch (reaction) {
            case 1:
                post.setRankOne(increment(post.getRankOne()));
                break;
            case 2:
                post.setRankTwo(increment(post.getRankTwo()));
                break;
            case 3:
                post.setRankThree(increment(post.getRankThree()));
                break;
            case 4:
                post.setRankFour(increment(post.getRankFour()));
                break;
            case 5:
                post.setRankFive(increment(post.getRankFive()));
                break;
            default:
                throw new IllegalArgumentException("Unknown reaction " + reaction);
        }
        post.addUserUprank(user);
        return postRepository.save(post);
    }

    private static Integer increment(Integer value) {
        return value == null ? 1 : value + 1;
    }

    /**
     * Delete the post by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Post : {}", id);
        overheardCommentRepository.deleteByPostId(id);
        postRepository.deleteById(id);
    }
}
