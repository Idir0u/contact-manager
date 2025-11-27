package com.seomaniak.contact.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        // Priorité à DATABASE_PUBLIC_URL (Railway public), sinon DATABASE_URL
        String databaseUrl = System.getenv("DATABASE_PUBLIC_URL");
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            databaseUrl = System.getenv("DATABASE_URL");
        }
        
        HikariConfig config = new HikariConfig();
        
        // Si une URL de base de données existe (Railway)
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                // Parser l'URL PostgreSQL (format: postgresql://user:pass@host:port/db)
                URI uri = new URI(databaseUrl.replace("postgres://", "postgresql://"));
                
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String database = uri.getPath().substring(1); // Retirer le '/' initial
                String userInfo = uri.getUserInfo();
                
                String username = null;
                String password = null;
                
                if (userInfo != null && userInfo.contains(":")) {
                    String[] credentials = userInfo.split(":", 2);
                    username = credentials[0];
                    password = credentials[1];
                }
                
                // Construire l'URL JDBC proprement
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", host, port, database);
                
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                config.setMaximumPoolSize(5);
                config.setMinimumIdle(2);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);
                
                System.out.println("✅ PostgreSQL DataSource configuré pour Railway");
                System.out.println("📊 Host: " + host);
                System.out.println("📊 Port: " + port);
                System.out.println("📊 Database: " + database);
                System.out.println("📊 Username: " + username);
                
            } catch (URISyntaxException e) {
                throw new RuntimeException("Erreur lors du parsing de DATABASE_URL: " + e.getMessage(), e);
            }
            
        } else {
            // H2 par défaut (développement local)
            config.setJdbcUrl("jdbc:h2:mem:contactdb");
            config.setUsername("sa");
            config.setPassword("");
            config.setDriverClassName("org.h2.Driver");
            
            System.out.println("🔧 H2 DataSource configuré pour développement local");
        }
        
        return new HikariDataSource(config);
    }
}
