package com.perea.overheard.web.rest;

import com.perea.overheard.OverheardclubApp;
import com.perea.overheard.domain.Post;
import com.perea.overheard.domain.Comment;
import com.perea.overheard.domain.User;
import com.perea.overheard.domain.Topic;
import com.perea.overheard.repository.PostRepository;
import com.perea.overheard.service.PostService;
import com.perea.overheard.web.rest.errors.ExceptionTranslator;
import com.perea.overheard.service.dto.PostCriteria;
import com.perea.overheard.service.PostQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.perea.overheard.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PostResource} REST controller.
 */
@SpringBootTest(classes = OverheardclubApp.class)
public class PostResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PostQueryService postQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPostMockMvc;

    private Post post;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PostResource postResource = new PostResource(postService, postQueryService);
        this.restPostMockMvc = MockMvcBuilders.standaloneSetup(postResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createEntity(EntityManager em) {
        Post post = new Post()
            .title(DEFAULT_TITLE)
            .content(DEFAULT_CONTENT)
            .date(DEFAULT_DATE);
        return post;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createUpdatedEntity(EntityManager em) {
        Post post = new Post()
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .date(UPDATED_DATE);
        return post;
    }

    @BeforeEach
    public void initTest() {
        post = createEntity(em);
    }

    @Test
    @Transactional
    public void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPost.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testPost.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void createPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post with an existing ID
        post.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList
        restPostMockMvc.perform(get("/api/posts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }


    @Test
    @Transactional
    public void getPostsByIdFiltering() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        Long id = post.getId();

        defaultPostShouldBeFound("id.equals=" + id);
        defaultPostShouldNotBeFound("id.notEquals=" + id);

        defaultPostShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPostShouldNotBeFound("id.greaterThan=" + id);

        defaultPostShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPostShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPostsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where title equals to DEFAULT_TITLE
        defaultPostShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the postList where title equals to UPDATED_TITLE
        defaultPostShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllPostsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where title not equals to DEFAULT_TITLE
        defaultPostShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the postList where title not equals to UPDATED_TITLE
        defaultPostShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllPostsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultPostShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the postList where title equals to UPDATED_TITLE
        defaultPostShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllPostsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where title is not null
        defaultPostShouldBeFound("title.specified=true");

        // Get all the postList where title is null
        defaultPostShouldNotBeFound("title.specified=false");
    }
                @Test
    @Transactional
    public void getAllPostsByTitleContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where title contains DEFAULT_TITLE
        defaultPostShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the postList where title contains UPDATED_TITLE
        defaultPostShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllPostsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where title does not contain DEFAULT_TITLE
        defaultPostShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the postList where title does not contain UPDATED_TITLE
        defaultPostShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }


    @Test
    @Transactional
    public void getAllPostsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content equals to DEFAULT_CONTENT
        defaultPostShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the postList where content equals to UPDATED_CONTENT
        defaultPostShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllPostsByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content not equals to DEFAULT_CONTENT
        defaultPostShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the postList where content not equals to UPDATED_CONTENT
        defaultPostShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllPostsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultPostShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the postList where content equals to UPDATED_CONTENT
        defaultPostShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllPostsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content is not null
        defaultPostShouldBeFound("content.specified=true");

        // Get all the postList where content is null
        defaultPostShouldNotBeFound("content.specified=false");
    }
                @Test
    @Transactional
    public void getAllPostsByContentContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content contains DEFAULT_CONTENT
        defaultPostShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the postList where content contains UPDATED_CONTENT
        defaultPostShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllPostsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content does not contain DEFAULT_CONTENT
        defaultPostShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the postList where content does not contain UPDATED_CONTENT
        defaultPostShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }


    @Test
    @Transactional
    public void getAllPostsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where date equals to DEFAULT_DATE
        defaultPostShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the postList where date equals to UPDATED_DATE
        defaultPostShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllPostsByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where date not equals to DEFAULT_DATE
        defaultPostShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the postList where date not equals to UPDATED_DATE
        defaultPostShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllPostsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where date in DEFAULT_DATE or UPDATED_DATE
        defaultPostShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the postList where date equals to UPDATED_DATE
        defaultPostShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllPostsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where date is not null
        defaultPostShouldBeFound("date.specified=true");

        // Get all the postList where date is null
        defaultPostShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        Comment comment = CommentResourceIT.createEntity(em);
        em.persist(comment);
        em.flush();
        post.addComment(comment);
        postRepository.saveAndFlush(post);
        Long commentId = comment.getId();

        // Get all the postList where comment equals to commentId
        defaultPostShouldBeFound("commentId.equals=" + commentId);

        // Get all the postList where comment equals to commentId + 1
        defaultPostShouldNotBeFound("commentId.equals=" + (commentId + 1));
    }


    @Test
    @Transactional
    public void getAllPostsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        post.setUser(user);
        postRepository.saveAndFlush(post);
        Long userId = user.getId();

        // Get all the postList where user equals to userId
        defaultPostShouldBeFound("userId.equals=" + userId);

        // Get all the postList where user equals to userId + 1
        defaultPostShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllPostsByTopicIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        Topic topic = TopicResourceIT.createEntity(em);
        em.persist(topic);
        em.flush();
        post.setTopic(topic);
        postRepository.saveAndFlush(post);
        Long topicId = topic.getId();

        // Get all the postList where topic equals to topicId
        defaultPostShouldBeFound("topicId.equals=" + topicId);

        // Get all the postList where topic equals to topicId + 1
        defaultPostShouldNotBeFound("topicId.equals=" + (topicId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPostShouldBeFound(String filter) throws Exception {
        restPostMockMvc.perform(get("/api/posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));

        // Check, that the count call also returns 1
        restPostMockMvc.perform(get("/api/posts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPostShouldNotBeFound(String filter) throws Exception {
        restPostMockMvc.perform(get("/api/posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPostMockMvc.perform(get("/api/posts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePost() throws Exception {
        // Initialize the database
        postService.save(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post
        Post updatedPost = postRepository.findById(post.getId()).get();
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .date(UPDATED_DATE);

        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPost)))
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPost.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testPost.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Create the Post

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePost() throws Exception {
        // Initialize the database
        postService.save(post);

        int databaseSizeBeforeDelete = postRepository.findAll().size();

        // Delete the post
        restPostMockMvc.perform(delete("/api/posts/{id}", post.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
