import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.ResponseEntity;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.SinkShape;
import akka.stream.javadsl.*;
import static org.asynchttpclient.Dsl.*;
import com.sun.xml.internal.ws.util.CompletedFuture;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

import static akka.http.javadsl.server.Directives.*;

public  class Router {

    static final Duration TIMEOUT = Duration.ofSeconds(5);
    public static final String QUERY_PARAMETR_URL = "testUrl";
    public static final String QUERY_PARAMETR_COUNT = "count";

    public static Route createRoute(ActorMaterializer materializer, ActorSystem system, Http http, ActorRef cacheActor) {

        return route(get(() ->
                parameter(QUERY_PARAMETR_URL, testUrl ->
                        parameter(QUERY_PARAMETR_COUNT, count -> {
                            int numOfReq = Integer.parseInt(count);
                            Flow<HttpRequest, HttpRequest, NotUsed> flow = Flow.of(HttpRequest.class);
                            Flow<HttpRequest, Pair<String, Integer>, NotUsed> mapped = flow.map(req -> new Pair<>(testUrl, numOfReq));
                            Flow<HttpRequest, Long, NotUsed> m = mapped.mapAsync(1, p ->
                                Patterns.ask(cacheActor, new CachingActor.GetMessage(testUrl), TIMEOUT).thenCompose(response -> {
                                    System.out.println("HEY");
                                    System.out.println(numOfReq);
                                            CompletionStage<Long> result;
                                            String resStr = String.valueOf(response);
                                            Long resLong = Long.parseLong(resStr);
                                    System.out.println("HEY");
                                    System.out.println(resLong);
                                            if(resLong != -1) {
                                                result = CompletableFuture.completedFuture(resLong);
                                            } else {
                                                Flow<Pair<String, Integer>, Pair<String, Integer>, NotUsed> f = Flow.create();
                                                Flow<Pair<String, Integer>, String, NotUsed> flowConcat = f.mapConcat(reqEntity -> {
                                                    ArrayList<String> list = new ArrayList<>(0);
                                                    for(int i = 0; i < reqEntity.second(); ++i) {
                                                        list.add(reqEntity.first());
                                                    }
                                                    return list;
                                                });
                                                Flow<Pair<String, Integer>, Long, NotUsed> flowMapped = flowConcat.mapAsync(1, url -> {
                                                    AsyncHttpClient asyncHttpClient = asyncHttpClient();
                                                    Request request = get(url).build();
                                                    long startTime = System.currentTimeMillis();
                                                    CompletableFuture<Long> whenResponse = asyncHttpClient.executeRequest(request)
                                                            .toCompletableFuture()
                                                            .thenCompose(response1 -> {
                                                                    long endTime = System.currentTimeMillis();
                                                                    return CompletableFuture.completedFuture(endTime - startTime);
                                                            });
                                                    return whenResponse;
                                                });
                                                Sink<Long, CompletionStage<Long>> fold = Sink.fold(0L, (agg, next) -> agg + next);
                                                Sink<Pair<String, Integer>, CompletionStage<Long>> testSink = flowMapped.toMat(fold, Keep.right());
                                                RunnableGraph<CompletionStage<Long>> graph = Source.from(Collections.singletonList(new Pair<>(testUrl, numOfReq))).toMat(testSink, Keep.right());
                                                result = graph.run(materializer);
                                            }
                                            return result;
                                })
                            );
                            Flow<HttpRequest, Long, NotUsed> result = m.map(res -> {
                                cacheActor.tell(new CachingActor.StoreMessage(testUrl, res), ActorRef.noSender());
                                return res;
                                //return HttpResponse.create().withEntity("The time of query requests is:" + res);
                            });

                            Sink<Long, CompletionStage<Long>> sink = Sink.head();
                            RunnableGraph<CompletionStage<Long>> graph = Source.empty(HttpRequest.class).via(result).toMat(sink, Keep.right());

                            return completeOKWithFuture(
                                    graph.run(materializer),
                                    Jackson.marshaller()
                            );
                        })
                )
        ));
    }
}
