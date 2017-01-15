package ru.mail.park.Forum;


import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Forum {

    private String name;
    private String short_name;
    private String user;
    private int id;

    ObjectMapper mapper = new ObjectMapper();

    public Forum(){
    }

    public Forum(String name, String short_name, String user, int id){
        this.name = name;
        this.short_name = short_name;
        this.user = user;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getUser() {
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ObjectNode getForumInfo(){
        final ObjectNode forumInfoResponse = mapper.createObjectNode();
        forumInfoResponse.put("name", name);
        forumInfoResponse.put("short_name", short_name);
        forumInfoResponse.put("user", user);
        forumInfoResponse.put("id", id);
        return forumInfoResponse;
    }

}
