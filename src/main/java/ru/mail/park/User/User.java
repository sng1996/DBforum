package ru.mail.park.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class User {
    private String username;
    private String about;
    private boolean isAnonymous;
    private String name;
    private String email;
    private int id;

    ObjectMapper mapper = new ObjectMapper();

    public User() {
    }

    public User(String username, String about, boolean isAnonymous, String name, String email, int id) {
        this.username = username;
        this.about = about;
        this.isAnonymous = isAnonymous;
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getAbout() {
        return about;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setIsAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ObjectNode getUserInfo(){
        final ObjectNode userInfoResponse = mapper.createObjectNode();
        userInfoResponse.put("username", username);
        userInfoResponse.put("name", name);
        userInfoResponse.put("about", about);
        userInfoResponse.put("isAnonymous", isAnonymous);
        userInfoResponse.put("email", email);
        userInfoResponse.put("id", id);
        return userInfoResponse;
    }

    public ObjectNode getUserInfoWithDetails(ArrayNode followers, ArrayNode followees, ArrayNode subscriptions){
        final ObjectNode userInfoResponse = mapper.createObjectNode();
        userInfoResponse.put("username", username);
        userInfoResponse.put("name", name);
        userInfoResponse.put("about", about);
        userInfoResponse.put("isAnonymous", isAnonymous);
        userInfoResponse.put("email", email);
        userInfoResponse.put("id", id);
        userInfoResponse.set("followers", followers);
        userInfoResponse.set("following", followees);
        userInfoResponse.set("subscriptions", subscriptions);
        return userInfoResponse;
    }
}
