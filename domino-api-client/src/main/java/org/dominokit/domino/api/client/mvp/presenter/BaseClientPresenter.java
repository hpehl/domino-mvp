package org.dominokit.domino.api.client.mvp.presenter;

import org.dominokit.domino.api.client.ClientApp;
import org.dominokit.domino.api.client.async.AsyncRunner;
import org.dominokit.domino.api.client.extension.DominoEvents;
import org.dominokit.domino.api.client.mvp.IsStore;
import org.dominokit.domino.api.client.mvp.RegistrationHandler;
import org.dominokit.domino.api.client.mvp.StoreRegistry;
import org.dominokit.domino.api.client.startup.BaseRoutingStartupTask;
import org.dominokit.domino.api.shared.extension.DominoEvent;
import org.dominokit.domino.api.shared.extension.DominoEventListener;
import org.dominokit.domino.history.AppHistory;
import org.dominokit.domino.history.DominoHistory;
import org.dominokit.domino.history.TokenParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

public abstract class BaseClientPresenter extends ClientPresenter implements Presentable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseClientPresenter.class);
    private PresenterState state;
    protected boolean activated;
    private BaseRoutingStartupTask routingTask;

    private final PresenterState initialized = () ->
            LOGGER.info("Presenter " + BaseClientPresenter.this.getClass() + " Have already been initialized.");

    private final PresenterState uninitialized = this::initialize;

    private Map<Class<? extends DominoEvent>, DominoEventListener> listeners;
    private final List<RegistrationHandler> storeRegisterations= new ArrayList<>();

    protected void initialize() {
        postConstruct();
        this.listeners = getListeners();
        registerListeners();
        state = initialized;
        if (isAutoActivate()) {
            activate();
        }
    }

    protected void postConstruct() {

    }

    protected void activate() {
        activated = true;
        fireStateEvent(true);
        onActivated();
    }

    protected void fireStateEvent(boolean state) {
        fireActivationEvent(state);
    }

    protected void fireActivationEvent(boolean state) {
    }

    private void registerListeners() {
        listeners.forEach((key, value) -> ClientApp.make().registerEventListener(key, value));
    }

    private void removeListeners() {
        listeners.forEach((key, value) -> ClientApp.make().removeEventListener(key, value));
    }

    @Override
    public Presentable init() {
        this.state = uninitialized;
        prepare();
        return this;
    }

    @Override
    public ClientPresenter prepare() {
        state.process();
        return this;
    }

    protected final void deActivate() {
        removeListeners();
        activated = false;
        fireStateEvent(false);
        removeStores();
        onDeactivated();
        if(nonNull(routingTask)) {
            routingTask.enable();
        }
    }

    private void removeStores(){
        storeRegisterations.forEach(RegistrationHandler::remove);
        storeRegisterations.clear();
    }

    @Override
    protected void onActivated() {

    }

    @Override
    protected void onDeactivated() {

    }

    public void setState(DominoHistory.State state) {

    }

    public void registerStore(String key, IsStore<?> store){
        RegistrationHandler registrationHandler = StoreRegistry.INSTANCE.registerStore(key, store);
        storeRegisterations.add(registrationHandler);

    }

    public void setRoutingTask(BaseRoutingStartupTask routingTask) {
        this.routingTask = routingTask;
    }

    public final void onSkippedRouting() {
        if (!routingTask.isEnabled()) {
            routingTask.enable();
        }
    }

    protected void publishState(String token, String title, String data) {
        routingTask.disable();
        history().fireState(token, title, data);
    }

    protected void publishState(String token, String title, String data, TokenParameter... parameters) {
        routingTask.disable();
        history().fireState(token, title, data, parameters);
    }

    protected void publishState(String token) {
        routingTask.disable();
        history().fireState(token);
    }

    protected void publishState(String token, TokenParameter... parameters) {
        routingTask.disable();
        history().fireState(token, parameters);
    }

    protected boolean isAutoActivate() {
        return true;
    }

    public final boolean isActivated() {
        return activated;
    }

    protected Map<Class<? extends DominoEvent>, DominoEventListener> getListeners() {
        return new HashMap<>();
    }

    protected <E extends DominoEvent> void fireEvent(Class<E> extensionPointInterface, E extensionPoint) {
        DominoEvents.fire(extensionPointInterface, extensionPoint);
    }

    protected void runAsync(AsyncRunner.AsyncTask asyncTask) {
        ClientApp.make().getAsyncRunner().runAsync(asyncTask);
    }

    protected AppHistory history() {
        return ClientApp.make().getHistory();
    }
}
