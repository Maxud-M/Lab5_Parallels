import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import com.sun.xml.internal.ws.util.CompletedFuture;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public  class MainHttp {

    static final Duration TIMEOUT = Duration.ofSeconds(5);
    public static final String QUERY_PARAMETR_URL = "testUrl";
    public static final String QUERY_PARAMETR_COUNT = "count";

    public static Route createRoute(ActorMaterializer materializer, ActorSystem system, Http http, ActorRef cacheActor) {
        return route(get(
                () -> parameter(QUERY_PARAMETR_URL, testUrl ->
                        parameter(QUERY_PARAMETR_COUNT, count -> {
                            Flow<HttpRequest, HttpRequest, NotUsed> flow = Flow.of(HttpRequest.class);
                            Flow<HttpRequest, Pair<String, Integer>, NotUsed> mapped = flow.map(req -> new Pair(testUrl, count));
                            mapped.mapAsync(1, p -> {
                                CompletionStage<Object> res = Patterns.ask(cacheActor, new CachingActor.GetMessage(testUrl), TIMEOUT)
                                        .thenCompose(response -> {
                                            if(!Objects.isNull(response)) {
                                                return CompletableFuture.completedFuture(response);
                                            } else {
                                                Source.from(Collections.singletonList()).toMat(testSink)
                                            }
                                        });

                            })

                            Flow<Pair<String, Integer>, Pair<String, Integer>, NotUsed> f = Flow.<Pair<String, Integer>> create();


                        }))));

    }
}
