package dat255.eventify.util;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ParseDistanceAsyncTask extends AsyncTask<String, String, String> {

    private static final String TAG = "ParseDistanceAsyncTask";
    private GoogleApi googleApi;
    private int id;

    public ParseDistanceAsyncTask(GoogleApi googleApi, int id) {
        this.googleApi = googleApi;
        this.id = id;
    }

    @Override
    protected String doInBackground(String... urls) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

            StringBuffer buffer = new StringBuffer();

            String line = "";
            //Extract distance from JSON
            while ((line = reader.readLine()) != null) {
                if (line.contains("distance")) {
                    line = reader.readLine();
                    line = line.replaceAll("[^0-9.]+", "");
                    buffer.append(line + " km");
                }
            }

        return buffer.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG,"MalformedURLException", e);
        } catch (IOException e) {
            Log.e(TAG,"IOException", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG,"IOException", e);
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        googleApi.updateDistance(id, result);
    }

}
