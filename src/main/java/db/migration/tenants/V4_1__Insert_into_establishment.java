package db.migration.tenants;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

@Slf4j
public class V4_1__Insert_into_establishment extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    Connection connection = context.getConnection();

    // Récupérer et loguer les informations sur la datasource
    DatabaseMetaData metaData = connection.getMetaData();
    String databaseUrl = metaData.getURL();
    log.info("Insertion dans la datasource : {}", databaseUrl);

    try (PreparedStatement statement =
        connection.prepareStatement("INSERT INTO main.establishment (name, email) VALUES (?, ?)")) {
      statement.setString(1, "ABC");
      statement.setString(2, "abc@example.com");
      statement.executeUpdate();

      statement.setString(1, "XYZ");
      statement.setString(2, "xyz@example.com");
      statement.executeUpdate();
    }
  }
}
