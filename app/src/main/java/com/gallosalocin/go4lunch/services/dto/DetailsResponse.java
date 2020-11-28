package com.gallosalocin.go4lunch.services.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DetailsResponse implements Serializable {

    @SerializedName("html_attributions")
    private List<Object> htmlAttributions = null;
    @SerializedName("result")
    private DetailsResult detailsResult;
    private String status;

    public DetailsResponse() {
    }

    public DetailsResponse(List<Object> htmlAttributions, DetailsResult detailsResult, String status) {
        this.htmlAttributions = htmlAttributions;
        this.detailsResult = detailsResult;
        this.status = status;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public DetailsResult getDetailsResult() {
        return detailsResult;
    }

    public void setDetailsResult(DetailsResult detailsResult) {
        this.detailsResult = detailsResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DetailsResponse{" +
                "htmlAttributions=" + htmlAttributions +
                ", detailsResult=" + detailsResult +
                ", status='" + status + '\'' +
                '}';
    }
}
