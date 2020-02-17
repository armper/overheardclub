package com.perea.overheard.web.rest;

import com.perea.overheard.domain.Ranking;
import com.perea.overheard.repository.RankingRepository;
import com.perea.overheard.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.perea.overheard.domain.Ranking}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RankingResource {

    private final Logger log = LoggerFactory.getLogger(RankingResource.class);

    private static final String ENTITY_NAME = "ranking";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RankingRepository rankingRepository;

    public RankingResource(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    /**
     * {@code POST  /rankings} : Create a new ranking.
     *
     * @param ranking the ranking to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ranking, or with status {@code 400 (Bad Request)} if the ranking has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rankings")
    public ResponseEntity<Ranking> createRanking(@RequestBody Ranking ranking) throws URISyntaxException {
        log.debug("REST request to save Ranking : {}", ranking);
        if (ranking.getId() != null) {
            throw new BadRequestAlertException("A new ranking cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ranking result = rankingRepository.save(ranking);
        return ResponseEntity.created(new URI("/api/rankings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rankings} : Updates an existing ranking.
     *
     * @param ranking the ranking to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ranking,
     * or with status {@code 400 (Bad Request)} if the ranking is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ranking couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rankings")
    public ResponseEntity<Ranking> updateRanking(@RequestBody Ranking ranking) throws URISyntaxException {
        log.debug("REST request to update Ranking : {}", ranking);
        if (ranking.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Ranking result = rankingRepository.save(ranking);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ranking.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /rankings} : get all the rankings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rankings in body.
     */
    @GetMapping("/rankings")
    public List<Ranking> getAllRankings() {
        log.debug("REST request to get all Rankings");
        return rankingRepository.findAll();
    }

    /**
     * {@code GET  /rankings/:id} : get the "id" ranking.
     *
     * @param id the id of the ranking to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ranking, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rankings/{id}")
    public ResponseEntity<Ranking> getRanking(@PathVariable Long id) {
        log.debug("REST request to get Ranking : {}", id);
        Optional<Ranking> ranking = rankingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ranking);
    }

    /**
     * {@code DELETE  /rankings/:id} : delete the "id" ranking.
     *
     * @param id the id of the ranking to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rankings/{id}")
    public ResponseEntity<Void> deleteRanking(@PathVariable Long id) {
        log.debug("REST request to delete Ranking : {}", id);
        rankingRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
