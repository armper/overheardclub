package com.perea.overheard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;

import com.perea.overheard.domain.enumeration.RankType;

/**
 * A Rank.
 */
@Entity
@Table(name = "rank")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Rank implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_type")
    private RankType rankType;

    @Column(name = "date")
    private Instant date;

    @ManyToOne
    @JsonIgnoreProperties("ranks")
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RankType getRankType() {
        return rankType;
    }

    public Rank rankType(RankType rankType) {
        this.rankType = rankType;
        return this;
    }

    public void setRankType(RankType rankType) {
        this.rankType = rankType;
    }

    public Instant getDate() {
        return date;
    }

    public Rank date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public Rank user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rank)) {
            return false;
        }
        return id != null && id.equals(((Rank) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Rank{" +
            "id=" + getId() +
            ", rankType='" + getRankType() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
