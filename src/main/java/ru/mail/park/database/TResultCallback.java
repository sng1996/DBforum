package ru.mail.park.database;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sergeigavrilko on 12.01.17.
 */
public interface TResultCallback<T> {

    T call(ResultSet result) throws SQLException, JsonProcessingException;

}
