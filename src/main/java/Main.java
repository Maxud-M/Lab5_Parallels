import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class Main {
    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("simplest-test");
        ActorRef cacheActor = system.actorOf(Props.create(CachingActor.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Http http = Http.get(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = Router.createRoute(materializer, system, http, cacheActor)
                .flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8080),
                materializer
        );
        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
