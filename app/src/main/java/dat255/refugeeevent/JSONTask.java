package dat255.refugeeevent;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Oscar on 2016-09-29.
 */

public class JSONTask extends AsyncTask<String, String, String> {

    MainActivity ac;

    public JSONTask(MainActivity ac){
        this.ac = ac;
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

        reader = new BufferedReader(new InputStreamReader(stream));

        StringBuffer buffer = new StringBuffer();

        String line = "";
        while ((line = reader.readLine()) != null) {
        if(line.contains("distance")) {
        line = reader.readLine();
        line = line.replaceAll("[^0-9.]+","");
        buffer.append(line + " km");
        }
        }

        return buffer.toString();

        } catch (MalformedURLException e) {
        e.printStackTrace();
        } catch (IOException e) {
        e.printStackTrace();
        } finally {
        if (connection != null) {
        connection.disconnect();
        }
        try {
        if (reader != null) {
        reader.close();
        }
        } catch (IOException e) {
        e.printStackTrace();
        }

        }
        return null;
        }


@Override
protected void onPostExecute(String result){
            super.onPostExecute(result);
            ac.setText(result);
        }
}
