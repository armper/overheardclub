package com.perea.overheard.web.rest;

import com.perea.overheard.OverheardclubApp;
import com.perea.overheard.domain.Ranking;
import com.perea.overheard.domain.User;
import com.perea.overheard.domain.Post;
import com.perea.overheard.repository.RankingRepository;
import com.perea.overheard.service.RankingService;
import com.perea.overheard.web.rest.errors.ExceptionTranslator;
import com.perea.overheard.service.dto.RankingCriteria;
import com.perea.overheard.service.RankingQueryService;

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

import com.perea.overheard.domain.enumeration.RankType;
/**
 * Integration tests for the {@link RankingResource} REST controller.
 */
@SpringBootTest(classes = OverheardclubApp.class)
public class RankingResourceIT {

    private static final RankType DEFAULT_RANK_TYPE = RankType.HILARIOUS;
    private static final RankType UPDATED_RANK_TYPE = RankType.PATHETIC;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private RankingService rankingService;

    @Autowired
    private RankingQueryService rankingQueryService;

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

    private MockMvc restRankingMockMvc;

    private Ranking ranking;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RankingResource rankingResource = new RankingResource(rankingService, rankingQueryService);
        this.restRankingMockMvc = MockMvcBuilders.standaloneSetup(rankingResource)
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
    public static Ranking createEntity(EntityManager em) {
        Ranking ranking = new Ranking()
            .rankType(DEFAULT_RANK_TYPE)
            .date(DEFAULT_DATE);
        return ranking;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ranking createUpdatedEntity(EntityManager em) {
        Ranking ranking = new Ranking()
            .rankType(UPDATED_RANK_TYPE)
            .date(UPDATED_DATE);
        return ranking;
    }

    @BeforeEach
    public void initTest() {
        ranking = createEntity(em);
    }

    @Test
    @Transactional
    public void createRanking() throws Exception {
        int databaseSizeBeforeCreate = rankingRepository.findAll().size();

        // Create the Ranking
        restRankingMockMvc.perform(post("/api/rankings")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ranking)))
            .andExpect(status().isCreated());

        // Validate the Ranking in the database
        List<Ranking> rankingList = rankingRepository.findAll();
        assertThat(rankingList).hasSize(databaseSizeBeforeCreate + 1);
        Ranking testRanking = rankingList.get(rankingList.size() - 1);
        assertThat(testRanking.getRankType()).isEqualTo(DEFAULT_RANK_TYPE);
        assertThat(testRanking.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void createRankingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = rankingRepository.findAll().size();

        // Create the Ranking with an existing ID
        ranking.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRankingMockMvc.perform(post("/api/rankings")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ranking)))
            .andExpect(status().isBadRequest());

        // Validate the Ranking in the database
        List<Ranking> rankingList = rankingRepository.findAll();
        assertThat(rankingList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllRankings() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get all the rankingList
        restRankingMockMvc.perform(get("/api/rankings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ranking.getId().intValue())))
            .andExpect(jsonPath("$.[*].rankType").value(hasItem(DEFAULT_RANK_TYPE.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getRanking() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get the ranking
        restRankingMockMvc.perform(get("/api/rankings/{id}", ranking.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ranking.getId().intValue()))
            .andExpect(jsonPath("$.rankType").value(DEFAULT_RANK_TYPE.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }


    @Test
    @Transactional
    public void getRankingsByIdFiltering() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        Long id = ranking.getId();

        defaultRankingShouldBeFound("id.equals=" + id);
        defaultRankingShouldNotBeFound("id.notEquals=" + id);

        defaultRankingShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRankingShouldNotBeFound("id.greaterThan=" + id);

        defaultRankingShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRankingShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllRankingsByRankTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get all the rankingList where rankType equals to DEFAULT_RANK_TYPE
        defaultRankingShouldBeFound("rankType.equals=" + DEFAULT_RANK_TYPE);

        // Get all the rankingList where rankType equals to UPDATED_RANK_TYPE
        defaultRankingShouldNotBeFound("rankType.equals=" + UPDATED_RANK_TYPE);
    }

    @Test
    @Transactional
    public void getAllRankingsByRankTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get all the rankingList where rankType not equals to DEFAULT_RANK_TYPE
        defaultRankingShouldNotBeFound("rankType.notEquals=" + DEFAULT_RANK_TYPE);

        // Get all the rankingList where rankType not equals to UPDATED_RANK_TYPE
        defaultRankingShouldBeFound("rankType.notEquals=" + UPDATED_RANK_TYPE);
    }

    @Test
    @Transactional
    public void getAllRankingsByRankTypeIsInShouldWork() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get all the rankingList where rankType in DEFAULT_RANK_TYPE or UPDATED_RANK_TYPE
        defaultRankingShouldBeFound("rankType.in=" + DEFAULT_RANK_TYPE + "," + UPDATED_RANK_TYPE);

        // Get all the rankingList where rankType equals to UPDATED_RANK_TYPE
        defaultRankingShouldNotBeFound("rankType.in=" + UPDATED_RANK_TYPE);
    }

    @Test
    @Transactional
    public void getAllRankingsByRankTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get all the rankingList where rankType is not null
        defaultRankingShouldBeFound("rankType.specified=true");

        // Get all the rankingList where rankType is null
        defaultRankingShouldNotBeFound("rankType.specified=false");
    }

    @Test
    @Transactional
    public void getAllRankingsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get all the rankingList where date equals to DEFAULT_DATE
        defaultRankingShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the rankingList where date equals to UPDATED_DATE
        defaultRankingShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllRankingsByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get all the rankingList where date not equals to DEFAULT_DATE
        defaultRankingShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the rankingList where date not equals to UPDATED_DATE
        defaultRankingShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllRankingsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get all the rankingList where date in DEFAULT_DATE or UPDATED_DATE
        defaultRankingShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the rankingList where date equals to UPDATED_DATE
        defaultRankingShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllRankingsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);

        // Get all the rankingList where date is not null
        defaultRankingShouldBeFound("date.specified=true");

        // Get all the rankingList where date is null
        defaultRankingShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllRankingsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        ranking.setUser(user);
        rankingRepository.saveAndFlush(ranking);
        Long userId = user.getId();

        // Get all the rankingList where user equals to userId
        defaultRankingShouldBeFound("userId.equals=" + userId);

        // Get all the rankingList where user equals to userId + 1
        defaultRankingShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllRankingsByPostIsEqualToSomething() throws Exception {
        // Initialize the database
        rankingRepository.saveAndFlush(ranking);
        Post post = PostResourceIT.createEntity(em);
        em.persist(post);
        em.flush();
        ranking.setPost(post);
        rankingRepository.saveAndFlush(ranking);
        Long postId = post.getId();

        // Get all the rankingList where post equals to postId
        defaultRankingShouldBeFound("postId.equals=" + postId);

        // Get all the rankingList where post equals to postId + 1
        defaultRankingShouldNotBeFound("postId.equals=" + (postId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRankingShouldBeFound(String filter) throws Exception {
        restRankingMockMvc.perform(get("/api/rankings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ranking.getId().intValue())))
            .andExpect(jsonPath("$.[*].rankType").value(hasItem(DEFAULT_RANK_TYPE.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));

        // Check, that the count call also returns 1
        restRankingMockMvc.perform(get("/api/rankings/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRankingShouldNotBeFound(String filter) throws Exception {
        restRankingMockMvc.perform(get("/api/rankings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRankingMockMvc.perform(get("/api/rankings/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingRanking() throws Exception {
        // Get the ranking
        restRankingMockMvc.perform(get("/api/rankings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRanking() throws Exception {
        // Initialize the database
        rankingService.save(ranking);

        int databaseSizeBeforeUpdate = rankingRepository.findAll().size();

        // Update the ranking
        Ranking updatedRanking = rankingRepository.findById(ranking.getId()).get();
        // Disconnect from session so that the updates on updatedRanking are not directly saved in db
        em.detach(updatedRanking);
        updatedRanking
            .rankType(UPDATED_RANK_TYPE)
            .date(UPDATED_DATE);

        restRankingMockMvc.perform(put("/api/rankings")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedRanking)))
            .andExpect(status().isOk());

        // Validate the Ranking in the database
        List<Ranking> rankingList = rankingRepository.findAll();
        assertThat(rankingList).hasSize(databaseSizeBeforeUpdate);
        Ranking testRanking = rankingList.get(rankingList.size() - 1);
        assertThat(testRanking.getRankType()).isEqualTo(UPDATED_RANK_TYPE);
        assertThat(testRanking.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingRanking() throws Exception {
        int databaseSizeBeforeUpdate = rankingRepository.findAll().size();

        // Create the Ranking

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRankingMockMvc.perform(put("/api/rankings")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(ranking)))
            .andExpect(status().isBadRequest());

        // Validate the Ranking in the database
        List<Ranking> rankingList = rankingRepository.findAll();
        assertThat(rankingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRanking() throws Exception {
        // Initialize the database
        rankingService.save(ranking);

        int databaseSizeBeforeDelete = rankingRepository.findAll().size();

        // Delete the ranking
        restRankingMockMvc.perform(delete("/api/rankings/{id}", ranking.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ranking> rankingList = rankingRepository.findAll();
        assertThat(rankingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
