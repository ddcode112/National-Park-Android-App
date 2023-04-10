package ds.edu.cmu.nationalparkapp;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Apr 6, 2023
 *
 * This class provides capabilities to get the topic list and state list from web service.
 * And report the results back to dropdown UI.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class GetParkInfo {
    // for callback
    NationalPark np = null;
    // topic from input
    String topic = null;
    // state code from input
    String stateCode = null;
    // query from input
    String q = null;
    // ArrayList to store the information of parks
    public List<ParkInfo> parkList;
    // JSONArray to store the responses from web service
    JSONArray parkJSONArray;

    /**
     * Search for park information.
     * @param topic name of topic from user input
     * @param stateCode state code from user input
     * @param q query/keyword from user input
     * @param activity the UI thread activity
     * @param np the callback method's class
     */
    public void search(String topic, String stateCode, String q, Activity activity, NationalPark np) {
        this.np = np;
        this.topic = topic;
        this.stateCode = stateCode;
        this.q = q;
        new BackgroundTask(activity).execute();
    }
    /**
     * Implements a background thread.
     * Refer to AndroidInterestingPicture Lab.
     */
    private class BackgroundTask {
        private Activity activity; // The UI thread
        public BackgroundTask(Activity activity) {
            this.activity = activity;
        }
        /**
         * Background thread starts.
         * Call doInBackground() to get data from web service,
         * and call onPostExecute() to update to UI.
         */
        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        doInBackground();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    // This is magic: activity should be set to MainActivity.this
                    //    then this method uses the UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                onPostExecute();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }).start();
        }
        /**
         * Call startBackground().
         */
        private void execute() {
            startBackground();
        }
        /**
         * Call search function to get the resulted JSONArray,
         * convert the JSONObject to ParkInfo object,
         * and store it into parkList ArrayList.
         * @throws JSONException
         */
        private void doInBackground() throws JSONException, MalformedURLException {
            parkJSONArray = search(topic, stateCode, q);
            parkList = new ArrayList<>();
            if (parkJSONArray != null && parkJSONArray.length() != 0) {
                for (int i = 0; i < parkJSONArray.length(); i++) {
                    ParkInfo p = new ParkInfo();
                    p.fullName = parkJSONArray.getJSONObject(i).getString("fullName");
                    p.description = parkJSONArray.getJSONObject(i).getString("description");
                    p.url = parkJSONArray.getJSONObject(i).getString("url");
                    URL u = new URL(parkJSONArray.getJSONObject(i).getString("img"));
                    p.img = getRemoteImage(u);
                    p.min = Float.parseFloat(parkJSONArray.getJSONObject(i).getString("minEntranceFee"));
                    p.max = Float.parseFloat(parkJSONArray.getJSONObject(i).getString("maxEntranceFee"));
                    parkList.add(p);
                }
            }
        }

        /**
         * Run on the UI thread to update the ListView.
         * @throws JSONException
         * @throws IOException
         */
        public void onPostExecute() throws JSONException, IOException {
            np.listReady((ArrayList<ParkInfo>) parkList);
        }

        /**
         * Search park info by topic, state, and q.
         * @param topic name of topic from user input
         * @param state state code from user input
         * @param q query/keyword from user input
         * @return responses in JSONArray format
         * @throws JSONException
         */
        private JSONArray search(String topic, String state, String q) throws JSONException {
            String sourceURL = "https://ddcode112-automatic-capybara-x6xx6gjq65r3wpp-8080.preview.app.github.dev/NationalPark?";
            String url = sourceURL + "topic=" + topic + "&stateCode=" + state + "&q=" + q;
            String response = fetch(url);

            return new JSONArray(response);
        }

        /**
         * Given a URL referring to an image, return a bitmap of that image.
         * Refer to AndroidInterestingPicture Lab.
         * @param url source url
         * @return Bitmap of the image
         */
        private Bitmap getRemoteImage(final URL url) {
            try {
                final URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        /**
         * Fetch data from url.
         * Refer to Lab 2.
         * @param urlString source url
         * @return responses in String
         */
        private String fetch(String urlString) {
            String response = "";
            try {
                URL url = new URL(urlString);
                /*
                 * Create an HttpURLConnection.  This is useful for setting headers
                 * and for getting the path of the resource that is returned (which
                 * may be different than the URL above if redirected).
                 * HttpsURLConnection (with an "s") can be used if required by the site.
                 */
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String str;
                // Read each line of "in" until done, adding each to "response"
                while ((str = in.readLine()) != null) {
                    // str is one line of text readLine() strips newline characters
                    response += str;
                }

                in.close();
            } catch (IOException e) {
                System.out.println("Eeek, an exception");
                // Do something reasonable.  This is left for students to do.
            }
            return response;
        }
    }
}
