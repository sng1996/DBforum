package ru.mail.park.Thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mail.park.Forum.ForumService;
import ru.mail.park.Post.Post;
import ru.mail.park.Post.PostService;
import ru.mail.park.User.User;
import ru.mail.park.User.UserService;
import ru.mail.park.database.Database;
import ru.mail.park.errors.Errors;

import java.sql.*;
import java.util.ArrayList;


public class ThreadService {

    public ResponseEntity createThread(@RequestBody Thread body) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        UserService userService = new UserService();
        ForumService forumService = new ForumService();

        try {
            Database.update("insert into threads VALUES (NULL, " +
                    ""+forumService.getIdOnForum(body.getForum())+", " +
                    "\'"+body.getTitle()+"\', " +
                    ""+body.isClosed()+", " +
                    ""+userService.getIdOnEmail(body.getUser())+", " +
                    "\'"+body.getDate()+"\', " +
                    "\'"+body.getMessage()+"\', " +
                    "\'"+body.getSlug()+"\', " +
                    ""+body.isDeleted()+", " +
                    "0, 0, 0, 0)");

            Database.select("select id, forum, title, isClosed, user, date, message, slug, isDeleted from threads where id = (select max(id) from threads);", result -> {
                result.next();
                Thread thread = new Thread(forumService.getForumOnId(result.getInt("forum")), result.getString("title"), result.getBoolean("isClosed"), userService.getEmailOnId(result.getInt("user")), result.getString("date"), result.getString("message"), result.getString("slug"), result.getBoolean("isDeleted"), result.getInt("id"));
                response.put("code", 0);
                response.set("response", thread.getThreadInfo());
            });
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
                e.printStackTrace();
                return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.USER_EXISTS));
        }
    }

    public ObjectNode details(Integer id, ArrayList related) throws JsonProcessingException, SQLException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        UserService userService = new UserService();
        ForumService forumService = new ForumService();
        ObjectNode respThread = mapper.createObjectNode();

        boolean isUser = false;
        boolean isForum = false;

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
                else
                    return null;
            }
        }

         Thread thread = Database.select("select * from threads where threads.id = "+id+";", result -> {
             result.next();
             Integer posts = 0;
             if(!result.getBoolean("isDeleted")){
                 posts = result.getInt("posts");
             }
             return new Thread(forumService.getForumOnId(result.getInt("forum")), result.getString("title"), result.getBoolean("isClosed"), userService.getEmailOnId(result.getInt("user")), result.getString("date").substring(0, result.getString("date").length() - 2), result.getString("message"), result.getString("slug"), result.getBoolean("isDeleted"), result.getInt("dislikes"), result.getInt("threads.id"), result.getInt("likes"), result.getInt("points"), posts);
         });
            respThread = thread.getThreadInfoDetails();
            Integer user_id = userService.getIdOnEmail(thread.getUser());
            Integer forum_id = forumService.getIdOnForum(thread.getForum());
            if (isUser) {
                respThread.set("user", userService.details(user_id));
            }
            if (!isUser) {
                respThread.put("user", userService.getEmailOnId(user_id));
            }
            if (isForum) {
                respThread.set("forum", forumService.details(forum_id, null));
            }
            if (!isForum) {
                respThread.put("forum", forumService.getForumOnId(forum_id));
            }
            return respThread;
    }

    public ResponseEntity closeThread(Thread thread) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode resp = mapper.createObjectNode();

        try {
            Database.update("update threads set isClosed = true where id = "+thread.getThread()+";");
            response.put("code", 0);
            resp.put("thread", thread.getThread());
            response.set("response", resp);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity listThread(String since, Integer limit, String order, String user, String forum){

        String strSince = "";
        String strLimit = "";
        String query1 = "";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        final ArrayNode resp = mapper.createArrayNode();
        UserService userService = new UserService();
        ForumService forumService = new ForumService();

        if (since != null)
            strSince = "and threads.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;


        try {
            if (user != null){
                query1 = " threads.user = \'" + userService.getIdOnEmail(user) + "\' ";
            }
            else{
                query1 = " threads.forum = \'" + forumService.getIdOnForum(forum) + "\' ";
            }
            Database.select("select * from threads where " + query1 + " " + strSince + " ORDER BY date " + order + strLimit + ";", result -> {
                while (result.next()) {
                    Thread thread = new Thread(forumService.getForumOnId(result.getInt("forum")), result.getString("title"), result.getBoolean("isClosed"), userService.getEmailOnId(result.getInt("user")), result.getString("date").substring(0, result.getString("date").length() - 2), result.getString("message"), result.getString("slug"), result.getBoolean("isDeleted"), result.getInt("dislikes"), result.getInt("threads.id"), result.getInt("likes"), result.getInt("points"), result.getInt("posts"));
                    resp.add(thread.getThreadInfoDetails());
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

    public ResponseEntity listPostsThread(String since, Integer limit, String order, String sort, int id) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        final ArrayNode resp = mapper.createArrayNode();
        UserService userService = new UserService();
        ForumService forumService = new ForumService();

        String strSince = "";
        String strLimit = "";

        if (since != null)
            strSince = "and posts.date > \'" + since + "\'";
        if (limit != null)
            strLimit = " LIMIT " + limit;

        String query = "";

        if (sort == null || sort.equals("flat")) {
            query = "select * from posts  where posts.thread = \'" + id + "\' " + strSince + " ORDER BY posts.date " + order + strLimit + ";";
        }
        else if(sort.equals("tree")){
            if (order.equals("asc"))
                query = "select * from posts join forSort using(id) where posts.thread = \'" + id + "\' order by mpath " + strLimit + ";";
            else{
                query = "select * from posts join forSort using(id) where posts.thread = \'" + id + "\' order by substring_index(mpath, '.', 1) desc, mpath asc " + strLimit + ";";
            }
        }
        else if(sort.equals("parent_tree")){
            if (order.equals("asc"))
                query = "select * from posts join forSort using(id) join (select substring_index(mpath, '.', 1) as path from posts join forSort using(id) where posts.thread = \'" + id + "\' group by path " + strLimit + ") tmp on path = substring_index(mpath, '.', 1) where posts.thread = \'" + id + "\' order by mpath";
            else{
                query = "select * from posts join forSort using(id) where posts.thread = \'" + id + "\' and substring_index(mpath, '.', 1) > (select max(substring_index(mpath, '.', 1))-" + limit + " from forSort) order by substring_index(mpath, '.', 1) desc, mpath asc";
            }
        }

        try {
            Database.select(query, result->{
                Database.select("select isDeleted from threads where id = "+id+"", result1 -> {
                    result1.next();
                    if (result1.getBoolean(1)){
                        while (result.next()){
                            Post post = new Post(result.getBoolean("isApproved"), userService.getEmailOnId(result.getInt("user")), null, result.getString("date").substring(0, result.getString("date").length() - 2), result.getString("message"), result.getBoolean("isSpam"), result.getBoolean("isHighlighted"), result.getInt("thread"), forumService.getForumOnId(result.getInt("forum")), true, result.getBoolean("isEdited"), result.getInt("dislikes"), result.getInt("likes"), result.getInt("parent"), result.getInt("points"), result.getInt("id"));
                            if (post.getParent() == 0)
                                post.setParent(null);
                            resp.add(post.getPostInfoDetails());
                        }
                    }
                    else{
                        while (result.next()){
                            Post post = new Post(result.getBoolean("isApproved"), userService.getEmailOnId(result.getInt("user")), null, result.getString("date").substring(0, result.getString("date").length() - 2), result.getString("message"), result.getBoolean("isSpam"), result.getBoolean("isHighlighted"), result.getInt("thread"), forumService.getForumOnId(result.getInt("forum")), result.getBoolean("isDeleted"), result.getBoolean("isEdited"), result.getInt("dislikes"), result.getInt("likes"), result.getInt("parent"), result.getInt("points"), result.getInt("id"));
                            if (post.getParent() == 0)
                                post.setParent(null);
                            resp.add(post.getPostInfoDetails());
                        }
                    }
                    response.put("code", 0);
                    response.set("response", resp);
                });
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

    public ResponseEntity threadOpen(Thread thread) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode resp = mapper.createObjectNode();

        try {
            Database.update("update threads set isClosed = false where id = "+thread.getThread()+";");
            response.put("code", 0);
            resp.put("thread", thread.getThread());
            response.set("response", resp);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity threadRemove(Thread thread) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode resp = mapper.createObjectNode();

        try {
            Database.update("update threads set isDeleted = true where id = "+thread.getThread()+";");
            response.put("code", 0);
            resp.put("thread", thread.getThread());
            response.set("response", resp);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity threadRestore(Thread thread) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode resp = mapper.createObjectNode();

        try {
            Database.update("update threads set isDeleted = false where id = "+thread.getThread()+";");
            response.put("code", 0);
            resp.put("thread", thread.getThread());
            response.set("response", resp);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity threadSubscribe(Thread thread) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode resp = mapper.createObjectNode();
        UserService userService = new UserService();

        try {
            Database.update("insert into subscribes VALUES (NULL, "+thread.getThread()+", "+userService.getIdOnEmail(thread.getUser())+")");
            response.put("code", 0);
            resp.put("thread", thread.getThread());
            resp.put("user", thread.getUser());
            response.set("response", resp);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity threadUnsubscribe(Thread thread) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ObjectNode resp = mapper.createObjectNode();
        UserService userService = new UserService();

        try {
            Database.update("delete from subscribes where thread = "+thread.getThread()+" and user = "+userService.getIdOnEmail(thread.getUser())+"");
            response.put("code", 0);
            resp.put("thread", thread.getThread());
            resp.put("user", thread.getUser());
            response.set("response", resp);
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

    public ResponseEntity updateThread(Thread thread) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        try {
            Database.update("update threads set message = \'"+thread.getMessage()+"\', slug = \'"+thread.getSlug()+"\' where id = "+thread.getThread()+";");
            ObjectNode resp = mapper.createObjectNode();
            response.put("code", 0);
            ThreadService threadService = new ThreadService();
            response.set("response", threadService.details(thread.getThread(), null));
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity voteThread(VoteThread thread) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        String query = "";

        if (thread.getVote() > 0)
            query = "likes = likes + 1";
        else
            query = "dislikes = dislikes + 1";

        try {
            Database.update("update threads set " + query + ", points = likes - dislikes where id = "+thread.getThread()+";");
            ObjectNode resp = mapper.createObjectNode();
            response.put("code", 0);
            ThreadService threadService = new ThreadService();
            response.set("response", threadService.details(thread.getThread(), null));
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
