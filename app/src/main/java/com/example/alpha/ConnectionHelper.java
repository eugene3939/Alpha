package com.example.alpha;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionHelper {
    Connection connection;
    String uname, pass, ip, port, database;

    @SuppressLint("NewApi")

    public Connection connectionClass(){
        ip = "10.60.200.13";
        database = "kgpos_test";
        uname = "kgpos";
        pass = "admkgpos";
        port = "1433";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection con = null;
        String ConnectionUrl = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + database + ";user=" + uname + ";password=" + pass + ";";
            Log.d("連線~啟動!","ConnectionUrl: " + ConnectionUrl );
            con = DriverManager.getConnection(ConnectionUrl);
            if (con!=null)
                Log.d("嘿ya!","連線成功");
            else
                Log.d("2","連接失敗");
        }catch (Exception e){
            e.printStackTrace();
            Log.e("錯誤", Objects.requireNonNull(e.getMessage()));
        }

        return con;
    }
}