package dat255.eventify.model;

import com.memetix.mst.language.Language;

public class TranslateRequest {

    private String text;
    private Language language;

    public TranslateRequest(Language language, String text) {
        this.language = language;
        this.text = text;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
