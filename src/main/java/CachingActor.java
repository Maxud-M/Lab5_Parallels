import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class CachingActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(StoreMessage).build();
    }
}
