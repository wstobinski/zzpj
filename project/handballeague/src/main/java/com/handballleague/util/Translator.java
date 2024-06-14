package com.handballleague.util;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Translator {

    @Value("${sentiment.api.key}")
    private String apiKey;
    private String targetLanguage;

    private Translate translate;

//    @Autowired
    public Translator() {
        this.targetLanguage = "en";
    }

    public String translate(String text) {

        // Initialize translation service with the API key
        Translate translate = TranslateOptions.newBuilder().setApiKey(this.apiKey).build().getService();

        // Perform translation
        Translation translation = translate.translate(text, Translate.TranslateOption.targetLanguage(this.targetLanguage));

        // Print the translated text
        return translation.getTranslatedText();
    }

}
