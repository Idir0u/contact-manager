package com.seomaniak.contact.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        HikariConfig config = new HikariConfig();
        
        // Si DATABASE_URL existe (Railway), le convertir en format JDBC
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Convertir postgresql:// en jdbc:postgresql://
            if (databaseUrl.startsWith("postgresql://")) {
                databaseUrl = "jdbc:" + databaseUrl;
            } else if (databaseUrl.startsWith("postgres://")) {
                databaseUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
            }
            
            String username = System.getenv("PGUSER");
            String password = System.getenv("PGPASSWORD");
            
            config.setJdbcUrl(databaseUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(5);
            config.setConnectionTimeout(30000);
            
            System.out.println("✅ PostgreSQL DataSource configuré pour Railway");
            System.out.println("📊 URL: " + databaseUrl.replaceAll(":[^:@]+@", ":***@"));
            
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
