package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for results from IMDb
 *
 */
public class OMDbTitleContainer {

    public List<OMDbTitle> title_popular = new ArrayList<>();
    public List<OMDbTitle> title_exact = new ArrayList<>();
    public List<OMDbTitle> title_substring = new ArrayList<>();
    public List<OMDbTitle> title_approx = new ArrayList<>();

}
