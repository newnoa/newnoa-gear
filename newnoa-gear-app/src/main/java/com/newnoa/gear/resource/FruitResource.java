package com.newnoa.gear.resource;

import com.newnoa.gear.Fruit;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.context.ThreadContext;
import org.jboss.logging.Logger;
import org.neo4j.driver.Driver;
import org.neo4j.driver.async.AsyncSession;
import org.neo4j.driver.async.ResultCursor;

/**
 * newnoa-gear.
 *
 * @author: Neowei
 * @date: 2024/4/7 19:52
 */
@Path("/fruits")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FruitResource {
    @Inject
    Driver driver;

    @Inject
    ThreadContext threadContext;
    private static final Logger LOGGER = Logger.getLogger(FruitResource.class);
    @GET
    public CompletionStage<Response> get() {
        AsyncSession session = driver.session(AsyncSession.class);
        CompletionStage<List<Fruit>> cs = session
                .executeReadAsync(tx -> tx
                        .runAsync("MATCH (f:Fruit) RETURN f ORDER BY f.name")
                        .thenCompose(cursor -> cursor
                                .listAsync(record -> Fruit.from(record.get("f").asNode()))));
        LOGGER.info("awesome");
        return threadContext.withContextCapture(cs)
                .thenCompose(fruits ->
                        session.closeAsync().thenApply(signal -> fruits))
                .thenApply(Response::ok)
                .thenApply(ResponseBuilder::build);
    }

    @POST
    public CompletionStage<Response> create(Fruit fruit) {
        AsyncSession session = driver.session(AsyncSession.class);
        CompletionStage<Fruit> cs = session
                .executeWriteAsync(tx -> tx
                        .runAsync(
                                "CREATE (f:Fruit {id: randomUUID(), name: $name}) RETURN f",
                                Map.of("name", fruit.name))
                        .thenCompose(ResultCursor::singleAsync)
                        .thenApply(record -> Fruit.from(record.get("f").asNode())));
        return threadContext.withContextCapture(cs)
                .thenCompose(persistedFruit -> session
                        .closeAsync().thenApply(signal -> persistedFruit))
                .thenApply(persistedFruit -> Response
                        .created(URI.create("/fruits/" + persistedFruit.id))
                        .build());
    }
}
