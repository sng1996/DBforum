package ru.mail.park.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.Forum.ForumService;
import ru.mail.park.errors.Errors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;


@CrossOrigin(origins = {"http://127.0.0.1"})
@RestController
public class UserAbstractController {

    @RequestMapping(path = "/db/api/user/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody User body) throws JsonProcessingException {
        UserService userService = new UserService();
        return userService.create(body);
    }

    @RequestMapping(path = "/db/api/user/details", method = RequestMethod.GET)
    public ResponseEntity details(@RequestParam("user") String user) throws JsonProcessingException {
        UserService userService = new UserService();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("code", 0);
        int user_id = 0;
        try {
            user_id = userService.getIdOnEmail(user);
            response.set("response", userService.details(user_id));
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        }catch(SQLException e){
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        }catch(JsonProcessingException e){
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    @RequestMapping(path = "/db/api/user/follow", method = RequestMethod.POST)
    public ResponseEntity followUser(@RequestBody FollowUser body) throws JsonProcessingException {
        UserService userService = new UserService();
        return userService.followUser(body);
    }

    @RequestMapping(path = "/db/api/user/listFollowers", method = RequestMethod.GET)
    public ResponseEntity listFollowersUser(@RequestParam(value = "since_id", required = false) Integer since_id,
                                            @RequestParam(value = "max_id", required = false) Integer max_id,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                            @RequestParam("user") String user) throws JsonProcessingException {
        UserService userService = new UserService();
        return userService.listFollowersUser(since_id, max_id, limit, order, user);
    }

    @RequestMapping(path = "/db/api/user/listFollowing", method = RequestMethod.GET)
    public ResponseEntity listFollowingUser(@RequestParam(value = "since_id", required = false) Integer since_id,
                                            @RequestParam(value = "max_id", required = false) Integer max_id,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                            @RequestParam("user") String user) throws JsonProcessingException {
        UserService userService = new UserService();
        return userService.listFollowingUser(since_id, max_id, limit, order, user);
    }

    @RequestMapping(path = "/db/api/user/unfollow", method = RequestMethod.POST)
    public ResponseEntity unfollowUser(@RequestBody FollowUser body) throws JsonProcessingException {
        UserService userService = new UserService();
        return userService.unfollowUser(body);
    }

    @RequestMapping(path = "/db/api/user/updateProfile", method = RequestMethod.POST)
    public ResponseEntity updateUser(@RequestBody UpdateUser body) throws JsonProcessingException {
        UserService userService = new UserService();
        return userService.updateUser(body);
    }

    @RequestMapping(path = "/db/api/user/listPosts", method = RequestMethod.GET)
    public ResponseEntity listPostsForum(@RequestParam(value = "since", required = false) String since,
                                         @RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                                         @RequestParam("user") String user) throws JsonProcessingException {
        UserService userService = new UserService();
        return userService.listPostsUser(since, limit, order, user);
    }
}
