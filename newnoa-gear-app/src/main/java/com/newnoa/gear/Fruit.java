package com.newnoa.gear;

import java.util.UUID;
import org.neo4j.driver.types.Node;

/**
 * newnoa-gear.
 *
 * @author: Neowei
 * @date: 2024/4/7 19:54
 */
public class Fruit {
    public UUID id;

    public String name;

    public Fruit() {
        // This is needed for the REST-Easy JSON Binding
    }

    public Fruit(String name) {
        this.name = name;
    }

    public Fruit(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Fruit from(Node node) {
        return new Fruit(UUID.fromString(node.get("id").asString()), node.get("name").asString());
    }
}
