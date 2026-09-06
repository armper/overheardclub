package club.overheard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** End-to-end walk through the API against an in-memory database. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiFlowTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String register(String username) throws Exception {
        MvcResult res = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"%s","email":"%s@example.com","password":"correct-horse","displayName":"%s"}
                    """.formatted(username, username, username)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", not(emptyString())))
            .andReturn();
        return json.readTree(res.getResponse().getContentAsString()).get("token").asText();
    }

    private MockHttpServletRequestBuilder as(MockHttpServletRequestBuilder b, String token) {
        return b.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void memberCanPostReactAndComment_visitorCanOnlyRead() throws Exception {
        String alice = register("alice");
        String bob = register("bob");

        // duplicate username is rejected
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"ALICE\",\"email\":\"x@example.com\",\"password\":\"correct-horse\",\"displayName\":\"A\"}"))
            .andExpect(status().isConflict());

        // wrong password
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"nope\"}"))
            .andExpect(status().isUnauthorized());

        // visitors can't write
        mvc.perform(post("/api/topics").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Trains\"}"))
            .andExpect(status().isUnauthorized());

        MvcResult topic = mvc.perform(as(post("/api/topics"), alice).content("{\"title\":\"Trains\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is("Trains")))
            .andReturn();
        long topicId = json.readTree(topic.getResponse().getContentAsString()).get("id").asLong();

        // validation errors come back as field messages
        mvc.perform(as(post("/api/posts"), alice).content("{\"title\":\"x\",\"content\":\"\",\"topicId\":" + topicId + "}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.title", notNullValue()));

        MvcResult post = mvc.perform(as(post("/api/posts"), alice)
                .content("{\"title\":\"Kid on the platform\",\"content\":\"\\\"Is the train late or are we early?\\\"\",\"topicId\":" + topicId + "}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.author.displayName", is("alice")))
            .andExpect(jsonPath("$.canEdit", is(true)))
            .andReturn();
        long postId = json.readTree(post.getResponse().getContentAsString()).get("id").asLong();

        // one reaction per member: a second call changes it rather than adding
        mvc.perform(as(put("/api/posts/" + postId + "/reaction"), bob).content("{\"type\":\"FUNNY\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reactions.FUNNY", is(1)))
            .andExpect(jsonPath("$.myReaction", is("FUNNY")));
        mvc.perform(as(put("/api/posts/" + postId + "/reaction"), bob).content("{\"type\":\"LOVE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reactions.LOVE", is(1)))
            .andExpect(jsonPath("$.reactions.FUNNY").doesNotExist());

        mvc.perform(as(post("/api/posts/" + postId + "/comments"), bob).content("{\"content\":\"Both.\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.canDelete", is(true)));

        // bob can't edit alice's post
        mvc.perform(as(put("/api/posts/" + postId), bob)
                .content("{\"title\":\"Hijacked\",\"content\":\"nope nope\",\"topicId\":" + topicId + "}"))
            .andExpect(status().isForbidden());

        // visitors can read the feed and see counts, but no personal reaction
        mvc.perform(get("/api/posts?sort=top"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].commentCount", is(1)))
            .andExpect(jsonPath("$.items[0].reactions.LOVE", is(1)))
            .andExpect(jsonPath("$.items[0].myReaction").doesNotExist())
            .andExpect(jsonPath("$.items[0].canEdit", is(false)));

        // topic list carries post counts; a non-empty topic can't be deleted
        mvc.perform(get("/api/topics"))
            .andExpect(jsonPath("$[0].postCount", is(1)));
        mvc.perform(as(delete("/api/topics/" + topicId), alice)).andExpect(status().isConflict());

        // author deletes the post, and its comments/reactions go with it
        mvc.perform(as(delete("/api/posts/" + postId), alice)).andExpect(status().isNoContent());
        mvc.perform(get("/api/posts/" + postId)).andExpect(status().isNotFound());
        mvc.perform(as(delete("/api/topics/" + topicId), alice)).andExpect(status().isNoContent());

        // admin endpoints are off-limits to regular members
        mvc.perform(as(get("/api/admin/users"), alice)).andExpect(status().isForbidden());
    }

    @Test
    void spaRoutesFallBackToIndex() throws Exception {
        mvc.perform(get("/topics/1")).andExpect(status().isOk());
        mvc.perform(get("/api/nope")).andExpect(status().is4xxClientError());
    }
}
