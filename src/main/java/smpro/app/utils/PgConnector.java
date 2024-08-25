package smpro.app.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.postgresql.jdbc.PgArray;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateRevokedException;
import java.sql.*;
import java.util.*;


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


    public static List<HashMap<String, Object>> fetch(String query,Connection c)  {
        System.out.println(query);
        List<HashMap<String, Object>> data = new ArrayList<>();

        try {
            Statement statement = c.createStatement();
            ResultSet rs = statement.executeQuery(query);


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



        } catch (SQLException err) {
            System.err.println(err.getLocalizedMessage());
        }

        return data;



    }
    public static void insert(String query) {

        try {
            System.out.println(query);
            Statement statement = connection.get().createStatement();
            statement.execute(query);

            statement.close();
            System.out.printf("insert successfull");

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();

        }


    }
    public static void update(String query)  {
        System.out.println(query);
        try {
            Statement statement = connection.get().createStatement();
            statement.executeUpdate(query);

            statement.close();

            System.out.println("update successfull");

        } catch (SQLException err) {
            System.err.println(err.getLocalizedMessage());
            err.printStackTrace();

        }

    }


    public static List<String> listHashAttrs(List<HashMap<String, Object>> items,String key) {

        List<String> out = new ArrayList<>();
        for (HashMap<String, Object> item : items) {
            Object value = item.get(key);
            out.add(String.valueOf(value));

        }

        return out;

    }

    public static boolean insertBinaryData(PreparedStatement ps)  {


        try {
            System.out.println(ps.toString());
            ps.execute();
            System.out.println("binary data inserted");
            return true;

        } catch (SQLException err) {
            err.printStackTrace();
        }

        return false;




    }

    public static InputStream readBinarydata( PreparedStatement ps) {
        System.out.println("===============Inserting binary data ============== \n");
        InputStream is = null;

        try {
            ResultSet rs =  ps.executeQuery();

            if (rs.next()) {
                System.out.println("file data found");
                if (!Objects.equals(null,rs.getBinaryStream(1))) is = rs.getBinaryStream(1);
            }
            ps.close();
        } catch (SQLException err) {
            err.printStackTrace();

        }


        return is;


    }


    public static void switchDbConnection(String newdbName) throws SQLException {
        Connection newcon =   initConnect(newdbName, dbHost);
        connection.set(newcon);

        System.out.println("Database switched to =>"+newdbName);


    }


    public static String getFielorBlank(HashMap<String, Object> obj, String key) {

        Object val = obj.get(key);
        if (Objects.equals(null,val)) return "";

        return String.valueOf(val);

    }
    public static Number getNumberOrNull(HashMap<String, Object> obj, String key) {

        Object val = obj.get(key);
        if (Objects.equals(null,val)) return null;

        return (Number) val;

    }

    public static List<String> parsePgArray(ResultSet rs, String key) {
        List<String> out;
        try {
            Array pglist = rs.getArray(key);
            String[] stringArray = (String[]) pglist.getArray();
            out = Arrays.stream(stringArray).toList();


        } catch (SQLException e) {
            out = new ArrayList<>();
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
        return out;
    }










}
