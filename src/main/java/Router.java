import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;

import static akka.http.javadsl.server.Directives.*;

public  class MainHttp {

    public static Route createRoute(ActorMaterializer materializer, ActorSystem system, Http http) {
        return route(get(
                () -> parameter("ur;"))

    }
}
