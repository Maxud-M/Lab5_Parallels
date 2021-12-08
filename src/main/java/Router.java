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
import akka.stream.javadsl.*;
import static org.asynchttpclient.Dsl.*;
import com.sun.xml.internal.ws.util.CompletedFuture;

import java.time.Duration;
import java.util.ArrayList;
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
                            Flow<HttpRequest, HttpResponse, NotUsed> m = mapped.mapAsync(1, p -> {
                                CompletionStage<Long> res = Patterns.ask(cacheActor, new CachingActor.GetMessage(testUrl), TIMEOUT)
                                        .thenCompose(response -> {
                                            if(!Objects.isNull(response)) {
                                                return CompletableFuture.completedFuture(response);
                                            } else {
                                                Flow<Pair<String, Integer>, Pair<String, Integer>, NotUsed> f = Flow.create();
                                                Flow<Pair<String, Integer>, String, NotUsed> flowConcat = f.mapConcat(reqEntity -> {
                                                    ArrayList<String> result = new ArrayList<String>(0);
                                                    for(int i = 0; i < reqEntity.second(); ++i) {
                                                        result.add(reqEntity.first());
                                                    }
                                                    return result;
                                                });
                                                Flow<Pair<String, Integer>, Long, NotUsed> flowMapped = flowConcat.mapAsync(1, pair -> {
                                                    long startTime = System.currentTimeMillis();
                                                    //request
                                                    long endTime = System.currentTimeMillis();
                                                    return
                                                    //start timer, async http client, in thenCompose end timer and return future with result time
                                                });
                                                Sink<Pair<String, Integer>, CompletionStage<Long>> fold = Sink.fold(0, (agg, next) -> agg + next);
                                                Sink<Long, CompletionStage<Long>> testSink = flow_2.toMat(fold, Keep.right());
                                                RunnableGraph<CompletionStage<Long>> graph = Source.from(Collections.singletonList()).toMat(testSink, Keep.right());
                                                CompletionStage<Long> result = graph.run(materializer);
                                                return result;
                                            }
                                        });
                                return res;
                                        });

                            });


                        }))));

    }
}
