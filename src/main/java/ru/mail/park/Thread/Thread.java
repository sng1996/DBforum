package ru.mail.park.Thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Timestamp;

/**
 * Created by sergeigavrilko on 08.11.16.
 */
public class Thread {

    private String forum;
    private String title;
    private boolean isClosed;
    private String user;
    private Timestamp tmpDate;
    private String date;
    private String message;
    private String slug;
    private boolean isDeleted;
    private int dislikes;
    private int id;
    private int likes;
    private int points;
    private int posts;
    private int vote;
    private int thread;

    ObjectMapper mapper = new ObjectMapper();

    public Thread() {
    }


    public Thread(String forum, String title, boolean isClosed, String user, String date, String message, String slug, boolean isDeleted, int id) {
        this.forum = forum;
        this.title = title;
        this.isClosed = isClosed;
        this.user = user;
        this.date = date;
        this.message = message;
        this.slug = slug;
        this.isDeleted = isDeleted;
        this.id = id;
    }

    public Thread(String forum, String title, boolean isClosed, String user, String date, String message, String slug, boolean isDeleted, int dislikes, int id, int likes, int points, int posts) {
        this.forum = forum;
        this.title = title;
        this.isClosed = isClosed;
        this.user = user;
        this.date = date;
        this.message = message;
        this.slug = slug;
        this.isDeleted = isDeleted;
        this.dislikes = dislikes;
        this.id = id;
        this.likes = likes;
        this.points = points;
        this.posts = posts;
    }

    public String getForum() {
        return forum;
    }

    public String getTitle() {
        return title;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public Timestamp getTmpDate() { return tmpDate; }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public int getDislikes() {return dislikes;}

    public int getId() {return id;}

    public int getLikes() {return likes;}

    public int getPoints() {return points;}

    public int getPosts() {return posts;}

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public void setTmpDate(Timestamp tmpDate) {
        this.tmpDate = tmpDate;
    }

    public ObjectNode getThreadInfo(){
        final ObjectNode threadInfoResponse = mapper.createObjectNode();
        threadInfoResponse.put("date", date);
        threadInfoResponse.put("forum", forum);
        threadInfoResponse.put("id", id);
        threadInfoResponse.put("isClosed", isClosed);
        threadInfoResponse.put("isDeleted", isDeleted);
        threadInfoResponse.put("message", message);
        threadInfoResponse.put("slug", slug);
        threadInfoResponse.put("title", title);
        threadInfoResponse.put("user", user);
        return threadInfoResponse;
    }

    public ObjectNode getThreadInfoDetails(){
        final ObjectNode threadInfoResponse = mapper.createObjectNode();
        threadInfoResponse.put("date", date);
        threadInfoResponse.put("forum", forum);
        threadInfoResponse.put("id", id);
        threadInfoResponse.put("isClosed", isClosed);
        threadInfoResponse.put("isDeleted", isDeleted);
        threadInfoResponse.put("message", message);
        threadInfoResponse.put("slug", slug);
        threadInfoResponse.put("title", title);
        threadInfoResponse.put("user", user);
        threadInfoResponse.put("likes", likes);
        threadInfoResponse.put("dislikes", dislikes);
        threadInfoResponse.put("points", points);
        threadInfoResponse.put("posts", posts);
        return threadInfoResponse;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }
}
