package com.perea.overheard.web.rest;

import com.perea.overheard.domain.Rank;
import com.perea.overheard.repository.RankRepository;
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
 * REST controller for managing {@link com.perea.overheard.domain.Rank}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RankResource {

    private final Logger log = LoggerFactory.getLogger(RankResource.class);

    private static final String ENTITY_NAME = "rank";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RankRepository rankRepository;

    public RankResource(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    /**
     * {@code POST  /ranks} : Create a new rank.
     *
     * @param rank the rank to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rank, or with status {@code 400 (Bad Request)} if the rank has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ranks")
    public ResponseEntity<Rank> createRank(@RequestBody Rank rank) throws URISyntaxException {
        log.debug("REST request to save Rank : {}", rank);
        if (rank.getId() != null) {
            throw new BadRequestAlertException("A new rank cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Rank result = rankRepository.save(rank);
        return ResponseEntity.created(new URI("/api/ranks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ranks} : Updates an existing rank.
     *
     * @param rank the rank to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rank,
     * or with status {@code 400 (Bad Request)} if the rank is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rank couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ranks")
    public ResponseEntity<Rank> updateRank(@RequestBody Rank rank) throws URISyntaxException {
        log.debug("REST request to update Rank : {}", rank);
        if (rank.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Rank result = rankRepository.save(rank);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rank.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /ranks} : get all the ranks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ranks in body.
     */
    @GetMapping("/ranks")
    public List<Rank> getAllRanks() {
        log.debug("REST request to get all Ranks");
        return rankRepository.findAll();
    }

    /**
     * {@code GET  /ranks/:id} : get the "id" rank.
     *
     * @param id the id of the rank to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rank, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ranks/{id}")
    public ResponseEntity<Rank> getRank(@PathVariable Long id) {
        log.debug("REST request to get Rank : {}", id);
        Optional<Rank> rank = rankRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(rank);
    }

    /**
     * {@code DELETE  /ranks/:id} : delete the "id" rank.
     *
     * @param id the id of the rank to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ranks/{id}")
    public ResponseEntity<Void> deleteRank(@PathVariable Long id) {
        log.debug("REST request to delete Rank : {}", id);
        rankRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
