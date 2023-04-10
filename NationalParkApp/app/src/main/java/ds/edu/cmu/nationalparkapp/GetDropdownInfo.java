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
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetDropdownInfo {
    // Arraylist to store the topics
    public List<String> topicList;
    // JSONArray to get the responded topics
    JSONArray topicJSONArray;
    // ArrayList to store the states
    public List<String> stateList;
    // JSONArray to get the responded states
    JSONArray stateJSONArray;
    // for callback
    NationalPark np = null;

    /**
     * Get the list of topics and states.
     * @param activity the UI thread activity
     * @param np the callback method's class
     */
    public void get(Activity activity, NationalPark np) {
        this.np = np;
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
                    }
                    // This is magic: activity should be set to MainActivity.this
                    //    then this method uses the UI thread
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                onPostExecute();
                            } catch (JSONException e) {
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
         * Get topics and states JSONArray.
         * @throws JSONException
         */
        private void doInBackground() throws JSONException {
            topicJSONArray = getTopicArray();
            stateJSONArray = getStateArray();
        }

        /**
         * Convert topic and state JSONArray to ArrayList,
         * and then run on the UI thread to update the dropdown UI.
         * @throws JSONException
         */
        public void onPostExecute() throws JSONException {
            topicList = new ArrayList<>();
            stateList = new ArrayList<>();
            if (topicJSONArray != null && topicJSONArray.length() != 0) {
                for (int i = 0; i < topicJSONArray.length(); i++) {
                    topicList.add(topicJSONArray.getString(i));
                }
            }
            if (stateJSONArray != null && stateJSONArray.length() != 0) {
                for (int i = 0; i < stateJSONArray.length(); i++) {
                    stateList.add(stateJSONArray.getString(i));
                }
            }
            np.dropdownReady((ArrayList<String>) topicList, (ArrayList<String>) stateList);
        }

        /**
         * Get topic JSONArray.
         * @return topic JSONArray
         * @throws JSONException
         */
        private JSONArray getTopicArray() throws JSONException {
            String url = "https://ddcode112-automatic-capybara-x6xx6gjq65r3wpp-8080.preview.app.github.dev/getTopics";
            String response = fetch(url);
            return new JSONArray(response);
        }

        /**
         * Get state JSONArray.
         * @return state JSONArray
         * @throws JSONException
         */
        private JSONArray getStateArray() throws JSONException {
            String url = "https://ddcode112-automatic-capybara-x6xx6gjq65r3wpp-8080.preview.app.github.dev/getStates";
            String response = fetch(url);
            return new JSONArray(response);
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
                e.printStackTrace();
                // Do something reasonable.  This is left for students to do.
            }
            return response;
        }
    }
}
