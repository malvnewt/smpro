package smpro.app.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateRevokedException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


public class PgConnector {
    public  static String baseDbname = "smpro_base";
    public static  String virginDbname = "smpro_virgin";
    public static  String dbPassword = "postgres";
    public static  String dbUsername = "postgres";
    public  static int dbPort = 5432;
    public static  String dbHost = "192.168.100.10";


   public static ObjectProperty<Connection> baseConnection = new SimpleObjectProperty<>();
   public static ObjectProperty<Connection> connection = new SimpleObjectProperty<>();


    public static Connection initConnect(String dbname, String dbHost) throws SQLException {
         String connectionUrl = String.format("jdbc:postgresql://%s:%d/%s", dbHost, dbPort, dbname);

        Properties properties = new Properties();
        properties.setProperty("user", dbUsername);
        properties.setProperty("password", dbPassword);

        Connection conn = null;
        Connection baseconn = null;

        try {
             conn = DriverManager.getConnection(connectionUrl, properties);

        } catch (Exception err) {
            conn = DriverManager.getConnection(connectionUrl.replace(dbHost,"localhost"), properties);

        }

        return conn;

    }


    public static Connection getConnection() {
        return connection.get();}
    public static Connection getBaseConnection() {
        return baseConnection.get();}


    public static List<HashMap<String, Object>> fetch(String query,Connection c) throws SQLException {
        System.out.println(query);

        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery(query);

        List<HashMap<String, Object>> data = new ArrayList<>();

        while (rs.next()) {
            HashMap<String, Object> rdata = new HashMap<>();
            int colcount = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= colcount; i++) {
                String colname = rs.getMetaData().getColumnName(i);
                Object coldata = rs.getObject(i);
                rdata.put(colname, coldata);
            }

            data.add(rdata);

        }

        System.out.println("FETCH RETURN " + data.size() + " records");

        statement.close();
        rs.close();
       return data;
    }
    public static void insert(String query) throws SQLException {
        System.out.println(query);
        Statement statement = connection.get().createStatement();
        statement.execute(query);

        statement.close();
        connection.get().commit();
        System.out.printf("insert successfull");

    }
    public static void update(String query) throws SQLException {
        System.out.println(query);

        Statement statement = connection.get().createStatement();
        statement.executeQuery(query);

        statement.close();
        connection.get().commit();

        System.out.println("update successfull");
    }


    public static List<String> listHashAttrs(List<HashMap<String, Object>> items,String key) {

        List<String> out = new ArrayList<>();
        for (HashMap<String, Object> item : items) {
            Object value = item.get(key);
            out.add(String.valueOf(value));

        }

        return out;

    }

    public static void insertBinaryData(InputStream is, String table,String imagecolname, int itemid) throws SQLException, IOException {

        PreparedStatement ps = connection.get().prepareStatement(String.format("""
                update "%s" set "%s"=? where id=%d
                """, table, imagecolname, itemid));

        ps.setBinaryStream(1, is,is.readAllBytes().length);

        ps.executeUpdate();

        ps.close();

        System.out.println("binary data inserted");
        connection.get().commit();





    }

    public static InputStream readBinarydata( String table,String imagecolname, int itemid) throws SQLException, IOException {
        InputStream is = null;

        PreparedStatement ps = connection.get().prepareStatement(String.format("""
                select "%s" from "%s" where id=%d
                """, imagecolname, table, itemid));

        ps.execute();

        ResultSet rs = ps.getResultSet();

        if (rs.first()) {
            is = rs.getBinaryStream(imagecolname);
        }



        ps.executeUpdate();

        ps.close();

        System.out.println("file read");

        return is;





    }










}
