package dat255.eventify;

import android.view.View;
import android.widget.TextView;
import static org.junit.Assert.*;

import com.memetix.mst.language.Language;

import org.junit.Test;

import dat255.eventify.activity.MainActivity;
import dat255.eventify.model.TranslateRequest;
import dat255.eventify.util.TranslateAsyncTask;

public class TranslateRequestTest {

    @Test
    public void translateTest() throws Exception {
        TranslateRequest translateTest = new TranslateRequest(Language.FRENCH, "Tack");

        final TextView textView = new TextView(new MainActivity());
        textView.setText("Hej");
        assertEquals("Hej", textView.getText());

        TranslateAsyncTask task = new TranslateAsyncTask(textView);
        task.execute(translateTest);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertEquals("Merci", textView.getText());

            }
        });
        t.start();

    }
}


