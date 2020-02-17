package com.perea.overheard.web.rest;

import com.perea.overheard.OverheardclubApp;
import com.perea.overheard.domain.Rank;
import com.perea.overheard.repository.RankRepository;
import com.perea.overheard.web.rest.errors.ExceptionTranslator;

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
 * Integration tests for the {@link RankResource} REST controller.
 */
@SpringBootTest(classes = OverheardclubApp.class)
public class RankResourceIT {

    private static final Integer DEFAULT_RANK = 1;
    private static final Integer UPDATED_RANK = 2;

    private static final RankType DEFAULT_RANK_TYPE = RankType.FUNNY;
    private static final RankType UPDATED_RANK_TYPE = RankType.PATHETIC;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private RankRepository rankRepository;

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

    private MockMvc restRankMockMvc;

    private Rank rank;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RankResource rankResource = new RankResource(rankRepository);
        this.restRankMockMvc = MockMvcBuilders.standaloneSetup(rankResource)
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
    public static Rank createEntity(EntityManager em) {
        Rank rank = new Rank()
            .rank(DEFAULT_RANK)
            .rankType(DEFAULT_RANK_TYPE)
            .date(DEFAULT_DATE);
        return rank;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rank createUpdatedEntity(EntityManager em) {
        Rank rank = new Rank()
            .rank(UPDATED_RANK)
            .rankType(UPDATED_RANK_TYPE)
            .date(UPDATED_DATE);
        return rank;
    }

    @BeforeEach
    public void initTest() {
        rank = createEntity(em);
    }

    @Test
    @Transactional
    public void createRank() throws Exception {
        int databaseSizeBeforeCreate = rankRepository.findAll().size();

        // Create the Rank
        restRankMockMvc.perform(post("/api/ranks")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rank)))
            .andExpect(status().isCreated());

        // Validate the Rank in the database
        List<Rank> rankList = rankRepository.findAll();
        assertThat(rankList).hasSize(databaseSizeBeforeCreate + 1);
        Rank testRank = rankList.get(rankList.size() - 1);
        assertThat(testRank.getRank()).isEqualTo(DEFAULT_RANK);
        assertThat(testRank.getRankType()).isEqualTo(DEFAULT_RANK_TYPE);
        assertThat(testRank.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void createRankWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = rankRepository.findAll().size();

        // Create the Rank with an existing ID
        rank.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRankMockMvc.perform(post("/api/ranks")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rank)))
            .andExpect(status().isBadRequest());

        // Validate the Rank in the database
        List<Rank> rankList = rankRepository.findAll();
        assertThat(rankList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllRanks() throws Exception {
        // Initialize the database
        rankRepository.saveAndFlush(rank);

        // Get all the rankList
        restRankMockMvc.perform(get("/api/ranks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rank.getId().intValue())))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].rankType").value(hasItem(DEFAULT_RANK_TYPE.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getRank() throws Exception {
        // Initialize the database
        rankRepository.saveAndFlush(rank);

        // Get the rank
        restRankMockMvc.perform(get("/api/ranks/{id}", rank.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rank.getId().intValue()))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK))
            .andExpect(jsonPath("$.rankType").value(DEFAULT_RANK_TYPE.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRank() throws Exception {
        // Get the rank
        restRankMockMvc.perform(get("/api/ranks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRank() throws Exception {
        // Initialize the database
        rankRepository.saveAndFlush(rank);

        int databaseSizeBeforeUpdate = rankRepository.findAll().size();

        // Update the rank
        Rank updatedRank = rankRepository.findById(rank.getId()).get();
        // Disconnect from session so that the updates on updatedRank are not directly saved in db
        em.detach(updatedRank);
        updatedRank
            .rank(UPDATED_RANK)
            .rankType(UPDATED_RANK_TYPE)
            .date(UPDATED_DATE);

        restRankMockMvc.perform(put("/api/ranks")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedRank)))
            .andExpect(status().isOk());

        // Validate the Rank in the database
        List<Rank> rankList = rankRepository.findAll();
        assertThat(rankList).hasSize(databaseSizeBeforeUpdate);
        Rank testRank = rankList.get(rankList.size() - 1);
        assertThat(testRank.getRank()).isEqualTo(UPDATED_RANK);
        assertThat(testRank.getRankType()).isEqualTo(UPDATED_RANK_TYPE);
        assertThat(testRank.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingRank() throws Exception {
        int databaseSizeBeforeUpdate = rankRepository.findAll().size();

        // Create the Rank

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRankMockMvc.perform(put("/api/ranks")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rank)))
            .andExpect(status().isBadRequest());

        // Validate the Rank in the database
        List<Rank> rankList = rankRepository.findAll();
        assertThat(rankList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRank() throws Exception {
        // Initialize the database
        rankRepository.saveAndFlush(rank);

        int databaseSizeBeforeDelete = rankRepository.findAll().size();

        // Delete the rank
        restRankMockMvc.perform(delete("/api/ranks/{id}", rank.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Rank> rankList = rankRepository.findAll();
        assertThat(rankList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
