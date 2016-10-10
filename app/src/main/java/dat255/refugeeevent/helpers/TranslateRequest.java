package dat255.refugeeevent.helpers;

import com.memetix.mst.language.Language;

/**
 * Created by Kristoffer on 2016-10-11.
 */

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
