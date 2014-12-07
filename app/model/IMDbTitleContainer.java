package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for results from IMDb
 *
 */
public class IMDbTitleContainer {

    public List<IMDbTitle> title_popular = new ArrayList<>();
    public List<IMDbTitle> title_exact = new ArrayList<>();
    public List<IMDbTitle> title_substring = new ArrayList<>();
    public List<IMDbTitle> title_approx = new ArrayList<>();

}
