import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class CachingActor extends AbstractActor {
    HashMap<String, Float>

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(StoreMessage).build();
    }



    public static class StoreMessage()
}
