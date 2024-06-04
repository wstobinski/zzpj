package com.handballleague.initialization;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Blob;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import com.google.protobuf.ByteString;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayersInitializer {
    public static void main(String[] args) throws IOException {
        try (VertexAI vertexAi = new VertexAI("handball-app-425321", "us-central1"); ) {
            GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setMaxOutputTokens(8192)
                            .setTemperature(1F)
                            .setTopP(0.95F)
                            .build();
            List<SafetySetting> safetySettings = Arrays.asList(
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build()
            );
            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName("gemini-1.5-pro-001")
                            .setVertexAi(vertexAi)
                            .setGenerationConfig(generationConfig)
                            .setSafetySettings(safetySettings)
                            .build();

            var text1 = "come up with data for 10 handball players. For each of them  give separated by comma first name, last name, phone number (9 digits without country code), pitch number, is capitan (yes or no - only 1 player can be capitan). List this players in separete lines, numerate them , don't write anything else";

            var content = ContentMaker.fromMultiModalData(text1);
            ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(content);

            // Do something with the response
            responseStream.stream().forEach(System.out::println);
        }
    }
}
