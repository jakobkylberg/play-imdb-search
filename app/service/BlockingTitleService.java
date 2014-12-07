package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.IMDbTitle;
import model.IMDbTitleContainer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Blocking service for retrieving iMDB titles by title search
 *
 */
public class BlockingTitleService {

    public static String IMDB_URL_PATTERN = "http://www.imdb.com/xml/find?json=1&nr=1&tt=on&q=%s";
    public static String OMDB_URL_PATTERN = "http://www.omdbapi.com/?i=%s&plot=short&r=json";
    private static ObjectMapper MAPPER = new ObjectMapper();

    public IMDbTitleContainer getIMDbTitles(String title) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(String.format(IMDB_URL_PATTERN, URLEncoder.encode(title, "UTF-8")));
            HttpResponse httpResponse = client.execute(request);
            IMDbTitleContainer imDbTitleContainer =
                    MAPPER.readValue(httpResponse.getEntity().getContent(), IMDbTitleContainer.class);
            return imDbTitleContainer;
        } catch (IOException e) {
            throw new RuntimeException("Error retrieving iMDb title information", e);
        }
    }

}
