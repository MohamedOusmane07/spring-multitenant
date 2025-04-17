package db.migration.masterdb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

@Slf4j
public class V3_1__Insert_into_user2_masterdb extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    Connection connection = context.getConnection();

    // Récupérer et loguer les informations sur la datasource
    DatabaseMetaData metaData = connection.getMetaData();
    String databaseUrl = metaData.getURL();
    log.info("Insertion dans la datasource : {}", databaseUrl);

    try (PreparedStatement statement =
        connection.prepareStatement("INSERT INTO main.user2 (name, email) VALUES (?, ?)")) {
      statement.setString(1, "Alice");
      statement.setString(2, "alice@example.com");
      statement.executeUpdate();

      statement.setString(1, "Bob");
      statement.setString(2, "bob@example.com");
      statement.executeUpdate();
    }
  }
}
