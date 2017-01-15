package ru.mail.park.Post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Pos;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mail.park.Forum.Forum;
import ru.mail.park.Forum.ForumService;
import ru.mail.park.Thread.ThreadService;
import ru.mail.park.User.UserService;
import ru.mail.park.database.Database;
import ru.mail.park.errors.Errors;

import java.sql.*;
import java.util.ArrayList;

@Service
@Transactional
public class PostService {

    public ResponseEntity createPost(Post body) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        UserService userService = new UserService();
        ForumService forumService = new ForumService();

        try {
            Database.update("insert into posts VALUES (NULL, " +
                    "" + body.getIsApproved() + ", " +
                    "" + userService.getIdOnEmail(body.getUser()) + ", " +
                    "\'" + body.getDate() + "\', " +
                    "\'" + body.getMessage() + "\', " +
                    "" + body.getIsSpam() + ", " +
                    "" + body.getIsHighlighted() + ", " +
                    "" + body.getThread() + ", " +
                    "" + forumService.getIdOnForum(body.getForum()) + ", " +
                    "" + body.getIsDeleted() + ", " +
                    "" + body.getIsEdited() + ", " +
                    "0, " +
                    "0, " +
                    "" + body.getParent() + ", " +
                    "0)");

            Database.update("update threads set posts = posts + 1 where id = " + body.getThread() + ";");
            String tmpId = "";
            Integer id = Database.select("select max(id) from posts;", result -> {
                result.next();
                return result.getInt(1);
            });
            String idWithZero = id.toString();
            while (idWithZero.length() % 9 < 8)
                idWithZero = "0" + idWithZero;
            if (body.getParent() != null && body.getParent() != 0) {
                tmpId = Database.select("select mpath from forSort where id = " + body.getParent() + ";", result -> {
                    result.next();
                    return result.getString(1);
                });
                tmpId = tmpId + "." + idWithZero;
            } else {
                tmpId = tmpId + idWithZero;
            }
            Database.update("replace into forSort values(" + id + ", \'" + tmpId + "\')");

            Database.select("select * from posts where id = (select max(id) from posts);", result -> {
                result.next();
                Integer parent = null;
                if (result.getInt("parent") != 0) {
                    parent = result.getInt("parent");
                }
                Post post = new Post(result.getBoolean("isApproved"), userService.getEmailOnId(result.getInt("user")), null, result.getString("date"), result.getString("message"), result.getBoolean("isSpam"), result.getBoolean("isHighlighted"), result.getInt("thread"), forumService.getForumOnId(result.getInt("forum")), result.getBoolean("isDeleted"), result.getBoolean("isEdited"), result.getInt("dislikes"), result.getInt("likes"), parent, result.getInt("points"), result.getInt("id"));
                response.put("code", 0);
                response.set("response", post.getPostInfo());
            });
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (SQLException e) {
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ObjectNode details(Integer idPost, ArrayList related, final Boolean isDel) throws JsonProcessingException, SQLException {

        boolean isUser = false;
        boolean isForum = false;
        boolean isThread = false;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode respPost = mapper.createObjectNode();
        UserService userService = new UserService();
        ForumService forumService = new ForumService();

        if (related != null) {
            for (int i = 0; i < related.size(); i++) {
                if (related.get(i).equals("user")) {
                    isUser = true;
                    continue;
                }
                if (related.get(i).equals("forum")) {
                    isForum = true;
                    continue;
                }
                if (related.get(i).equals("thread")) {
                    isThread = true;
                    continue;
                }
            }
        }

        if (idPost < 1){
            response.put("code", Errors.OBJECT_NOT_FOUND);
            response.put("response", 0);
            return response;
        }

        Post post = Database.select("select * from posts where posts.id = "+idPost+";", result -> {
            result.next();
            return new Post(result.getBoolean("isApproved"), userService.getEmailOnId(result.getInt("user")), null, result.getString("date").substring(0, result.getString("date").length() - 2), result.getString("message"), result.getBoolean("isSpam"), result.getBoolean("isHighlighted"), result.getInt("thread"), forumService.getForumOnId(result.getInt("forum")), result.getBoolean("isDeleted"), result.getBoolean("isEdited"), result.getInt("dislikes"), result.getInt("likes"), result.getInt("parent"), result.getInt("points"), result.getInt("id"));
        });

        if (post.getParent() == 0)
            post.setParent(null);
        if (isDel)
            post.setIsDeleted(true);

        respPost = post.getPostInfoDetails();
        Integer user_id = userService.getIdOnEmail(post.getUser());
        Integer forum_id = forumService.getIdOnForum(post.getForum());
        if (isUser) {
            respPost.set("user", userService.details(user_id));
        }
        if (!isUser) {
            respPost.put("user", userService.getEmailOnId(user_id));
        }
        if (isForum) {
            respPost.set("forum", forumService.details(forum_id, null));
        }
        if (!isForum) {
            respPost.put("forum", forumService.getForumOnId(forum_id));
        }
        if (isThread) {
            ThreadService threadService = new ThreadService();
            respPost.set("thread", threadService.details(post.getThread(), null));
        }
        if (!isThread) {
            respPost.put("thread", post.getThread());
        }
        return respPost;
    }

    public ResponseEntity listPost(String since, Integer limit, String order, Integer thread, String forum) {

        String strSince = "";
        String strLimit = "";
        String query1 = "";
        ForumService forumService = new ForumService();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        final ArrayNode resp = mapper.createArrayNode();
        UserService userService = new UserService();


        if (since != null)
            strSince = "and posts.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;

        try {
            if (thread != null) {
                query1 = " posts.thread = " + thread + " ";
            } else {
                query1 = " posts.forum = \'" + forumService.getIdOnForum(forum) + "\' ";
            }
            Database.select("select * from posts where " + query1 + strSince + " ORDER BY posts.date " + order + strLimit + ";", result -> {
                while (result.next()) {
                    Post post = new Post(result.getBoolean("isApproved"), userService.getEmailOnId(result.getInt("user")), null, result.getString("date").substring(0, result.getString("date").length() - 2), result.getString("message"), result.getBoolean("isSpam"), result.getBoolean("isHighlighted"), result.getInt("thread"), forumService.getForumOnId(result.getInt("forum")), result.getBoolean("isDeleted"), result.getBoolean("isEdited"), result.getInt("dislikes"), result.getInt("likes"), result.getInt("parent"), result.getInt("points"), result.getInt("id"));
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

    public ResponseEntity removePost(Post post) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        try {
            Database.update("update posts set isDeleted = true where id = "+post.getPost()+";");
            Database.select("select thread from posts where id = "+post.getPost()+"", result -> {
                result.next();
                Database.update("update threads set posts = posts - 1 where id = "+result.getInt("thread")+";");
            });
            ObjectNode resp = mapper.createObjectNode();
            response.put("code", 0);
            resp.put("post", post.getPost());
            response.set("response", resp);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity restorePost(Post post) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        try {
            Database.update("update posts set isDeleted = false where id = "+post.getPost()+";");
            Database.select("select thread from posts where id = "+post.getPost()+";", result -> {
                result.next();
                Database.update("update threads set posts = posts + 1 where id = "+result.getInt("thread")+";");
            });
            ObjectNode resp = mapper.createObjectNode();
            response.put("code", 0);
            resp.put("post", post.getId());
            response.set("response", resp);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity updatePost(Post post) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        try {
            Database.update("update posts set message = \'"+post.getMessage()+"\' where id = "+post.getPost()+";");
            ObjectNode resp = mapper.createObjectNode();
            response.put("code", 0);
            PostService postService = new PostService();
            response.set("response", postService.details(post.getPost(), null, false));
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity votePost(VotePost post) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();

        String query = "";

        if (post.getVote() > 0)
            query = "likes = likes + 1";
        else
            query = "dislikes = dislikes + 1";

        try {
            Database.update("update posts set " + query + ", points = likes - dislikes where id = "+post.getPost()+";");
            response.put("code", 0);
            PostService postService = new PostService();
            response.set("response", postService.details(post.getPost(), null, false));
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }
}
