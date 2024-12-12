package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;

public class HikariConnectionPool {
    private static HikariDataSource dataSource;

    static {
        try {
            // Указываем путь к целевой базе данных
            String dbFileName = "database.sqlite";
            Path targetPath = Paths.get(System.getProperty("user.home"), "currenciesapp", dbFileName);

            // Проверяем, существует ли база данных, и копируем её, если нужно
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath.getParent()); // Создаем каталог, если его нет
                try (InputStream in = HikariConnectionPool.class.getResourceAsStream("/" + dbFileName)) {
                    if (in == null) {
                        throw new RuntimeException("Файл базы данных не найден в ресурсах.");
                    }
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlite:" + targetPath.toAbsolutePath());
            config.setDriverClassName("org.sqlite.JDBC");
            config.setMaximumPoolSize(5);
            config.setConnectionTimeout(10000);

            dataSource = new HikariDataSource(config);
        } catch (NullPointerException | IOException e) {
            throw new RuntimeException("Не удалось загрузить базу данных.");
        }
    }

    public static Connection getConnection() {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка в соединении с БД");
        }
    }
}
