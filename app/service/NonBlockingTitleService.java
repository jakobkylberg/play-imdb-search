package service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.IMDbTitle;
import model.IMDbTitleContainer;
import model.OMDbTitle;
import model.OMDbTitleContainer;
import play.Logger;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Non-blocking service for retrieving titles
 *
 */
public class NonBlockingTitleService {

    public static String IMDB_URL_PATH = "http://www.imdb.com/xml/find";
    public static String OMDB_URL_PATTERN = "http://www.omdbapi.com/?i=%s&plot=short&r=json";
    private static ObjectMapper MAPPER = new ObjectMapper();

    public F.Promise<OMDbTitleContainer> getOMDbTitles(String query) {
        F.Promise<IMDbTitleContainer> imDbTitleContainerPromise = WS.url(IMDB_URL_PATH)
                .setQueryParameter("json", "1")
                .setQueryParameter("nr", "1")
                .setQueryParameter("tt", "on")
                .setQueryParameter("q", query)
                .get()
                .map(WSResponse::asJson)
                .map(this::fromJsonToIMDbTitleContainer);

        F.Promise<List<IMDbTitle>> iMDbTitlesPromise = imDbTitleContainerPromise.map(imDbTitleContainer -> {
            imDbTitleContainer.title_approx.stream().forEach(iMDbTitle -> iMDbTitle.searchResultType = "title_approx");
            imDbTitleContainer.title_exact.stream().forEach(iMDbTitle -> iMDbTitle.searchResultType = "title_exact");
            imDbTitleContainer.title_popular.stream().forEach(iMDbTitle -> iMDbTitle.searchResultType = "title_popular");
            imDbTitleContainer.title_substring.stream().forEach(iMDbTitle -> iMDbTitle.searchResultType = "title_substring");
            List<IMDbTitle> imDbTitles = new ArrayList<>();
            imDbTitles.addAll(imDbTitleContainer.title_approx);
            imDbTitles.addAll(imDbTitleContainer.title_exact);
            imDbTitles.addAll(imDbTitleContainer.title_popular);
            imDbTitles.addAll(imDbTitleContainer.title_substring);
            return imDbTitles;
        });

        return iMDbTitlesPromise.flatMap(this::getOMDbEntries).map(oMDbTitles -> {
            Map<String, List<OMDbTitle>> oMDbTitleMap = oMDbTitles
                    .stream()
                    .collect(Collectors.groupingBy(OMDbTitle::getSearchResultType));
            OMDbTitleContainer omDbTitleContainer = new OMDbTitleContainer();
            omDbTitleContainer.title_approx = oMDbTitleMap.get("title_approx");
            omDbTitleContainer.title_exact = oMDbTitleMap.get("title_exact");
            omDbTitleContainer.title_popular = oMDbTitleMap.get("title_popular");
            omDbTitleContainer.title_substring = oMDbTitleMap.get("title_substring");
            return omDbTitleContainer;
        });
    }

    protected IMDbTitleContainer fromJsonToIMDbTitleContainer(JsonNode iMDbTitleContainer) {
        return createModelFromJsonNode(iMDbTitleContainer, IMDbTitleContainer.class);
    }

    protected F.Promise<List<OMDbTitle>> getOMDbEntries(List<IMDbTitle> imDbTitles) {
        List<F.Promise<OMDbTitle>> listOfPromises = imDbTitles.stream()
                .map(this::getOMDbTitleFromIMDbTitle)
                .collect(Collectors.toList());
        return F.Promise.sequence(listOfPromises);
    }

    protected F.Promise<OMDbTitle> getOMDbTitleFromIMDbTitle(IMDbTitle iMDbTitle) {
        return WS.url(String.format(OMDB_URL_PATTERN, iMDbTitle.id)).get()
                .map(WSResponse::asJson)
                .map(this::fromJsonToOMDbTitle)
                .map(oMDbTitle -> {
                    oMDbTitle.searchResultType = iMDbTitle.searchResultType;
                    return oMDbTitle;
                });
    }

    protected OMDbTitle fromJsonToOMDbTitle(JsonNode OMDbTitle) {
        return createModelFromJsonNode(OMDbTitle, OMDbTitle.class);
    }

    protected <T> T createModelFromJsonNode(JsonNode cmsApiNode, Class<T> klass) {
        try {
            ObjectMapper configuredMap = MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            JavaType type = configuredMap.getTypeFactory().constructType(klass);
            return configuredMap.readValue(cmsApiNode.toString(), type);
        } catch (IOException e) {
            Logger.warn("Failed to parse json node", e);
            return null;
        }
    }

}
