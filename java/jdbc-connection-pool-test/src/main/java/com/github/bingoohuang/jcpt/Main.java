package com.github.bingoohuang.jcpt;

import java.util.Scanner;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

public class Main {
    private static final DruidDataSource ds = new DruidDataSource();
    static {
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
    }

    @SneakyThrows
    public static void main(String[] args) {
        String sql = "select * from t1";

        @Cleanup
        val input = new Scanner(System.in);
        while (input.hasNextLine()) {
            input.nextLine();

            try (val conn = ds.getConnection(); val stmt = conn.createStatement(); val rs = stmt.executeQuery(sql)) {

                val rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    System.out.print(rsmd.getColumnName(i));
                }
                System.out.println();

                while (rs.next()) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        if (i > 1)
                            System.out.print(",  ");
                        System.out.print(rs.getString(i));
                    }
                    System.out.println();
                }
            }
        }
    }
}