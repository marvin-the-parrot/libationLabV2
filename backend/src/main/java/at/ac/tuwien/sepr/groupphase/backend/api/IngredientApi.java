package at.ac.tuwien.sepr.groupphase.backend.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IngredientApi {
    @JsonProperty("idIngredient")
    private String idIngredient;

    @JsonProperty("strIngredient")
    private String name;

    @JsonProperty("strDescription")
    private String strDescription;

    @JsonProperty("strType")
    private String strType;

    @JsonProperty("strAlcohol")
    private String strAlcohol;

    @JsonProperty("strABV")
    private String strAbv;

    public IngredientApi() {
    }

    public IngredientApi(String idIngredient, String name, String strDescription, String strType,
                         String strAlcohol, String strAbv) {
        this.idIngredient = idIngredient;
        this.name = name;
        this.strDescription = strDescription;
        this.strType = strType;
        this.strAlcohol = strAlcohol;
        this.strAbv = strAbv;
    }

    public String getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(String idIngredient) {
        this.idIngredient = idIngredient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrDescription() {
        return strDescription;
    }

    public void setStrDescription(String strDescription) {
        this.strDescription = strDescription;
    }

    public String getStrType() {
        return strType;
    }

    public void setStrType(String strType) {
        this.strType = strType;
    }

    public String getStrAlcohol() {
        return strAlcohol;
    }

    public void setStrAlcohol(String strAlcohol) {
        this.strAlcohol = strAlcohol;
    }

    public String getStrAbv() {
        return strAbv;
    }

    public void setStrAbv(String strAbv) {
        this.strAbv = strAbv;
    }

}

