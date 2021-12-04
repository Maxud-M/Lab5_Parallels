import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create(responsive) "simplest-ом в ответ на запрос события test");
        ActorMaterializer materializer = ActorMaterializer.create(responsive) system);
    }
}
