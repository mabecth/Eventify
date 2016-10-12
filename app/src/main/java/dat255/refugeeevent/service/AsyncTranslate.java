package dat255.refugeeevent.service;

import android.os.AsyncTask;
import android.widget.TextView;

import com.memetix.mst.translate.Translate;

import java.lang.ref.WeakReference;

import dat255.refugeeevent.model.TranslateRequest;

/**
 * Created by Kristoffer on 2016-10-11.
 */

public class AsyncTranslate extends AsyncTask<TranslateRequest, Void, String> {

    private WeakReference<TextView> translatedView;

    public AsyncTranslate(TextView translatedView) {
        this.translatedView = new WeakReference<TextView>(translatedView);

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
            e.printStackTrace();
        }
        return translatedText;
    }

    @Override
    protected void onPostExecute(String s) {
        TextView output = translatedView.get();
        if (output != null){
            output.setText(s);
        }
    }
}
