package ru.mail.park.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.datasource.DataSourceUtils;
import ru.mail.park.Forum.ForumService;
import ru.mail.park.Post.Post;
import ru.mail.park.Post.PostService;
import ru.mail.park.database.Database;
import ru.mail.park.errors.Errors;


import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

@Service
@Transactional
public class UserService{

    public UserService() {
    }

    public ResponseEntity create(User body) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        try {
            if (body.isAnonymous()){
                Database.update("insert into users VALUES (NULL, " +
                        "NULL, " +
                        "NULL, " +
                        ""+body.isAnonymous()+", " +
                        "NULL, " +
                        "\'"+body.getEmail()+"\')");
            }
            else {
                Database.update("insert into users VALUES (NULL, " +
                        "\'" + body.getUsername() + "\', " +
                        "\'" + body.getAbout() + "\', " +
                        "" + body.isAnonymous() + ", " +
                        "\'" + body.getName() + "\', " +
                        "\'" + body.getEmail() + "\')");
            }

            Database.select("SELECT * FROM users WHERE id = (SELECT max(id) FROM users);", result->{
                result.next();
                User user = new User(result.getString(2), result.getString(3), result.getBoolean(4), result.getString(5), result.getString(6), result.getInt(1));
                response.put("code", 0);
                response.set("response", user.getUserInfo());
            });
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        } catch (SQLException e) {
            return ResponseEntity.ok().body(Errors.getJson(Errors.USER_EXISTS));
        }
    }

    public ObjectNode details(int user_id) throws JsonProcessingException, SQLException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        final ArrayNode followers = mapper.createArrayNode();
        final ArrayNode subscriptions = mapper.createArrayNode();
        final ArrayNode following = mapper.createArrayNode();
        UserService userService = new UserService();

        User user = Database.select("select * from users where id = " + user_id + ";", result -> {
            result.next();
            if (result.getBoolean("isAnonymous")) {
                return new User(null, null, result.getBoolean(4), null, result.getString(6), result.getInt(1));
            } else {
                return new User(result.getString(2), result.getString(3), result.getBoolean(4), result.getString(5), result.getString(6), result.getInt(1));
            }
        });

        Database.select("select thread from subscribes where user = " + user.getId() + ";", result -> {
            while (result.next()) {
                subscriptions.add(result.getInt("thread"));
            }
        });

        Database.select("select follower from follows where followee = " + user.getId() + ";", result -> {
            while (result.next()) {
                followers.add(userService.getEmailOnId(result.getInt("follower")));
            }
        });

        Database.select("select followee from follows where follower = " + user.getId() + ";", result -> {
            while (result.next()) {
                following.add(userService.getEmailOnId(result.getInt("followee")));
            }
        });

        return user.getUserInfoWithDetails(followers, following, subscriptions);
    }

    public ResponseEntity listFollowersUser(Integer since_id, Integer max_id, Integer limit, String order, String strUser) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ArrayNode users = mapper.createArrayNode();
        String strLimit = "";
        String strSince = "";
        String strMax = "";

        if (since_id != null) {
            strSince = " and users.id >= " + since_id;
        }

        if (max_id != null) {
            strMax = " and users.id <= " + max_id;
        }

        if (limit != null)
            strLimit = " LIMIT " + limit;

        if (order == null)
            order = "desc";

        try {
            Database.select("SELECT DISTINCT * FROM follows JOIN users ON follower = users.id WHERE followee = "+getIdOnEmail(strUser)+" " + strSince + strMax + " order by name " + order + " " + strLimit + ";", result ->{
                while(result.next()){
                    User user = new User(result.getString("username"), result.getString("about"), result.getBoolean("isAnonymous"), result.getString("name"), result.getString("email"), result.getInt("id"));
                    users.add(details(getIdOnEmail(user.getEmail())));
                }
                response.put("code", 0);
                response.set("response", users);
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

    public ResponseEntity listFollowingUser(Integer since_id, Integer max_id, Integer limit, String order, String strUser)  {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ArrayNode users = mapper.createArrayNode();
        String strLimit = "";
        String strSince = "";
        String strMax = "";

        if (since_id != null) {
            strSince = " and users.id >= " + since_id;
        }

        if (max_id != null) {
            strMax = " and users.id <= " + max_id;
        }

        if (limit != null)
            strLimit = " LIMIT " + limit;

        if (order == null)
            order = "desc";

        try {
            Database.select("SELECT DISTINCT * FROM follows JOIN users ON followee = users.id WHERE follower = "+getIdOnEmail(strUser)+"" + strSince + strMax + " order by name " + order + " " + strLimit + ";", result -> {
                while(result.next()){
                    User user = new User(result.getString("username"), result.getString("about"), result.getBoolean("isAnonymous"), result.getString("name"), result.getString("email"), result.getInt("id"));
                    users.add(details(getIdOnEmail(user.getEmail())));
                }
                response.put("code", 0);
                response.set("response", users);
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

    public ResponseEntity updateUser(UpdateUser body) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        try {
            Database.update("update users set about = \'"+body.getAbout()+"\', name = \'"+body.getName()+"\' where email = \'"+body.getUser()+"\';");
            response.put("code", 0);
            response.set("response", details(getIdOnEmail(body.getUser())));
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));

        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity unfollowUser(FollowUser body) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        try {
            int follower_id = getIdOnEmail(body.getFollower());
            int followee_id = getIdOnEmail(body.getFollowee());
            Database.update("delete from follows where follower = "+follower_id+" and followee = "+followee_id+";");

            response.put("code", 0);
            response.set("response", details(follower_id));
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("code", 1);
            response.put("response", 0);
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            response.put("code", 4);
            response.put("response", 0);
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity followUser(FollowUser body) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        try {
            int follower_id = getIdOnEmail(body.getFollower());
            int followee_id = getIdOnEmail(body.getFollowee());
            Database.update("insert into follows VALUES (NULL, "+follower_id+", "+followee_id+")");
            response.put("code", 0);
            response.set("response", details(follower_id));
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("code", 1);
            response.put("response", 0);
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            response.put("code", 4);
            response.put("response", 0);
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity listPostsUser(String since, Integer limit, String order, String strUser) {
        String strSince = "";
        String strLimit = "";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        final ArrayNode resp = mapper.createArrayNode();
        ForumService forumService = new ForumService();

        if (since != null)
            strSince = "and posts.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;

        try {
            Database.select("select * from posts where user = "+getIdOnEmail(strUser)+" " + strSince + " ORDER BY date " + order + strLimit + ";", result -> {
                while (result.next()) {
                    Post post = new Post(result.getBoolean("isApproved"), strUser, null, result.getString("date").substring(0, result.getString("date").length() - 2), result.getString("message"), result.getBoolean("isSpam"), result.getBoolean("isHighlighted"), result.getInt("thread"), forumService.getForumOnId(result.getInt("forum")), result.getBoolean("isDeleted"), result.getBoolean("isEdited"), result.getInt("dislikes"), result.getInt("likes"), result.getInt("parent"), result.getInt("points"), result.getInt("id"));
                    if (post.getParent() == 0)
                        post.setParent(null);
                    resp.add(post.getPostInfoDetails());
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

    public Integer getIdOnEmail(String email) throws SQLException, JsonProcessingException {
            return Database.select("select id from users where email = \'"+email+"\';", result -> {
                result.next();
                return result.getInt("id");
            });
    }

    public String getEmailOnId(int id) throws SQLException, JsonProcessingException {
        return Database.select("select email from users where id = " + id + ";", result -> {
            result.next();
            return result.getString("email");
        });
    }

}
