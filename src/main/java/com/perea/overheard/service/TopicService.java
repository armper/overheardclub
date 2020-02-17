package com.perea.overheard.service;

import com.perea.overheard.domain.Topic;
import com.perea.overheard.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Topic}.
 */
@Service
@Transactional
public class TopicService {

    private final Logger log = LoggerFactory.getLogger(TopicService.class);

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    /**
     * Save a topic.
     *
     * @param topic the entity to save.
     * @return the persisted entity.
     */
    public Topic save(Topic topic) {
        log.debug("Request to save Topic : {}", topic);
        return topicRepository.save(topic);
    }

    /**
     * Get all the topics.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Topic> findAll() {
        log.debug("Request to get all Topics");
        return topicRepository.findAll();
    }

    /**
     * Get one topic by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Topic> findOne(Long id) {
        log.debug("Request to get Topic : {}", id);
        return topicRepository.findById(id);
    }

    /**
     * Delete the topic by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Topic : {}", id);
        topicRepository.deleteById(id);
    }
}
