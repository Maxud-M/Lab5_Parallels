import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;

public class CachingActor extends AbstractActor {
    HashMap<String, Float> cache = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(StoreMessage.class, m -> {
            
        }).build();
    }



    public static class StoreMessage{
        private String url;
        private Float result;

        public String getUrl() {
            return url;
        }

        public Float getResult() {
            return result;
        }
    }
}
