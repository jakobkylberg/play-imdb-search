package controllers;

import model.OMDbTitleContainer;
import play.libs.F;
import play.libs.Json;
import views.html.*;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import service.BlockingTitleService;
import service.NonBlockingTitleService;

public class Application extends Controller {

    public static BlockingTitleService blockingTitleService = new BlockingTitleService();
    public static NonBlockingTitleService nonBlockingTitleService = new NonBlockingTitleService();

    public static Result searchSync(String title) {
        return ok(Json.toJson(blockingTitleService.getIMDbTitles(title)));
    }

    public static F.Promise<Result> searchAsync(String title) {
        return nonBlockingTitleService.getOMDbTitles(title).map(Json::toJson).map(Results::ok);
    }

    public static F.Promise<Result> searchGUI(String query) {
        if (query.isEmpty()) {
            return F.Promise.promise(() -> ok(search.render(new OMDbTitleContainer())));
        }
        F.Promise<OMDbTitleContainer> omDbTitleContainerPromise = nonBlockingTitleService.getOMDbTitles(query);
        return omDbTitleContainerPromise.map(oMDbTitleContainer -> ok(search.render(oMDbTitleContainer)));
    }

}
