package ru.mail.park.General;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.Database;
import ru.mail.park.errors.Errors;

import java.sql.*;
import java.util.ArrayList;

@Service
@Transactional
public class GeneralService {

    public ResponseEntity clear() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        try {
            Database.select("SELECT CONCAT('truncate table ',table_name,';')\n" +
                    "FROM INFORMATION_SCHEMA.TABLES\n" +
                    "WHERE TABLE_SCHEMA = 'dbforum'\n" +
                    "AND TABLE_TYPE = 'BASE TABLE';", result -> {
                while (result.next()) {
                    Database.update(result.getString(1));
                }
                response.put("code", 0);
                response.put("response", "OK");
            });
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }

    public ResponseEntity status() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        ArrayList arr = new ArrayList();
        ObjectNode statusNode = mapper.createObjectNode();

        try {
            Database.select("SELECT COUNT(*) count FROM users", result -> {
                if (result.next()) {
                    statusNode.put("user", result.getInt("count"));
                }
            });

            Database.select("SELECT COUNT(*) count FROM threads", result -> {
                if (result.next()) {
                    statusNode.put("thread", result.getInt("count"));
                }
            });

            Database.select("SELECT COUNT(*) count FROM forums", result -> {
                if (result.next()) {
                    statusNode.put("forum", result.getInt("count"));
                }
            });

            Database.select("SELECT COUNT(*) count FROM posts", result -> {
                if (result.next()) {
                    statusNode.put("post", result.getInt("count"));
                }
            });

            response.put("code", 0);
            response.set("response", statusNode);
            return ResponseEntity.ok().body(mapper.writeValueAsString(response));


        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.OBJECT_NOT_FOUND));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(Errors.getJson(Errors.UNKNOWN_ERROR));
        }
    }
}
