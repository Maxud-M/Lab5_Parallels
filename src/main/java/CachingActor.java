import akka.actor.AbstractActor;
import akka.actor.dsl.Inbox;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;

public class CachingActor extends AbstractActor {
    HashMap<String, Float> cache = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(StoreMessage.class, m -> {
                    cache.put(m.url, m.result);
                })
                .match(GetMessage.class, m -> {
                    sender().tell(cache.get(m.getUrl()))
                })
                .build();
    }

    public static class GetMessage{
        private String url;

        public String getUrl() {
            return url;
        }

        GetMessage(String url) {
            this.url = url;
        }
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

        StoreMessage(String url, Float result) {
            this.url = url;
            this.result = result;
        }
    }
}
