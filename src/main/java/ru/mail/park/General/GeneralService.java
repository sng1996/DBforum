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
            Database.select("SELECT CONCAT('select count(*) from ',table_name,';')\n" +
                    "FROM INFORMATION_SCHEMA.TABLES\n" +
                    "WHERE TABLE_SCHEMA = 'forum'\n" +
                    "AND TABLE_TYPE = 'BASE TABLE';", result -> {
                int i = 0;
                while(result.next()){
                    Database.select(result.getString(1), result1 -> {
                        result1.next();
                        arr.add(result1.getInt(1));
                    });
                }
            });
            statusNode.put("forum", (Integer)arr.get(0));
            statusNode.put("post", (Integer)arr.get(1));
            statusNode.put("thread", (Integer)arr.get(2));
            statusNode.put("user", (Integer)arr.get(3));
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
