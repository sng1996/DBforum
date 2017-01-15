package ru.mail.park.Forum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mail.park.Post.PostService;
import ru.mail.park.Thread.ThreadService;
import ru.mail.park.User.User;
import ru.mail.park.User.UserService;
import ru.mail.park.database.Database;
import ru.mail.park.errors.Errors;

import java.sql.*;
import java.util.ArrayList;

@Service
@Transactional
public class ForumService {

    public ResponseEntity createForum(Forum body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        UserService userService = new UserService();
        try {
            Database.update("insert into forums VALUES (NULL, \'" + body.getName() + "\', \'" + body.getShort_name() + "\', " + userService.getIdOnEmail(body.getUser()) + ")");
            Database.select("SELECT id, name, short_name, user FROM forums WHERE id = (SELECT max(id) FROM forums);", result -> {
                result.next();
                Forum forum = new Forum(result.getString("name"), result.getString("short_name"), userService.getEmailOnId(result.getInt("user")), result.getInt("id"));
                response.put("code", 0);
                response.set("response", forum.getForumInfo());
            });
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        } catch (SQLException e) {
            return ResponseEntity.ok().body(Errors.getJson(Errors.USER_EXISTS));
        }
    }

    public ObjectNode details(int forum_id, ArrayList related) throws SQLException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode respForum = mapper.createObjectNode();
        UserService userService = new UserService();
        boolean isUser = false;

        if (related != null) {
            for (int i = 0; i < related.size(); i++) {
                if (related.get(i).equals("user")) {
                    isUser = true;
                }
            }
        }

        final Integer userId = Database.select("select * from forums where forums.id = " + forum_id + ";", result -> {
            result.next();
            respForum.put("id", forum_id);
            respForum.put("name", result.getString("forums.name"));
            respForum.put("short_name", result.getString("short_name"));
            return result.getInt("forums.user");
        });

        if (isUser) {
            respForum.set("user", userService.details(userId));
        } else {
            respForum.put("user", userService.getEmailOnId(userId));
        }

        return respForum;
    }

    public ResponseEntity listPostsForum(String since, Integer limit, String order, ArrayList related, String forum) {

        String strSince = "";
        String strLimit = "";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        final ArrayNode resp = mapper.createArrayNode();

        if (since != null)
            strSince = "and posts.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;

        try {
            Database.select("select id from posts where forum = " + getIdOnForum(forum) + " " + strSince + " ORDER BY date " + order + strLimit + ";", result -> {
                while (result.next()) {
                    PostService postService = new PostService();
                    resp.add(postService.details(result.getInt("id"), related, false));
                }
                response.put("code", 0);
                response.set("response", resp);
            });
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
                e.printStackTrace();
                return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        }
    }

    public ResponseEntity listThreadsForum(String since, Integer limit, String order, ArrayList related, String forum) {

        String strSince = "";
        String strLimit = "";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        final ArrayNode resp = mapper.createArrayNode();

        if (since != null)
            strSince = "and threads.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;

        try{
            Database.select("select id from threads where forum = "+getIdOnForum(forum)+" " + strSince + " ORDER BY date " + order + strLimit + ";", result -> {
                while (result.next()) {
                    ThreadService threadService = new ThreadService();
                    resp.add(threadService.details(result.getInt("id"), related));
                }
                response.put("code", 0);
                response.set("response", resp);
            });
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        }
    }

    public ResponseEntity listUsersForum(Integer since_id, Integer max_id, Integer limit, String order, String forum) {

        String strSince = "";
        String strLimit = "";
        String strMax = "";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        final ArrayNode resp = mapper.createArrayNode();
        UserService userService = new UserService();

        if (since_id != null){
            strSince = " and users.id >= " + since_id;
        }

        if (max_id != null){
            strMax = " and users.id <= " + max_id;
        }

        if (limit != null)
            strLimit = " LIMIT " + limit;

        if (order == null)
            order = "desc";

        try {
            String query = "Select distinct users.id, users.name from posts join users on posts.user = users.id where posts.forum = "+getIdOnForum(forum)+" " + strSince + strMax + " order by users.name " + order + " " + strLimit + ";";
            System.out.println(query);
            Database.select(query, result -> {
                while (result.next()) {
                    resp.add(userService.details(result.getInt("users.id")));
                }
                response.put("code", 0);
                response.set("response", resp);
            });
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        }
    }

    public int getIdOnForum(String forum) throws SQLException, JsonProcessingException {
        return Database.select("select id from forums where short_name = \'" + forum + "\'", result -> {
            result.next();
            return result.getInt("id");
        });
    }

    public String getForumOnId(int id) throws SQLException, JsonProcessingException {
        return Database.select("select short_name from forums where id = "+id+"", result -> {
            result.next();
            return result.getString("short_name");
        });
    }
}
