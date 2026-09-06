package club.overheard.web;

import club.overheard.config.CurrentUser;
import club.overheard.dto.Dtos.TopicRequest;
import club.overheard.dto.Dtos.TopicView;
import club.overheard.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {
    private final TopicService topics;

    public TopicController(TopicService topics) {
        this.topics = topics;
    }

    @GetMapping
    public List<TopicView> list() {
        return topics.list();
    }

    @GetMapping("/{id}")
    public TopicView get(@PathVariable Long id) {
        return topics.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicView create(@Valid @RequestBody TopicRequest req, @AuthenticationPrincipal CurrentUser me) {
        return topics.create(req, me.user());
    }

    @PutMapping("/{id}")
    public TopicView rename(@PathVariable Long id, @Valid @RequestBody TopicRequest req, @AuthenticationPrincipal CurrentUser me) {
        return topics.rename(id, req, me.user());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal CurrentUser me) {
        topics.delete(id, me.user());
    }
}
