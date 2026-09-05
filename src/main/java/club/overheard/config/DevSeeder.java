package club.overheard.config;

import club.overheard.domain.*;
import club.overheard.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Creates an admin account on first run, and (when `app.seed.enabled`) a handful of
 * sample members, topics and posts so a dev database isn't empty.
 */
@Configuration
public class DevSeeder {
    private static final Logger log = LoggerFactory.getLogger(DevSeeder.class);

    @Bean
    ApplicationRunner seed(AppProperties props, UserRepository users, TopicRepository topics, PostRepository posts,
                           CommentRepository comments, ReactionRepository reactions, PasswordEncoder encoder) {
        return args -> {
            if (users.count() > 0) {
                return;
            }
            String adminPassword = props.seed().adminPassword();
            if (adminPassword == null || adminPassword.isBlank()) {
                log.warn("No users exist and app.seed.admin-password is unset; no admin account was created.");
                return;
            }
            User admin = user(users, encoder, "admin", "admin@overheard.club", "Admin", adminPassword, Role.ADMIN);
            log.info("Created admin account 'admin'.");

            if (!props.seed().enabled()) {
                return;
            }
            User alice = user(users, encoder, "alice", "alice@example.com", "Alice", "password", Role.USER);
            User sam = user(users, encoder, "sam", "sam@example.com", "Sam", "password", Role.USER);

            Topic train = topic(topics, "On the train", admin);
            Topic office = topic(topics, "At the office", admin);
            Topic cafe = topic(topics, "Coffee shops", alice);

            Post p1 = post(posts, "Two kids arguing about whether Pluto has feelings",
                "\"It got demoted. Of course it has feelings.\"\n\"Planets don't have feelings.\"\n\"Then why did it get demoted?\"",
                alice, train, 30);
            Post p2 = post(posts, "Manager, very seriously",
                "\"Let's circle back on the circle-back.\"", sam, office, 26);
            Post p3 = post(posts, "Barista to a regular",
                "\"Your usual? Or are we having a day?\"", alice, cafe, 5);
            Post p4 = post(posts, "Overheard at standup",
                "\"I have no blockers, only vibes.\"", admin, office, 2);

            react(reactions, p1, sam, ReactionType.FUNNY);
            react(reactions, p1, admin, ReactionType.MIND_BLOWN);
            react(reactions, p2, alice, ReactionType.EYE_ROLL);
            react(reactions, p2, admin, ReactionType.FUNNY);
            react(reactions, p3, sam, ReactionType.LOVE);
            react(reactions, p4, alice, ReactionType.NICE);
            react(reactions, p4, sam, ReactionType.FUNNY);

            comment(comments, p1, sam, "I was on this train. Can confirm the Pluto kid won.");
            comment(comments, p4, alice, "Putting this on a mug.");
            log.info("Seeded sample members (alice/sam, password 'password'), topics and posts.");
        };
    }

    private static User user(UserRepository repo, PasswordEncoder enc, String username, String email, String name, String pw, Role role) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setDisplayName(name);
        u.setPasswordHash(enc.encode(pw));
        u.setRole(role);
        return repo.save(u);
    }

    private static Topic topic(TopicRepository repo, String title, User by) {
        Topic t = new Topic();
        t.setTitle(title);
        t.setCreatedBy(by);
        return repo.save(t);
    }

    private static Post post(PostRepository repo, String title, String content, User author, Topic topic, int hoursAgo) {
        Post p = new Post();
        p.setTitle(title);
        p.setContent(content);
        p.setAuthor(author);
        p.setTopic(topic);
        p.setCreatedAt(Instant.now().minus(hoursAgo, ChronoUnit.HOURS));
        return repo.save(p);
    }

    private static void react(ReactionRepository repo, Post post, User user, ReactionType type) {
        Reaction r = new Reaction();
        r.setPost(post);
        r.setUser(user);
        r.setType(type);
        repo.save(r);
    }

    private static void comment(CommentRepository repo, Post post, User author, String text) {
        Comment c = new Comment();
        c.setPost(post);
        c.setAuthor(author);
        c.setContent(text);
        repo.save(c);
    }
}
