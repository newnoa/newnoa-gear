package com.newnoa.gear.neo4j;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.nio.file.Path;
import org.jboss.logging.Logger;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.connectors.HttpConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;

/**
 * newnoa-gear.
 *
 * @author: Neowei
 * @date: 2024/4/7 18:47
 */
@ApplicationScoped
public class EmbeddedNeo4j {
    private static final Logger LOGGER = Logger.getLogger(EmbeddedNeo4j.class);

    public EmbeddedNeo4j() {
    }

    private DatabaseManagementService databaseManagementService;

    public void startup(@Observes StartupEvent event) {
        try {
            Class.forName("org.neo4j.dbms.api.DatabaseManagementService");
        } catch (ClassNotFoundException e) {
            LOGGER.info("embedded neo4j not found. use remote neo4j");
            return;
        }
        initDatabaseService();
    }

    private void initDatabaseService() {
        LOGGER.info("neo4j init start");
        DatabaseManagementService databaseManagementService =
                new DatabaseManagementServiceBuilder(Path.of("newnoa-gear-graphdb")).setConfig(GraphDatabaseSettings.default_listen_address, new SocketAddress("0.0.0.0"))
                        .setConfig(BoltConnector.enabled, true).setConfig(HttpConnector.enabled, true).build();
        registerShutdownHook(databaseManagementService);
        LOGGER.info("neo4j init finish");
    }

    private static void registerShutdownHook(final DatabaseManagementService managementService) {
        Runtime.getRuntime().addShutdownHook(new Thread(managementService::shutdown));
    }
}
