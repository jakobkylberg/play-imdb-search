package controllers;

import model.IMDbTitleContainer;
import play.libs.Json;

import play.mvc.Controller;
import play.mvc.Result;
import service.BlockingTitleService;

public class Application extends Controller {

    public static BlockingTitleService blockingTitleService = new BlockingTitleService();

    public static Result searchSync(String title) {
        return ok(Json.toJson(blockingTitleService.getIMDbTitles(title)));
    }

    public static Result searchGUI(String query) {
        return ok(views.html.search.render(query.isEmpty() ?
                new IMDbTitleContainer() : blockingTitleService.getIMDbTitles(query)));
    }

}
