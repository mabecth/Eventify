package dat255.eventify;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import android.widget.TextView;

import com.memetix.mst.language.Language;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import dat255.eventify.model.TranslateRequest;
import dat255.eventify.util.TranslateAsyncTask;

import static org.junit.Assert.assertEquals;

public class TranslateRequestTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("dat255.eventify", appContext.getPackageName());

        TranslateRequest translateTest = new TranslateRequest(Language.FRENCH, "Tack");

        final TextView textView = new TextView(appContext);
        textView.setText("Hej");
        assertEquals("Hej", textView.getText());

        TranslateAsyncTask task = new TranslateAsyncTask(textView);
        task.execute(translateTest);

        final CountDownLatch signal = new CountDownLatch(1);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.e("TranslateRequestTest", "InterruptedException", e);
                }
                assertEquals("Merci", textView.getText());
                Log.d("Translate", textView.getText().toString());
                signal.countDown();

            }
        });
        t.start();
        signal.await();
    }
}
