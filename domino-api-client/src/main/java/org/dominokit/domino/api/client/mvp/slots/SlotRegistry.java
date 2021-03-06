package org.dominokit.domino.api.client.mvp.slots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SlotRegistry {

    public static final Logger LOGGER = LoggerFactory.getLogger(SlotRegistry.class);

    private static final Map<String, Deque<IsSlot>> SLOT_QUEUE = new HashMap<>();

    public static void registerSlot(String key, IsSlot slot) {
        LOGGER.info(" >> REGISTERING SLOT ["+key+"]");
        if (!SLOT_QUEUE.containsKey(key.toLowerCase())) {
            SLOT_QUEUE.put(key.toLowerCase(), new LinkedList<>());
        }

        SLOT_QUEUE.get(key.toLowerCase()).push(slot);
    }

    public static void removeSlot(String key) {
        LOGGER.info(" << REMOVING SLOT ["+key+"]");
        if (SLOT_QUEUE.containsKey(key.toLowerCase())) {
            IsSlot popedOut = SLOT_QUEUE.get(key.toLowerCase()).pop();
            popedOut.cleanUp();
        }
    }

    public static IsSlot get(String key){
        return SLOT_QUEUE.get(key.toLowerCase()).peek();
    }

}
