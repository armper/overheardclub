package club.overheard.repo;

import club.overheard.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByTitleIgnoreCase(String title);

    /** Topics with their post counts, alphabetical. */
    @Query("select t, count(p) from Topic t left join Post p on p.topic = t group by t order by lower(t.title)")
    List<Object[]> findAllWithPostCounts();
}
