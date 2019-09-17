package com.github.bingoohuang.jcpt;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

public class Main {
    private static final DruidDataSource ds = createDruidDataSource();

    @SneakyThrows
    public static void main(String[] args) {
        @Cleanup
        val input = new Scanner(System.in);
        for (; input.hasNextLine(); input.nextLine()) {
            executeSQL("select * from t1");
        }
    }

    @SneakyThrows
    private static void executeSQL(String sql) {
        @Cleanup
        val conn = ds.getConnection();
        @Cleanup
        val stmt = conn.createStatement();
        @Cleanup
        val rs = stmt.executeQuery(sql);

        val rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        printColumnNames(rsmd, columnsNumber);

        while (rs.next()) {
            printRow(rs, columnsNumber);
        }
    }

    @SneakyThrows
    private static void printRow(ResultSet rs, int columnsNumber) {
        System.out.print(rs.getString(1));
        for (int i = 2; i <= columnsNumber; i++) {
            System.out.print(",  " + rs.getString(i));
        }
        System.out.println();
    }

    @SneakyThrows
    private static void printColumnNames(ResultSetMetaData rsmd, int columnsNumber) {
        System.out.print(rsmd.getColumnName(1));
        for (int i = 2; i <= columnsNumber; i++) {
            System.out.print(",  " + rsmd.getColumnName(i));
        }
        System.out.println();
    }

    public static DruidDataSource createDruidDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://127.0.0.1:13303/bjca?useSSL=false&useUnicode=true"
                + "&characterEncoding=UTF-8&connectTimeout=3000&socketTimeout=3000&autoReconnect=true");
        ds.setUsername("root");
        ds.setPassword("root");
        ds.setInitialSize(2);
        ds.setMinIdle(0);
        ds.setMaxActive(20);

        ds.setMaxWait(6000);
        ds.setTimeBetweenEvictionRunsMillis(6000);
        ds.setMinEvictableIdleTimeMillis(30000);
        ds.setTestWhileIdle(true);
        ds.setTestOnBorrow(false);
        ds.setTestOnReturn(false);

        return ds;
    }
}