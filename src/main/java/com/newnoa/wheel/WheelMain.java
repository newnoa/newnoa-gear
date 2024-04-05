package com.newnoa.wheel;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * @author: Neowei
 * @date: 2024/4/5 14:15
 */

@QuarkusMain
public class WheelMain {
    public static void main(String... args) {
        Quarkus.run(args);
    }
}
