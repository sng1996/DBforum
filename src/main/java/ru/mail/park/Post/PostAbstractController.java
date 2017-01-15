package ru.mail.park.Post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.errors.Errors;


import java.sql.SQLException;
import java.util.ArrayList;

@CrossOrigin(origins = {"http://127.0.0.1"})
@RestController
public class PostAbstractController {

    @RequestMapping(path = "/db/api/post/create", method = RequestMethod.POST)
    public ResponseEntity createPost(@RequestBody Post body) throws JsonProcessingException {

        PostService postService = new PostService();
        return postService.createPost(body);
    }

    @RequestMapping(path = "/db/api/post/details", method = RequestMethod.GET)
    public ResponseEntity detailsPost(@RequestParam("post") Integer id,
                                      @RequestParam(value = "related", required = false) ArrayList related) throws JsonProcessingException, SQLException {
        PostService postService = new PostService();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode tmp = mapper.createObjectNode();
        try {
            tmp = postService.details(id, related, false);
            response.put("code", 0);
            response.set("response", tmp);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        }

    }

    @RequestMapping(path = "/db/api/post/list", method = RequestMethod.GET)
    public ResponseEntity listPost(@RequestParam(value = "since", required = false) String since,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                   @RequestParam(value = "thread", required = false) Integer thread,
                                   @RequestParam(value = "forum", required = false) String forum) throws JsonProcessingException {
        PostService postService = new PostService();
        return postService.listPost(since, limit, order, thread, forum);
    }

    @RequestMapping(path = "/db/api/post/remove", method = RequestMethod.POST)
    public ResponseEntity removePost(@RequestBody Post post) throws JsonProcessingException {
        PostService postService = new PostService();
        return postService.removePost(post);
    }

    @RequestMapping(path = "/db/api/post/restore", method = RequestMethod.POST)
    public ResponseEntity restorePost(@RequestBody Post post) throws JsonProcessingException {

        PostService postService = new PostService();
        return postService.restorePost(post);
    }

    @RequestMapping(path = "/db/api/post/update", method = RequestMethod.POST)
    public ResponseEntity updatePost(@RequestBody Post post) throws JsonProcessingException {

        PostService postService = new PostService();
        return postService.updatePost(post);
    }

    @RequestMapping(path = "/db/api/post/vote", method = RequestMethod.POST)
    public ResponseEntity votePost(@RequestBody VotePost post) throws JsonProcessingException {
        PostService postService = new PostService();
        //System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHH " + post.getId());
        return postService.votePost(post);
    }
}
