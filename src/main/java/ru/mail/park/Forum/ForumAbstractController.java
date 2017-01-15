package ru.mail.park.Forum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.errors.Errors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

@CrossOrigin(origins = {"http://127.0.0.1"})
@RestController
public class ForumAbstractController {

    @RequestMapping(path = "/db/api/forum/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody Forum body) throws JsonProcessingException {
        ForumService forumService = new ForumService();
        return forumService.createForum(body);
    }

    @RequestMapping(path = "/db/api/forum/details", method = RequestMethod.GET)
    public ResponseEntity detailsForum(@RequestParam("forum") String forum,
                                       @RequestParam(value = "related", required = false) ArrayList related) {
        ForumService forumService = new ForumService();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("code", 0);
        int forum_id = 0;
        try {
            forum_id = forumService.getIdOnForum(forum);
            response.set("response", forumService.details(forum_id, related));
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        }
    }

    @RequestMapping(path = "/db/api/forum/listPosts", method = RequestMethod.GET)
    public ResponseEntity listPostsForum(@RequestParam(value = "since", required = false) String since,
                                         @RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                         @RequestParam(value = "related", required = false) ArrayList related,
                                         @RequestParam("forum") String forum) throws JsonProcessingException {
        ForumService forumService = new ForumService();
        return forumService.listPostsForum(since, limit, order, related, forum);
    }

    @RequestMapping(path = "/db/api/forum/listThreads", method = RequestMethod.GET)
    public ResponseEntity listThreadsForum(@RequestParam(value = "since", required = false) String since,
                                           @RequestParam(value = "limit", required = false) Integer limit,
                                           @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                           @RequestParam(value = "related", required = false) ArrayList related,
                                           @RequestParam("forum") String forum) throws JsonProcessingException {
        ForumService forumService = new ForumService();
        return forumService.listThreadsForum(since, limit, order, related, forum);
    }

    @RequestMapping(path = "/db/api/forum/listUsers", method = RequestMethod.GET)
    public ResponseEntity listUsersForum(@RequestParam(value = "since_id", required = false) Integer since_id,
                                         @RequestParam(value = "max_id", required = false) Integer max_id,
                                         @RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                         @RequestParam("forum") String forum) throws JsonProcessingException {
        ForumService forumService = new ForumService();
        return forumService.listUsersForum(since_id, max_id, limit, order, forum);
    }



}
