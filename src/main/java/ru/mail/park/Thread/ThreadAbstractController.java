package ru.mail.park.Thread;

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
public class ThreadAbstractController {

    @RequestMapping(path = "/db/api/thread/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody Thread body) throws JsonProcessingException {

        ThreadService threadService = new ThreadService();
        return threadService.createThread(body);
    }

    @RequestMapping(path = "/db/api/thread/close", method = RequestMethod.POST)
    public ResponseEntity closeThread(@RequestBody Thread body) throws JsonProcessingException {
        ThreadService threadService = new ThreadService();
        return threadService.closeThread(body);
    }

    @RequestMapping(path = "/db/api/thread/details", method = RequestMethod.GET)
    public ResponseEntity detailsThread(@RequestParam("thread") Integer id,
                                        @RequestParam(value = "related", required = false) ArrayList related) throws JsonProcessingException, SQLException {
        ThreadService threadService = new ThreadService();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode tmp = mapper.createObjectNode();
        try {
            tmp = threadService.details(id, related);
            if (tmp == null)
                return ResponseEntity.ok().body(Errors.getJson(Errors.INCORRECT_REQUEST));
            response.put("code", 0);
            response.set("response", tmp);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        }
        catch (SQLException e){
            return ResponseEntity.ok().body(Errors.getJson(Errors.INCORRECT_REQUEST));
        }
    }

    @RequestMapping(path = "/db/api/thread/list", method = RequestMethod.GET)
    public ResponseEntity listThread(@RequestParam(value = "since", required = false) String since,
                                     @RequestParam(value = "limit", required = false) Integer limit,
                                     @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                     @RequestParam(value = "user", required = false) String user,
                                     @RequestParam(value = "forum", required = false) String forum) throws JsonProcessingException {
        ThreadService threadService = new ThreadService();
        return threadService.listThread(since, limit, order, user, forum);
    }

    @RequestMapping(path = "/db/api/thread/listPosts", method = RequestMethod.GET)
    public ResponseEntity listPostsThread(@RequestParam(value = "since", required = false) String since,
                                          @RequestParam(value = "limit", required = false) Integer limit,
                                          @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                          @RequestParam(value = "sort", required = false) String sort,
                                          @RequestParam("thread") int id) throws JsonProcessingException {
        ThreadService threadService = new ThreadService();
        return threadService.listPostsThread(since, limit, order, sort, id);
    }

    @RequestMapping(path = "/db/api/thread/open", method = RequestMethod.POST)
    public ResponseEntity threadOpen(@RequestBody Thread body) throws JsonProcessingException {

        ThreadService threadService = new ThreadService();
        return threadService.threadOpen(body);
    }

    @RequestMapping(path = "/db/api/thread/remove", method = RequestMethod.POST)
    public ResponseEntity removeThread(@RequestBody Thread body) throws JsonProcessingException {
        ThreadService threadService = new ThreadService();
        return threadService.threadRemove(body);
    }

    @RequestMapping(path = "/db/api/thread/restore", method = RequestMethod.POST)
    public ResponseEntity restoreThread(@RequestBody Thread body) throws JsonProcessingException {

        ThreadService threadService = new ThreadService();
        return threadService.threadRestore(body);
    }

    @RequestMapping(path = "/db/api/thread/subscribe", method = RequestMethod.POST)
    public ResponseEntity subscribeThread(@RequestBody Thread body) throws JsonProcessingException {
        ThreadService threadService = new ThreadService();
        return threadService.threadSubscribe(body);
    }

    @RequestMapping(path = "/db/api/thread/unsubscribe", method = RequestMethod.POST)
    public ResponseEntity unsubscribeThread(@RequestBody Thread body) throws JsonProcessingException {
        ThreadService threadService = new ThreadService();
        return threadService.threadUnsubscribe(body);
    }

    @RequestMapping(path = "/db/api/thread/update", method = RequestMethod.POST)
    public ResponseEntity updateThread(@RequestBody Thread body) throws JsonProcessingException {
        ThreadService threadService = new ThreadService();
        return threadService.updateThread(body);
    }

    @RequestMapping(path = "/db/api/thread/vote", method = RequestMethod.POST)
    public ResponseEntity voteThread(@RequestBody VoteThread body) throws JsonProcessingException {
        ThreadService threadService = new ThreadService();
        return threadService.voteThread(body);
    }
}
