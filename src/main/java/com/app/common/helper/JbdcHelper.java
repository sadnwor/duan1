package com.app.common.helper;

import com.app.common.configs.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author InuHa
 */
public class JbdcHelper {

    @Getter
    private static Integer lastInsertedId = -1;
    
    @Setter
    private static boolean debug = false;
    
    public static ResultSet query(String query, Object... args) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement stmt = null;
        try {
            stmt = getStatement(query, args);
            resultSet = stmt.executeQuery();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return resultSet;
    }

    public static int update(String query, Object... args) throws SQLException {
        return update(query, false, args);
    }

    public static int updateAndFlush(String query, Object... args) throws SQLException {
        return update(query, true, args);
    }

    private static int update(String query, boolean autoCloseConnect, Object... args) throws SQLException {
        int result = 0;
        PreparedStatement stmt = null;

        try {
            stmt = getStatement(query, args);
            result = stmt.executeUpdate();
	    
	    if (query.trim().toUpperCase().startsWith("INSERT")) {
		try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
		    if (generatedKeys.next()) {
			lastInsertedId = generatedKeys.getInt(1);
		    }
		}
	    } else {
		lastInsertedId = -1;
	    }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        finally {
            if (autoCloseConnect) {
                assert stmt != null;
                close(stmt);
            }
        }
        return result;
    }

    public static Object value(String query, Object... args) throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = query(query, args);
            if (resultSet.next()) {
                return resultSet.getObject(1);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            close(resultSet);
        }
        return null;
    }

    public static boolean has(String query, Object... args) throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = query(query, args);
            return resultSet.next();
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            close(resultSet);
        }
        return false;
    }

    public static PreparedStatement getStatement(String query, Object... args) throws SQLException {
        Connection connection = DBConnect.getInstance().getConnect();
        PreparedStatement stmt = null;

        if (query.trim().startsWith("{")) {
            String sql = "{CALL " + getProcedureName(query) + "(";
            if (args.length > 0) {
                String[] fill = new String[args.length];
                Arrays.fill(fill, "?");
                sql += String.join(",", fill);
            }
            sql += ")}";
            stmt = connection.prepareCall(sql);
        } else {
            stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        }

        for (int i = 0; i < args.length; i++) {
            stmt.setObject(i + 1, args[i]);
        }
	
	if (debug) {
	    System.out.println("\n\n" + String.format(query.replaceAll("\\?", "%s"), args));
	    System.out.println("\n\n");
	}

        return stmt;
    }

    public static String getProcedureName(String query) {
        query = query.trim();
        String regex = "\\{\\s*(?:CALL )?\\s*([^(\\}\\s+]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            return matcher.group(1).trim();
        } 
        return null;
    }

    public static void close(AutoCloseable close) {
        try {
            DBConnect.getInstance().close(close);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
