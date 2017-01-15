package ru.mail.park.User;

/**
 * Created by sergeigavrilko on 04.11.16.
 */
public class FollowUser {
    private String follower;
    private String followee;


    public FollowUser() {
    }

    public FollowUser(String follower, String followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public String getFollower() {
        return follower;
    }

    public String getFollowee() {
        return followee;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public void setFollowee(String followee) {
        this.followee = followee;
    }
}
