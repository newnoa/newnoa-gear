/*
 * Copyright (c) 2024-2030, newnoa (newnoa@aliyun.com & https://www.newnoa.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.newnoa.gear.resource;

import static io.opentelemetry.api.trace.StatusCode.ERROR;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.opentelemetry.semconv.SemanticAttributes;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

/**
 * GreetingResource.
 *
 * @author: Neowei
 * @date: 2024/4/5 14:15
 */
@Path("/hello")
public final class GreetingResource {
    private static final Logger LOGGER = Logger.getLogger(GreetingResource.class);
    @Inject
    Tracer otelTracer;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @WithSpan(value = "Not needed, will create a new span, child of the automatic JAX-RS span")
    public String hello() {
        Span incomingSpan = Span.current();
        incomingSpan.setAttribute(SemanticAttributes.CODE_NAMESPACE, "GreetingResource");

        // Create a manual inner span
        Span innerSpan = otelTracer.spanBuilder("Count response chars").startSpan();
        LOGGER.info("awesome");
        try (Scope scope = innerSpan.makeCurrent()) {
            final String response = "Hello from Quarkus REST neowei";
            innerSpan.setAttribute("response-chars-count", response.length());
            return response;
        } catch (Exception e) {
            innerSpan.setStatus(ERROR);
            innerSpan.recordException(e);
            throw e;
        } finally {
            innerSpan.end();
        }
    }


}
