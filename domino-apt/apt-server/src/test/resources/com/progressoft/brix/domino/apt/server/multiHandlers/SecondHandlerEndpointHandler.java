package com.progressoft.brix.domino.apt.server.multiHandlers;

import com.progressoft.brix.domino.api.shared.request.ResponseBean;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import com.progressoft.brix.domino.api.server.ServerApp;
import com.progressoft.brix.domino.api.server.entrypoint.VertxEntryPointContext;

public class SecondHandlerEndpointHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {
        ServerApp serverApp=ServerApp.make();
        SecondRequest requestBody = Json.decodeValue(routingContext.getBodyAsString(), SecondRequest.class);
        ResponseBean response = (ResponseBean) serverApp
                .executeRequest(requestBody, new VertxEntryPointContext(routingContext, serverApp.serverContext().config(),
                        routingContext.vertx()));
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encode(response));
    }
}