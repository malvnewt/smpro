package smpro.app.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.postgresql.util.PGobject;

import java.io.InputStream;
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
        return connection.get();
    }
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


        } catch (SQLException err) {
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
        System.out.println(ps);
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

            String[] stringArray = new String[]{};

            if (pglist != null) {
                stringArray = (String[]) pglist.getArray();
            }

            out = Arrays.stream(stringArray).toList();


        } catch (SQLException e) {
            out = new ArrayList<>();
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
        return out;
    }

    public static HashMap<String, String> parsePgMapString(String pgMap, String... replacement) {

        HashMap<String, String> map = new HashMap<>();
        String bracesFreeString = pgMap.replaceAll("\\{", "").replaceAll("}", "");
        List<String> equalToSeperatedPairs = Arrays.stream(bracesFreeString.split(",")).toList();
        for (String pair : equalToSeperatedPairs) {
            String k = pair.strip().split("=")[0].replace(" ", "");
            String v = pair.strip().split("=")[1].replace(" ", "");
            if (replacement.length > 0) {
                k = k.replaceAll(replacement[0], "");
                v = v.replaceAll(replacement[0], "");
            }
            if (!map.containsKey(k)) {
                map.put(k, v);
            } else map.replace(k, v);
        }

        return map;
    }

    public static PGobject getJsonbObject(HashMap<?,?> map) {

        PGobject pGobject = new PGobject();
        pGobject.setType("jsonb");
        try {
            pGobject.setValue(new Gson().toJson(map.toString()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pGobject;
    }

    public static HashMap<String,String> getMapFromJsonB(PGobject jsonb) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(jsonb.getValue(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        HashMap<String,String> map = new HashMap<>();

        for (String k : map.keySet()) {
            if (!map.containsKey(k)) {
                map.put(k, jsonObject.get(k).getAsString());
            }else map.replace(k, jsonObject.get(k).getAsString());
        }

        return map;

    }

    public static List<String> aggregatePgArray(ResultSet rs, String key) {
        HashSet<String> set = new HashSet<>();
        while (true) {
            try {
                if (!rs.next()) break;

                List<String> array = parsePgArray(rs, key);
                set.addAll(array);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return new ArrayList<>(set);

    }

    public static List<String> getAggregatedListIfValueInPgArray( ResultSet rs,String pgListColname, String keyToAggregate, String listValueTocheck) throws SQLException {
        HashSet<String> list = new HashSet<>();


        while (rs.next()) {
                List<String> pglist = parsePgArray(rs,pgListColname );
                String keyval = String.valueOf(rs.getObject(keyToAggregate));
                ;
                if (pglist.contains(listValueTocheck)) list.add(keyval);

        }

        return new ArrayList<>(list);

    }




    public static HashMap<String, Object> getObjectFromId(int id, String tablename) {
        String q = String.format("""
                select * from "%s" where id=%d""", tablename, id);
        List<HashMap<String, Object>> res = fetch(q, getConnection());
        if (res.isEmpty()) return null;
        return res.get(0);
    }
    public static HashMap<String, Object> getObjectFromKey(String key,String value, String tablename) {

        String q = String.format("""
                select * from "%s" where %s='%s' """, tablename, key,value);
        List<HashMap<String, Object>> res = fetch(q, getConnection());
        if (res.isEmpty()) return null;
        return res.get(0);
    }


    //HR SERVICE
    public static Number aggregateNumericFieldsAndSum(List<HashMap<String, Object>> list, String numberKey) {
        double sum = 0;

        for (HashMap<String, Object> item : list) {
            Number n = getNumberOrNull(item, numberKey);
            if (!Objects.equals(null,n)) sum += n.doubleValue();
        }
        return sum;
    }










}
