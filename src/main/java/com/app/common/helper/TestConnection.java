package com.app.common.helper;

import com.app.common.configs.DBConnect;

import java.sql.SQLException;

/**
 *
 * @author InuHa
 */
public class TestConnection {

    public static boolean test() {
        try {
            DBConnect.getInstance().connectToDatabase();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}