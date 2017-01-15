package ru.mail.park.Post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Timestamp;

/**
 * Created by sergeigavrilko on 08.11.16.
 */
public class Post {

    private boolean isApproved;
    private String user;
    private Timestamp tmpDate;
    private String date;
    private String message;
    private boolean isSpam;
    private boolean isHighlighted;
    private Integer thread;
    private String forum;
    private boolean isDeleted;
    private boolean isEdited;
    private Integer dislikes;
    private Integer likes;
    private Integer parent;
    private Integer points;
    private Integer id;
    private Integer vote;
    private Integer post;

    ObjectMapper mapper = new ObjectMapper();

    public Post(){
    }

    public Post(boolean isApproved, String user, Timestamp tmpDate, String date, String message, boolean isSpam, boolean isHighlighted, Integer thread, String forum, boolean isDeleted, boolean isEdited, Integer dislikes, Integer likes, Integer parent, Integer points, Integer id) {
        this.isApproved = isApproved;
        this.user = user;
        this.tmpDate = tmpDate;
        this.date = date;
        this.message = message;
        this.isSpam = isSpam;
        this.isHighlighted = isHighlighted;
        this.thread = thread;
        this.forum = forum;
        this.isDeleted = isDeleted;
        this.isEdited = isEdited;
        this.dislikes = dislikes;
        this.likes = likes;
        this.parent = parent;
        this.points = points;
        this.id = id;
    }

    public ObjectNode getPostInfo() {
        final ObjectNode forumInfoResponse = mapper.createObjectNode();
        forumInfoResponse.put("date", date);
        forumInfoResponse.put("forum", forum);
        forumInfoResponse.put("isApproved", isApproved);
        forumInfoResponse.put("isEdited", isEdited);
        forumInfoResponse.put("isHighlighted", isHighlighted);
        forumInfoResponse.put("isSpam", isSpam);
        forumInfoResponse.put("message", message);
        forumInfoResponse.put("parent", parent);
        forumInfoResponse.put("thread", thread);
        forumInfoResponse.put("user", user);
        forumInfoResponse.put("id", id);
        forumInfoResponse.put("isDeleted", isDeleted);
        return forumInfoResponse;
    }

    public ObjectNode getPostInfoDetails() {
        final ObjectNode forumInfoResponse = mapper.createObjectNode();
        forumInfoResponse.put("date", date);
        forumInfoResponse.put("forum", forum);
        forumInfoResponse.put("isApproved", isApproved);
        forumInfoResponse.put("isEdited", isEdited);
        forumInfoResponse.put("isHighlighted", isHighlighted);
        forumInfoResponse.put("isSpam", isSpam);
        forumInfoResponse.put("message", message);
        forumInfoResponse.put("parent", parent);
        forumInfoResponse.put("thread", thread);
        forumInfoResponse.put("user", user);
        forumInfoResponse.put("id", id);
        forumInfoResponse.put("isDeleted", isDeleted);
        forumInfoResponse.put("dislikes", dislikes);
        forumInfoResponse.put("likes", likes);
        forumInfoResponse.put("points", points);
        return forumInfoResponse;
    }


    public boolean getIsApproved() {
        return isApproved;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public Timestamp getTmpDate() {
        return tmpDate;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsSpam() {
        return isSpam;
    }

    public boolean getIsHighlighted() {
        return isHighlighted;
    }

    public Integer getThread() {
        return thread;
    }

    public String getForum() {
        return forum;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public boolean getIsEdited() {
        return isEdited;
    }

    public Integer getDislikes() {return dislikes;}

    public Integer getLikes() {return likes;}

    public Integer getParent() {return parent;}

    public Integer getPoints() {return points;}

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
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

    public void setIsSpam(boolean isSpam) {
        this.isSpam = isSpam;
    }

    public void setIsHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setIsEdited(boolean isEdited) { this.isEdited = isEdited; }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTmpDate(Timestamp tmpDate) {
        this.tmpDate = tmpDate;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Integer getVote() { return vote; }

    public void setVote(Integer vote) { this.vote = vote; }

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }
}
