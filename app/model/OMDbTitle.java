package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class representing a title from OMDb
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OMDbTitle {

    public String Title;
    public String Year;
    public String Rated;
    public String Released;
    public String Runtime;
    public String Genre;
    public String Director;
    public String Writer;
    public String Actors;
    public String Plot;
    public String Language;
    public String Country;
    public String Awards;
    public String Poster;
    public String Metascore;
    public String imdbRating;
    public String imdbVotes;
    public String imdbID;
    public String Type;
    public String Response;
    public SearchResultType searchResultType;

    public String getSearchResultType() {
        return searchResultType.name();
    }
}



