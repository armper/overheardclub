package com.perea.overheard.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.perea.overheard.domain.Post} entity. This class is used
 * in {@link com.perea.overheard.web.rest.PostResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /posts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PostCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter content;

    private InstantFilter date;

    private IntegerFilter rankOne;

    private IntegerFilter rankTwo;

    private IntegerFilter rankThree;

    private IntegerFilter rankFour;

    private IntegerFilter rankFive;

    private LongFilter overheardCommentId;

    private LongFilter userId;

    private LongFilter topicId;

    public PostCriteria() {
    }

    public PostCriteria(PostCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.rankOne = other.rankOne == null ? null : other.rankOne.copy();
        this.rankTwo = other.rankTwo == null ? null : other.rankTwo.copy();
        this.rankThree = other.rankThree == null ? null : other.rankThree.copy();
        this.rankFour = other.rankFour == null ? null : other.rankFour.copy();
        this.rankFive = other.rankFive == null ? null : other.rankFive.copy();
        this.overheardCommentId = other.overheardCommentId == null ? null : other.overheardCommentId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.topicId = other.topicId == null ? null : other.topicId.copy();
    }

    @Override
    public PostCriteria copy() {
        return new PostCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getContent() {
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public InstantFilter getDate() {
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public IntegerFilter getRankOne() {
        return rankOne;
    }

    public void setRankOne(IntegerFilter rankOne) {
        this.rankOne = rankOne;
    }

    public IntegerFilter getRankTwo() {
        return rankTwo;
    }

    public void setRankTwo(IntegerFilter rankTwo) {
        this.rankTwo = rankTwo;
    }

    public IntegerFilter getRankThree() {
        return rankThree;
    }

    public void setRankThree(IntegerFilter rankThree) {
        this.rankThree = rankThree;
    }

    public IntegerFilter getRankFour() {
        return rankFour;
    }

    public void setRankFour(IntegerFilter rankFour) {
        this.rankFour = rankFour;
    }

    public IntegerFilter getRankFive() {
        return rankFive;
    }

    public void setRankFive(IntegerFilter rankFive) {
        this.rankFive = rankFive;
    }

    public LongFilter getOverheardCommentId() {
        return overheardCommentId;
    }

    public void setOverheardCommentId(LongFilter overheardCommentId) {
        this.overheardCommentId = overheardCommentId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getTopicId() {
        return topicId;
    }

    public void setTopicId(LongFilter topicId) {
        this.topicId = topicId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PostCriteria that = (PostCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(content, that.content) &&
            Objects.equals(date, that.date) &&
            Objects.equals(rankOne, that.rankOne) &&
            Objects.equals(rankTwo, that.rankTwo) &&
            Objects.equals(rankThree, that.rankThree) &&
            Objects.equals(rankFour, that.rankFour) &&
            Objects.equals(rankFive, that.rankFive) &&
            Objects.equals(overheardCommentId, that.overheardCommentId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(topicId, that.topicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        content,
        date,
        rankOne,
        rankTwo,
        rankThree,
        rankFour,
        rankFive,
        overheardCommentId,
        userId,
        topicId
        );
    }

    @Override
    public String toString() {
        return "PostCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (content != null ? "content=" + content + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (rankOne != null ? "rankOne=" + rankOne + ", " : "") +
                (rankTwo != null ? "rankTwo=" + rankTwo + ", " : "") +
                (rankThree != null ? "rankThree=" + rankThree + ", " : "") +
                (rankFour != null ? "rankFour=" + rankFour + ", " : "") +
                (rankFive != null ? "rankFive=" + rankFive + ", " : "") +
                (overheardCommentId != null ? "overheardCommentId=" + overheardCommentId + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (topicId != null ? "topicId=" + topicId + ", " : "") +
            "}";
    }

}
