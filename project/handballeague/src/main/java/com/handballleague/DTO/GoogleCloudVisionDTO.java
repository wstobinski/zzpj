package com.handballleague.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleCloudVisionDTO {
    @JsonProperty("responses")
    private List<Response> responsesList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        @JsonProperty("textAnnotations")
        private List<TextAnnotation> textAnnotations;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TextAnnotation {

            @JsonProperty("description")
            private String description;
        }
    }
}
