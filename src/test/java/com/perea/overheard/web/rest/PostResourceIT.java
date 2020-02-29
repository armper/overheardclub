package com.perea.overheard.web.rest;

import com.perea.overheard.OverheardclubApp;
import com.perea.overheard.domain.Post;
import com.perea.overheard.domain.OverheardComment;
import com.perea.overheard.domain.User;
import com.perea.overheard.domain.Topic;
import com.perea.overheard.repository.PostRepository;
import com.perea.overheard.service.PostService;
import com.perea.overheard.web.rest.errors.ExceptionTranslator;
import com.perea.overheard.service.dto.PostCriteria;
import com.perea.overheard.service.PostQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import java.util.ArrayList;
import java.util.List;

import static com.perea.overheard.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
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

    private static final Integer DEFAULT_RANK_ONE = 1;
    private static final Integer UPDATED_RANK_ONE = 2;
    private static final Integer SMALLER_RANK_ONE = 1 - 1;

    private static final Integer DEFAULT_RANK_TWO = 1;
    private static final Integer UPDATED_RANK_TWO = 2;
    private static final Integer SMALLER_RANK_TWO = 1 - 1;

    private static final Integer DEFAULT_RANK_THREE = 1;
    private static final Integer UPDATED_RANK_THREE = 2;
    private static final Integer SMALLER_RANK_THREE = 1 - 1;

    private static final Integer DEFAULT_RANK_FOUR = 1;
    private static final Integer UPDATED_RANK_FOUR = 2;
    private static final Integer SMALLER_RANK_FOUR = 1 - 1;

    private static final Integer DEFAULT_RANK_FIVE = 1;
    private static final Integer UPDATED_RANK_FIVE = 2;
    private static final Integer SMALLER_RANK_FIVE = 1 - 1;

    @Autowired
    private PostRepository postRepository;

    @Mock
    private PostRepository postRepositoryMock;

    @Mock
    private PostService postServiceMock;

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
            .date(DEFAULT_DATE)
            .rankOne(DEFAULT_RANK_ONE)
            .rankTwo(DEFAULT_RANK_TWO)
            .rankThree(DEFAULT_RANK_THREE)
            .rankFour(DEFAULT_RANK_FOUR)
            .rankFive(DEFAULT_RANK_FIVE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        post.setUser(user);
        // Add required entity
        Topic topic;
        if (TestUtil.findAll(em, Topic.class).isEmpty()) {
            topic = TopicResourceIT.createEntity(em);
            em.persist(topic);
            em.flush();
        } else {
            topic = TestUtil.findAll(em, Topic.class).get(0);
        }
        post.setTopic(topic);
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
            .date(UPDATED_DATE)
            .rankOne(UPDATED_RANK_ONE)
            .rankTwo(UPDATED_RANK_TWO)
            .rankThree(UPDATED_RANK_THREE)
            .rankFour(UPDATED_RANK_FOUR)
            .rankFive(UPDATED_RANK_FIVE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        post.setUser(user);
        // Add required entity
        Topic topic;
        if (TestUtil.findAll(em, Topic.class).isEmpty()) {
            topic = TopicResourceIT.createUpdatedEntity(em);
            em.persist(topic);
            em.flush();
        } else {
            topic = TestUtil.findAll(em, Topic.class).get(0);
        }
        post.setTopic(topic);
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
        assertThat(testPost.getRankOne()).isEqualTo(DEFAULT_RANK_ONE);
        assertThat(testPost.getRankTwo()).isEqualTo(DEFAULT_RANK_TWO);
        assertThat(testPost.getRankThree()).isEqualTo(DEFAULT_RANK_THREE);
        assertThat(testPost.getRankFour()).isEqualTo(DEFAULT_RANK_FOUR);
        assertThat(testPost.getRankFive()).isEqualTo(DEFAULT_RANK_FIVE);
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
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setTitle(null);

        // Create the Post, which fails.

        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setContent(null);

        // Create the Post, which fails.

        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setDate(null);

        // Create the Post, which fails.

        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
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
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].rankOne").value(hasItem(DEFAULT_RANK_ONE)))
            .andExpect(jsonPath("$.[*].rankTwo").value(hasItem(DEFAULT_RANK_TWO)))
            .andExpect(jsonPath("$.[*].rankThree").value(hasItem(DEFAULT_RANK_THREE)))
            .andExpect(jsonPath("$.[*].rankFour").value(hasItem(DEFAULT_RANK_FOUR)))
            .andExpect(jsonPath("$.[*].rankFive").value(hasItem(DEFAULT_RANK_FIVE)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllPostsWithEagerRelationshipsIsEnabled() throws Exception {
        PostResource postResource = new PostResource(postServiceMock, postQueryService);
        when(postServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restPostMockMvc = MockMvcBuilders.standaloneSetup(postResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restPostMockMvc.perform(get("/api/posts?eagerload=true"))
        .andExpect(status().isOk());

        verify(postServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllPostsWithEagerRelationshipsIsNotEnabled() throws Exception {
        PostResource postResource = new PostResource(postServiceMock, postQueryService);
            when(postServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restPostMockMvc = MockMvcBuilders.standaloneSetup(postResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restPostMockMvc.perform(get("/api/posts?eagerload=true"))
        .andExpect(status().isOk());

            verify(postServiceMock, times(1)).findAllWithEagerRelationships(any());
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
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.rankOne").value(DEFAULT_RANK_ONE))
            .andExpect(jsonPath("$.rankTwo").value(DEFAULT_RANK_TWO))
            .andExpect(jsonPath("$.rankThree").value(DEFAULT_RANK_THREE))
            .andExpect(jsonPath("$.rankFour").value(DEFAULT_RANK_FOUR))
            .andExpect(jsonPath("$.rankFive").value(DEFAULT_RANK_FIVE));
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
    public void getAllPostsByRankOneIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankOne equals to DEFAULT_RANK_ONE
        defaultPostShouldBeFound("rankOne.equals=" + DEFAULT_RANK_ONE);

        // Get all the postList where rankOne equals to UPDATED_RANK_ONE
        defaultPostShouldNotBeFound("rankOne.equals=" + UPDATED_RANK_ONE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankOneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankOne not equals to DEFAULT_RANK_ONE
        defaultPostShouldNotBeFound("rankOne.notEquals=" + DEFAULT_RANK_ONE);

        // Get all the postList where rankOne not equals to UPDATED_RANK_ONE
        defaultPostShouldBeFound("rankOne.notEquals=" + UPDATED_RANK_ONE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankOneIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankOne in DEFAULT_RANK_ONE or UPDATED_RANK_ONE
        defaultPostShouldBeFound("rankOne.in=" + DEFAULT_RANK_ONE + "," + UPDATED_RANK_ONE);

        // Get all the postList where rankOne equals to UPDATED_RANK_ONE
        defaultPostShouldNotBeFound("rankOne.in=" + UPDATED_RANK_ONE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankOneIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankOne is not null
        defaultPostShouldBeFound("rankOne.specified=true");

        // Get all the postList where rankOne is null
        defaultPostShouldNotBeFound("rankOne.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByRankOneIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankOne is greater than or equal to DEFAULT_RANK_ONE
        defaultPostShouldBeFound("rankOne.greaterThanOrEqual=" + DEFAULT_RANK_ONE);

        // Get all the postList where rankOne is greater than or equal to UPDATED_RANK_ONE
        defaultPostShouldNotBeFound("rankOne.greaterThanOrEqual=" + UPDATED_RANK_ONE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankOneIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankOne is less than or equal to DEFAULT_RANK_ONE
        defaultPostShouldBeFound("rankOne.lessThanOrEqual=" + DEFAULT_RANK_ONE);

        // Get all the postList where rankOne is less than or equal to SMALLER_RANK_ONE
        defaultPostShouldNotBeFound("rankOne.lessThanOrEqual=" + SMALLER_RANK_ONE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankOneIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankOne is less than DEFAULT_RANK_ONE
        defaultPostShouldNotBeFound("rankOne.lessThan=" + DEFAULT_RANK_ONE);

        // Get all the postList where rankOne is less than UPDATED_RANK_ONE
        defaultPostShouldBeFound("rankOne.lessThan=" + UPDATED_RANK_ONE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankOneIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankOne is greater than DEFAULT_RANK_ONE
        defaultPostShouldNotBeFound("rankOne.greaterThan=" + DEFAULT_RANK_ONE);

        // Get all the postList where rankOne is greater than SMALLER_RANK_ONE
        defaultPostShouldBeFound("rankOne.greaterThan=" + SMALLER_RANK_ONE);
    }


    @Test
    @Transactional
    public void getAllPostsByRankTwoIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankTwo equals to DEFAULT_RANK_TWO
        defaultPostShouldBeFound("rankTwo.equals=" + DEFAULT_RANK_TWO);

        // Get all the postList where rankTwo equals to UPDATED_RANK_TWO
        defaultPostShouldNotBeFound("rankTwo.equals=" + UPDATED_RANK_TWO);
    }

    @Test
    @Transactional
    public void getAllPostsByRankTwoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankTwo not equals to DEFAULT_RANK_TWO
        defaultPostShouldNotBeFound("rankTwo.notEquals=" + DEFAULT_RANK_TWO);

        // Get all the postList where rankTwo not equals to UPDATED_RANK_TWO
        defaultPostShouldBeFound("rankTwo.notEquals=" + UPDATED_RANK_TWO);
    }

    @Test
    @Transactional
    public void getAllPostsByRankTwoIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankTwo in DEFAULT_RANK_TWO or UPDATED_RANK_TWO
        defaultPostShouldBeFound("rankTwo.in=" + DEFAULT_RANK_TWO + "," + UPDATED_RANK_TWO);

        // Get all the postList where rankTwo equals to UPDATED_RANK_TWO
        defaultPostShouldNotBeFound("rankTwo.in=" + UPDATED_RANK_TWO);
    }

    @Test
    @Transactional
    public void getAllPostsByRankTwoIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankTwo is not null
        defaultPostShouldBeFound("rankTwo.specified=true");

        // Get all the postList where rankTwo is null
        defaultPostShouldNotBeFound("rankTwo.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByRankTwoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankTwo is greater than or equal to DEFAULT_RANK_TWO
        defaultPostShouldBeFound("rankTwo.greaterThanOrEqual=" + DEFAULT_RANK_TWO);

        // Get all the postList where rankTwo is greater than or equal to UPDATED_RANK_TWO
        defaultPostShouldNotBeFound("rankTwo.greaterThanOrEqual=" + UPDATED_RANK_TWO);
    }

    @Test
    @Transactional
    public void getAllPostsByRankTwoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankTwo is less than or equal to DEFAULT_RANK_TWO
        defaultPostShouldBeFound("rankTwo.lessThanOrEqual=" + DEFAULT_RANK_TWO);

        // Get all the postList where rankTwo is less than or equal to SMALLER_RANK_TWO
        defaultPostShouldNotBeFound("rankTwo.lessThanOrEqual=" + SMALLER_RANK_TWO);
    }

    @Test
    @Transactional
    public void getAllPostsByRankTwoIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankTwo is less than DEFAULT_RANK_TWO
        defaultPostShouldNotBeFound("rankTwo.lessThan=" + DEFAULT_RANK_TWO);

        // Get all the postList where rankTwo is less than UPDATED_RANK_TWO
        defaultPostShouldBeFound("rankTwo.lessThan=" + UPDATED_RANK_TWO);
    }

    @Test
    @Transactional
    public void getAllPostsByRankTwoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankTwo is greater than DEFAULT_RANK_TWO
        defaultPostShouldNotBeFound("rankTwo.greaterThan=" + DEFAULT_RANK_TWO);

        // Get all the postList where rankTwo is greater than SMALLER_RANK_TWO
        defaultPostShouldBeFound("rankTwo.greaterThan=" + SMALLER_RANK_TWO);
    }


    @Test
    @Transactional
    public void getAllPostsByRankThreeIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankThree equals to DEFAULT_RANK_THREE
        defaultPostShouldBeFound("rankThree.equals=" + DEFAULT_RANK_THREE);

        // Get all the postList where rankThree equals to UPDATED_RANK_THREE
        defaultPostShouldNotBeFound("rankThree.equals=" + UPDATED_RANK_THREE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankThreeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankThree not equals to DEFAULT_RANK_THREE
        defaultPostShouldNotBeFound("rankThree.notEquals=" + DEFAULT_RANK_THREE);

        // Get all the postList where rankThree not equals to UPDATED_RANK_THREE
        defaultPostShouldBeFound("rankThree.notEquals=" + UPDATED_RANK_THREE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankThreeIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankThree in DEFAULT_RANK_THREE or UPDATED_RANK_THREE
        defaultPostShouldBeFound("rankThree.in=" + DEFAULT_RANK_THREE + "," + UPDATED_RANK_THREE);

        // Get all the postList where rankThree equals to UPDATED_RANK_THREE
        defaultPostShouldNotBeFound("rankThree.in=" + UPDATED_RANK_THREE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankThreeIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankThree is not null
        defaultPostShouldBeFound("rankThree.specified=true");

        // Get all the postList where rankThree is null
        defaultPostShouldNotBeFound("rankThree.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByRankThreeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankThree is greater than or equal to DEFAULT_RANK_THREE
        defaultPostShouldBeFound("rankThree.greaterThanOrEqual=" + DEFAULT_RANK_THREE);

        // Get all the postList where rankThree is greater than or equal to UPDATED_RANK_THREE
        defaultPostShouldNotBeFound("rankThree.greaterThanOrEqual=" + UPDATED_RANK_THREE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankThreeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankThree is less than or equal to DEFAULT_RANK_THREE
        defaultPostShouldBeFound("rankThree.lessThanOrEqual=" + DEFAULT_RANK_THREE);

        // Get all the postList where rankThree is less than or equal to SMALLER_RANK_THREE
        defaultPostShouldNotBeFound("rankThree.lessThanOrEqual=" + SMALLER_RANK_THREE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankThreeIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankThree is less than DEFAULT_RANK_THREE
        defaultPostShouldNotBeFound("rankThree.lessThan=" + DEFAULT_RANK_THREE);

        // Get all the postList where rankThree is less than UPDATED_RANK_THREE
        defaultPostShouldBeFound("rankThree.lessThan=" + UPDATED_RANK_THREE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankThreeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankThree is greater than DEFAULT_RANK_THREE
        defaultPostShouldNotBeFound("rankThree.greaterThan=" + DEFAULT_RANK_THREE);

        // Get all the postList where rankThree is greater than SMALLER_RANK_THREE
        defaultPostShouldBeFound("rankThree.greaterThan=" + SMALLER_RANK_THREE);
    }


    @Test
    @Transactional
    public void getAllPostsByRankFourIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFour equals to DEFAULT_RANK_FOUR
        defaultPostShouldBeFound("rankFour.equals=" + DEFAULT_RANK_FOUR);

        // Get all the postList where rankFour equals to UPDATED_RANK_FOUR
        defaultPostShouldNotBeFound("rankFour.equals=" + UPDATED_RANK_FOUR);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFourIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFour not equals to DEFAULT_RANK_FOUR
        defaultPostShouldNotBeFound("rankFour.notEquals=" + DEFAULT_RANK_FOUR);

        // Get all the postList where rankFour not equals to UPDATED_RANK_FOUR
        defaultPostShouldBeFound("rankFour.notEquals=" + UPDATED_RANK_FOUR);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFourIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFour in DEFAULT_RANK_FOUR or UPDATED_RANK_FOUR
        defaultPostShouldBeFound("rankFour.in=" + DEFAULT_RANK_FOUR + "," + UPDATED_RANK_FOUR);

        // Get all the postList where rankFour equals to UPDATED_RANK_FOUR
        defaultPostShouldNotBeFound("rankFour.in=" + UPDATED_RANK_FOUR);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFourIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFour is not null
        defaultPostShouldBeFound("rankFour.specified=true");

        // Get all the postList where rankFour is null
        defaultPostShouldNotBeFound("rankFour.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByRankFourIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFour is greater than or equal to DEFAULT_RANK_FOUR
        defaultPostShouldBeFound("rankFour.greaterThanOrEqual=" + DEFAULT_RANK_FOUR);

        // Get all the postList where rankFour is greater than or equal to UPDATED_RANK_FOUR
        defaultPostShouldNotBeFound("rankFour.greaterThanOrEqual=" + UPDATED_RANK_FOUR);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFourIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFour is less than or equal to DEFAULT_RANK_FOUR
        defaultPostShouldBeFound("rankFour.lessThanOrEqual=" + DEFAULT_RANK_FOUR);

        // Get all the postList where rankFour is less than or equal to SMALLER_RANK_FOUR
        defaultPostShouldNotBeFound("rankFour.lessThanOrEqual=" + SMALLER_RANK_FOUR);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFourIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFour is less than DEFAULT_RANK_FOUR
        defaultPostShouldNotBeFound("rankFour.lessThan=" + DEFAULT_RANK_FOUR);

        // Get all the postList where rankFour is less than UPDATED_RANK_FOUR
        defaultPostShouldBeFound("rankFour.lessThan=" + UPDATED_RANK_FOUR);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFourIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFour is greater than DEFAULT_RANK_FOUR
        defaultPostShouldNotBeFound("rankFour.greaterThan=" + DEFAULT_RANK_FOUR);

        // Get all the postList where rankFour is greater than SMALLER_RANK_FOUR
        defaultPostShouldBeFound("rankFour.greaterThan=" + SMALLER_RANK_FOUR);
    }


    @Test
    @Transactional
    public void getAllPostsByRankFiveIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFive equals to DEFAULT_RANK_FIVE
        defaultPostShouldBeFound("rankFive.equals=" + DEFAULT_RANK_FIVE);

        // Get all the postList where rankFive equals to UPDATED_RANK_FIVE
        defaultPostShouldNotBeFound("rankFive.equals=" + UPDATED_RANK_FIVE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFive not equals to DEFAULT_RANK_FIVE
        defaultPostShouldNotBeFound("rankFive.notEquals=" + DEFAULT_RANK_FIVE);

        // Get all the postList where rankFive not equals to UPDATED_RANK_FIVE
        defaultPostShouldBeFound("rankFive.notEquals=" + UPDATED_RANK_FIVE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFiveIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFive in DEFAULT_RANK_FIVE or UPDATED_RANK_FIVE
        defaultPostShouldBeFound("rankFive.in=" + DEFAULT_RANK_FIVE + "," + UPDATED_RANK_FIVE);

        // Get all the postList where rankFive equals to UPDATED_RANK_FIVE
        defaultPostShouldNotBeFound("rankFive.in=" + UPDATED_RANK_FIVE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFive is not null
        defaultPostShouldBeFound("rankFive.specified=true");

        // Get all the postList where rankFive is null
        defaultPostShouldNotBeFound("rankFive.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByRankFiveIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFive is greater than or equal to DEFAULT_RANK_FIVE
        defaultPostShouldBeFound("rankFive.greaterThanOrEqual=" + DEFAULT_RANK_FIVE);

        // Get all the postList where rankFive is greater than or equal to UPDATED_RANK_FIVE
        defaultPostShouldNotBeFound("rankFive.greaterThanOrEqual=" + UPDATED_RANK_FIVE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFiveIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFive is less than or equal to DEFAULT_RANK_FIVE
        defaultPostShouldBeFound("rankFive.lessThanOrEqual=" + DEFAULT_RANK_FIVE);

        // Get all the postList where rankFive is less than or equal to SMALLER_RANK_FIVE
        defaultPostShouldNotBeFound("rankFive.lessThanOrEqual=" + SMALLER_RANK_FIVE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFiveIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFive is less than DEFAULT_RANK_FIVE
        defaultPostShouldNotBeFound("rankFive.lessThan=" + DEFAULT_RANK_FIVE);

        // Get all the postList where rankFive is less than UPDATED_RANK_FIVE
        defaultPostShouldBeFound("rankFive.lessThan=" + UPDATED_RANK_FIVE);
    }

    @Test
    @Transactional
    public void getAllPostsByRankFiveIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where rankFive is greater than DEFAULT_RANK_FIVE
        defaultPostShouldNotBeFound("rankFive.greaterThan=" + DEFAULT_RANK_FIVE);

        // Get all the postList where rankFive is greater than SMALLER_RANK_FIVE
        defaultPostShouldBeFound("rankFive.greaterThan=" + SMALLER_RANK_FIVE);
    }


    @Test
    @Transactional
    public void getAllPostsByOverheardCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        OverheardComment overheardComment = OverheardCommentResourceIT.createEntity(em);
        em.persist(overheardComment);
        em.flush();
        post.addOverheardComment(overheardComment);
        postRepository.saveAndFlush(post);
        Long overheardCommentId = overheardComment.getId();

        // Get all the postList where overheardComment equals to overheardCommentId
        defaultPostShouldBeFound("overheardCommentId.equals=" + overheardCommentId);

        // Get all the postList where overheardComment equals to overheardCommentId + 1
        defaultPostShouldNotBeFound("overheardCommentId.equals=" + (overheardCommentId + 1));
    }


    @Test
    @Transactional
    public void getAllPostsByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = post.getUser();
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
        // Get already existing entity
        Topic topic = post.getTopic();
        postRepository.saveAndFlush(post);
        Long topicId = topic.getId();

        // Get all the postList where topic equals to topicId
        defaultPostShouldBeFound("topicId.equals=" + topicId);

        // Get all the postList where topic equals to topicId + 1
        defaultPostShouldNotBeFound("topicId.equals=" + (topicId + 1));
    }


    @Test
    @Transactional
    public void getAllPostsByUserUprankIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        User userUprank = UserResourceIT.createEntity(em);
        em.persist(userUprank);
        em.flush();
        post.addUserUprank(userUprank);
        postRepository.saveAndFlush(post);
        Long userUprankId = userUprank.getId();

        // Get all the postList where userUprank equals to userUprankId
        defaultPostShouldBeFound("userUprankId.equals=" + userUprankId);

        // Get all the postList where userUprank equals to userUprankId + 1
        defaultPostShouldNotBeFound("userUprankId.equals=" + (userUprankId + 1));
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
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].rankOne").value(hasItem(DEFAULT_RANK_ONE)))
            .andExpect(jsonPath("$.[*].rankTwo").value(hasItem(DEFAULT_RANK_TWO)))
            .andExpect(jsonPath("$.[*].rankThree").value(hasItem(DEFAULT_RANK_THREE)))
            .andExpect(jsonPath("$.[*].rankFour").value(hasItem(DEFAULT_RANK_FOUR)))
            .andExpect(jsonPath("$.[*].rankFive").value(hasItem(DEFAULT_RANK_FIVE)));

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
            .date(UPDATED_DATE)
            .rankOne(UPDATED_RANK_ONE)
            .rankTwo(UPDATED_RANK_TWO)
            .rankThree(UPDATED_RANK_THREE)
            .rankFour(UPDATED_RANK_FOUR)
            .rankFive(UPDATED_RANK_FIVE);

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
        assertThat(testPost.getRankOne()).isEqualTo(UPDATED_RANK_ONE);
        assertThat(testPost.getRankTwo()).isEqualTo(UPDATED_RANK_TWO);
        assertThat(testPost.getRankThree()).isEqualTo(UPDATED_RANK_THREE);
        assertThat(testPost.getRankFour()).isEqualTo(UPDATED_RANK_FOUR);
        assertThat(testPost.getRankFive()).isEqualTo(UPDATED_RANK_FIVE);
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
