package calendar.api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LiquibaseConfig {

  private static final String LIQUIBASE_CHANGELOG_CLASSPATH = "classpath:";

  @Value("${spring.liquibase.changelog}")
  private String liquibaseChangelog;

  @Bean
  public DataSource dataSource() {
    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(
        "jdbc:postgresql://localhost:5432/postgres");
    hikariConfig.setUsername("user");
    hikariConfig.setPassword("password");
    hikariConfig.setDriverClassName("org.postgresql.Driver");

    return new HikariDataSource(hikariConfig);
  }


  @Primary
  @Bean(name = "liquibase")
  public SpringLiquibase liquibase(final DataSource dataSource) {
    final SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog(LIQUIBASE_CHANGELOG_CLASSPATH + liquibaseChangelog);
    return liquibase;
  }
}
