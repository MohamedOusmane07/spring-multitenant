package net.mohamed.springmultitenant.streams;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.tenant.config.DataSourceConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaTenantTopicProvisioner {

  private final KafkaAdmin kafkaAdmin;
  private final DataSourceConfig dataSourceConfig;

  @Bean
  public ApplicationRunner createTenantTopics() {
    return args -> {
      log.info("Creating tenant topics");
      List<String> tenantIds = dataSourceConfig.getAllTenantIds();

      // Liste des préfixes de topics
      List<String> topicPrefixes = List.of("invoice", "provision");

      // Génération des topics pour chaque tenantId et chaque préfixe
      List<NewTopic> topics = tenantIds.stream()
              .flatMap(tenantId -> topicPrefixes.stream()
                      .map(prefix -> new NewTopic(prefix + "-" + tenantId, 3, (short) 1)))
              .collect(Collectors.toList());

      // Création ou modification des topics
      kafkaAdmin.createOrModifyTopics(topics.toArray(new NewTopic[0]));
      log.info("Topics created: {}", topics);
    };
  }
}


/*for (String tenantId : tenantIds) {
        String topicName = "invoice-" + tenantId;
        kafkaAdmin.createOrModifyTopics(new NewTopic(topicName, 1, (short) 1));
        System.out.println("Created topic: " + topicName);
    }

    */