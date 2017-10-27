package com.progressoft.brix.domino.desktop.client;

import com.progressoft.brix.domino.api.client.events.Event;
import com.progressoft.brix.domino.api.client.events.ServerRequestEventFactory;
import com.progressoft.brix.domino.api.client.request.ServerRequest;
import com.progressoft.brix.domino.api.shared.request.ResponseBean;
import com.progressoft.brix.domino.desktop.client.events.DesktopFailedServerEvent;
import com.progressoft.brix.domino.desktop.client.events.DesktopSuccessServerEvent;

public class DesktopServerRequestEventFactory implements ServerRequestEventFactory {
    @Override
    public Event makeSuccess(ServerRequest request, ResponseBean responseBean) {
        return new DesktopSuccessServerEvent(request, responseBean);
    }

    @Override
    public Event makeFailed(ServerRequest request, Throwable error) {
        return new DesktopFailedServerEvent(request, error);
    }
}
