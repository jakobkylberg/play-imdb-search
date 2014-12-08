package service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import play.Logger;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import java.io.IOException;
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
    public static String OMDB_URL_PATH = "http://www.omdbapi.com";
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

        F.Promise<List<IMDbTitle>> iMDbTitlesPromise = imDbTitleContainerPromise.map(this::collapseTitlesIntoOneList);

        return iMDbTitlesPromise.flatMap(this::retrieveOMDbTitles).map(this::expandOMDbTitleListIntoSeparateLists);
    }

    protected IMDbTitleContainer fromJsonToIMDbTitleContainer(JsonNode iMDbTitleContainer) {
        return createModelFromJsonNode(iMDbTitleContainer, IMDbTitleContainer.class);
    }

    protected List<IMDbTitle> collapseTitlesIntoOneList(IMDbTitleContainer imDbTitleContainer) {
        imDbTitleContainer.title_approx.stream().forEach(iMDbTitle -> iMDbTitle.searchResultType = SearchResultType.title_approx);
        imDbTitleContainer.title_exact.stream().forEach(iMDbTitle -> iMDbTitle.searchResultType = SearchResultType.title_exact);
        imDbTitleContainer.title_popular.stream().forEach(iMDbTitle -> iMDbTitle.searchResultType = SearchResultType.title_popular);
        imDbTitleContainer.title_substring.stream().forEach(iMDbTitle -> iMDbTitle.searchResultType = SearchResultType.title_substring);
        List<IMDbTitle> imDbTitles = new ArrayList<>();
        imDbTitles.addAll(imDbTitleContainer.title_approx);
        imDbTitles.addAll(imDbTitleContainer.title_exact);
        imDbTitles.addAll(imDbTitleContainer.title_popular);
        imDbTitles.addAll(imDbTitleContainer.title_substring);
        return imDbTitles;
    }

    protected OMDbTitleContainer expandOMDbTitleListIntoSeparateLists(List<OMDbTitle> oMDbTitles) {
        Map<String, List<OMDbTitle>> oMDbTitleMap = oMDbTitles
                .stream()
                .collect(Collectors.groupingBy(OMDbTitle::getSearchResultType));
        OMDbTitleContainer omDbTitleContainer = new OMDbTitleContainer();
        omDbTitleContainer.title_approx = oMDbTitleMap.get(SearchResultType.title_approx.name());
        omDbTitleContainer.title_exact = oMDbTitleMap.get(SearchResultType.title_exact.name());
        omDbTitleContainer.title_popular = oMDbTitleMap.get(SearchResultType.title_popular.name());
        omDbTitleContainer.title_substring = oMDbTitleMap.get(SearchResultType.title_substring.name());
        return omDbTitleContainer;
    }

    protected OMDbTitle fromJsonToOMDbTitle(JsonNode OMDbTitle) {
        return createModelFromJsonNode(OMDbTitle, OMDbTitle.class);
    }

    protected F.Promise<List<OMDbTitle>> retrieveOMDbTitles(List<IMDbTitle> imDbTitles) {
        List<F.Promise<OMDbTitle>> listOfPromises = imDbTitles.stream()
                .map(this::getOMDbTitleFromIMDbTitle)
                .collect(Collectors.toList());
        return F.Promise.sequence(listOfPromises);
    }

    protected F.Promise<OMDbTitle> getOMDbTitleFromIMDbTitle(IMDbTitle iMDbTitle) {
        return WS.url(OMDB_URL_PATH)
                .setQueryParameter("i", iMDbTitle.id)
                .setQueryParameter("plot", "short")
                .setQueryParameter("r", "json")
                .get()
                .map(WSResponse::asJson)
                .map(this::fromJsonToOMDbTitle)
                .map(oMDbTitle -> {
                    oMDbTitle.searchResultType = iMDbTitle.searchResultType;
                    return oMDbTitle;
                });
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
