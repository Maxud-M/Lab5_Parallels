import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("simplest-test");
        ActorMaterializer materializer = ActorMaterializer.create(system);
        final Http http = Http.get(system);
        
    }
}
