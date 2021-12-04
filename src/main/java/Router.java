import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;

import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public  class MainHttp {

    public static final String QUERY_PARAMETR_URL = "testUrl";
    public static final String QUERY_PARAMETR_COUNT = "count";

    public static Route createRoute(ActorMaterializer materializer, ActorSystem system, Http http) {
        return route(get(
                () -> parameter(QUERY_PARAMETR_URL, testUrl ->
                        parameter(QUERY_PARAMETR_COUNT, count -> {
                            Flow<HttpRequest, HttpRequest, NotUsed> flow = Flow.of(HttpRequest.class);
                            Flow<HttpRequest, Pair<String, Integer>, NotUsed> mapped = flow.map(req -> new Pair(testUrl, count));
                            Flow.Pair<String, Integer> create()


                        }))));

    }
}
