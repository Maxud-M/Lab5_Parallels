import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("simplest-ом в ответ на запрос события test");
        ActorMaterializer materializer = ActorMaterializer.create(system);
    }
}
