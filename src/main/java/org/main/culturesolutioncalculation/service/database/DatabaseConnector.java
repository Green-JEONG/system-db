package org.main.culturesolutioncalculation.service.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static DatabaseConnector instance;
    private static HikariDataSource dataSource;

    private DatabaseConnector(String url, String user, String password) {
        // HikariCP 설정
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        // 선택적으로 HikariCP 설정 추가
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // 풀 크기 관련 설정
        config.setMinimumIdle(5); // 최소 유휴 커넥션 수
        config.setMaximumPoolSize(15); // 최대 커넥션 수

        // 풀 이름을 설정
        config.setPoolName("MyHikariCPPool");

        // HikariDataSource 객체 생성
        dataSource = new HikariDataSource(config);
        System.out.println("Successfully connected to the database via HikariCP.");
    }

    // 싱글톤 인스턴스 반환
    public static DatabaseConnector getInstance(String url, String user, String password) {
        if (instance == null) {
            instance = new DatabaseConnector(url, user, password);
        }
        return instance;
    }

    // DataSource로부터 커넥션 가져오기
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting a database connection from the pool.", e);
        }
    }

    // 풀을 닫아 커넥션 해제
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Disconnected from the database via HikariCP.");
        }
    }
}
