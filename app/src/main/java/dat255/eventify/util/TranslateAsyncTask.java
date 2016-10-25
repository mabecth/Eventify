package dat255.eventify.util;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.memetix.mst.translate.Translate;

import java.lang.ref.WeakReference;

import dat255.eventify.model.TranslateRequest;

public class TranslateAsyncTask extends AsyncTask<TranslateRequest, Void, String> {

    private static final String TAG = "TranslateAsyncTask";
    private WeakReference<TextView> translatedView;

    public TranslateAsyncTask(TextView translatedView) {
        this.translatedView = new WeakReference<>(translatedView);

        Translate.setClientId("dat255_rolf");
        Translate.setClientSecret("nSg9iOcBBD1odZXyy1b/y/xDYDmDC62KnpJU5YJ8CP4=");
    }

    @Override
    protected String doInBackground(TranslateRequest... params) {
        TranslateRequest req = params[0];
        String translatedText = "";
        try {
            translatedText = Translate.execute(req.getText(), req.getLanguage());
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
        return translatedText;
    }

    @Override
    protected void onPostExecute(String s) {
        TextView output = translatedView.get();
        if (output != null) {
            output.setText(s);
        }
    }
}