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
  public ApplicationRunner  createTenantTopics() {// Create tenant topics using KafkaAdmin
    return args -> {
    log.info("Creating tenant topics");
    List<String> tenantIds = dataSourceConfig.getAllTenantIds();


    List<NewTopic> topics =
        tenantIds.stream()
            .map(tenantId -> new NewTopic("invoice-" + tenantId, 3, (short) 1))
            .collect(Collectors.toList());

    kafkaAdmin.createOrModifyTopics(topics.toArray(new NewTopic[0]));


  };
}
}


/*for (String tenantId : tenantIds) {
        String topicName = "invoice-" + tenantId;
        kafkaAdmin.createOrModifyTopics(new NewTopic(topicName, 1, (short) 1));
        System.out.println("Created topic: " + topicName);
    }

    */