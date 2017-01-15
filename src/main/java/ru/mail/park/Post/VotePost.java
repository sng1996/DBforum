package ru.mail.park.Post;

/**
 * Created by sergeigavrilko on 14.11.16.
 */
public class VotePost {

    private Integer vote;
    private Integer post;

    public VotePost(Integer vote, Integer post) {
        this.vote = vote;
        this.post = post;
    }

    public VotePost() {
    }

    public Integer getVote() {
        return vote;
    }

    public Integer getPost() {
        return post;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public void setPost(Integer post) {
        this.post = post;
    }
}
