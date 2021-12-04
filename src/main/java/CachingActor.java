import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;

public class CachingActor extends AbstractActor {
    HashMap<String, Float> cache = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(StoreMessage).build();
    }



    public static class StoreMessage{
        String url;
    }
}
