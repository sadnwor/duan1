package com.app.common.configs;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author InuHa
 */
public class DBConnect {

    private final static String HOSTNAME = System.getProperty("localhost");

    private final static String PORT = System.getProperty("1433");

    private final static String DATABASE = System.getProperty("DuAn1");

    private final static String USERNAME = System.getProperty("sa");

    private final static String PASSWORD = System.getProperty("111111");

    private static DBConnect instance;

    private BasicDataSource dataSource;

    private DBConnect() {

    }

    public static DBConnect getInstance() {
        if (instance == null) {
            instance = new DBConnect();
        }
        return instance;
    }

    public void connectToDatabase() throws SQLException {
        String url = "jdbc:sqlserver://"
                + HOSTNAME + ":"
                + PORT + ";databaseName="
                + DATABASE + ";encrypt=true;"
                + "trustServerCertificate=true;";
        this.dataSource = new BasicDataSource();
        this.dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        this.dataSource.setUrl(url);
        this.dataSource.setUsername(USERNAME);
        this.dataSource.setPassword(PASSWORD);
        this.dataSource.start();
    }

    public static void connect() {
        try {
            getInstance().connectToDatabase();
            System.out.println("Connection success...");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public Connection getConnect() throws SQLException {
        return this.dataSource.getConnection();
    }

    public void close(AutoCloseable close) throws SQLException {

        if (close != null) {
            try {
                Statement stmt = null;
                ResultSet resultSet = null;
                Connection connection = null;

                if (close instanceof ResultSet) {
                    resultSet = ((ResultSet) close);
                    stmt = resultSet.getStatement();
                    connection = stmt.getConnection();
                } else if (close instanceof Statement) {
                    stmt = ((Statement) close);
                    resultSet = stmt.getResultSet();
                    connection = stmt.getConnection();
                } else if (close instanceof Connection) {
                    connection = ((Connection) close);
                }

                if (resultSet != null) {
                    resultSet.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                if (connection != null) {
                    connection.close();
                }

            } catch (Exception e) {
                throw new SQLException(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        connect();
    }
}
