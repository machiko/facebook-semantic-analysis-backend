package cc.openhome;

import java.io.File;
import java.sql.*;
//import org.sqlite.JDBC;

public class SQLiteTest {
    public static void main(String[] args){
        System.out.println("This is a SqliteTest program!");
         

        //載入資料庫驅動
        try{
        Class.forName("org.sqlite.JDBC");
        System.out.println("Load sqlite Driver sucess!");
        }
        catch(java.lang.ClassNotFoundException e){
        System.out.println("Fail to Load sqlite Driver!");
        System.out.println(e.getMessage());
        }



        try{
        //連接資料庫
        String connectionString = "jdbc:sqlite:./test.db";
        Connection cn = DriverManager.getConnection(connectionString);
         

        //SQL語句類
        System.out.println("Connect sucessfully!");
        Statement stmt = cn.createStatement();
         

        //創建資料庫
        File testdb = new File("test.db");
        if(testdb.exists()) testdb.delete(); //若有舊的則刪除之
         

        //創建表
        stmt.execute("CREATE TABLE test(id integer primary key, name char(10))");
        //插入資料
        stmt.execute("INSERT INTO test(id, name) VALUES(1, '張三')");
        stmt.execute("INSERT INTO test(id, name) VALUES(2, '李四')");
        //查詢
        ResultSet rs = stmt.executeQuery("SELECT * FROM test");
        while(rs.next()){
        String id = rs.getString("id");
        String name = rs.getString("name");
        System.out.println("id is " + id + " name is " + name);
        }



        //關閉
//        stmt.close();
//        cn.close();
        }
        catch(SQLException e){
        System.out.println("Fail！");
        System.out.println(e.getMessage());
        }
        }
}
