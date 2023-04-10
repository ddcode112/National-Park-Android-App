package ds.nationalpark;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Apr 5, 2023
 *
 * This model includes data fetched from National Park Service API.
 * API documention: https://www.nps.gov/subjects/developer/api-documentation.htm
 * And writes the search terms and responses from API to mongodb.
 */

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import org.json.JSONArray;
import org.json.JSONObject;

import org.bson.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

public class NationalParkModel {
    // main path of API
    private final String sourceURL = "https://developer.nps.gov/api/v1/";
    // API key for National Park API
    private final String apiKey = "&api_key=x9UQM7UfcW6yfTt1YlskxmMggtkoKrOWN4hZIohT";
    // List of state codes for dropdown selection
    private final ArrayList<String> state = new ArrayList<>(Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE",
            "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA",
            "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH",
            "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"));
    // Topic to topic id mapping
    private TreeMap<String, String> topic_id;
    // Connect to MongoDB
    ConnectionString connectionString = new ConnectionString("mongodb://wantienc:b03106027@ac-wnxakuz-shard-00-01.2ipcl1k.mongodb.net:27017,ac-wnxakuz-shard-00-00.2ipcl1k.mongodb.net:27017,ac-wnxakuz-shard-00-02.2ipcl1k.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");

    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .serverApi(ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build())
            .build();
    MongoClient mongoClient = MongoClients.create(settings);
    MongoDatabase database = mongoClient.getDatabase("Project4");
    MongoCollection collection = database.getCollection("SearchLog");
    // List of search logs
    ArrayList<Record> records;
    // Queue to record the topics sorted by number of times in the search results
    PriorityQueue<Map.Entry<String, Integer>> topicQueue;
    // Queue to record the states sorted by number of times in the search results
    PriorityQueue<Map.Entry<String, Integer>> stateQueue;
    // Minimum entrance fee among all records according to the record from MongoDB
    Double minFee = 0.0;
    // Maximum entrance fee among all records according to the record from MongoDB
    Double maxFee = 0.0;

    /**
     * Constructor.
     */
    NationalParkModel() {
        this.topic_id = new TreeMap<>();
        this.setTopic_id();
    }

    /**
     * Get all states in json format.
     * @return JSONArray state
     */
    public JSONArray getStateList() {
        JSONArray stateList = new JSONArray(this.state);
        return stateList;
    }

    /**
     * Get all search logs.
     * @return ArrayList of records
     */
    public ArrayList<Record> getRecords() {return this.records;}

    /**
     * Get minimum entrance fee among all responses.
     * @return minimum entrance fee
     */
    public Double getMinFee() {return this.minFee;}

    /**
     * Get maximum entrance fee among all responses.
     * @return maximum entrance fee
     */
    public Double getMaxFee() {return this.maxFee;}

    /**
     * Set topic to id mapping from API.
     */
    private void setTopic_id() {
        String topicAddress = sourceURL + "topics?" + apiKey;
        String response = fetch(topicAddress);
        JSONObject output = new JSONObject(response);
        JSONArray outputArray = output.getJSONArray("data");
        for (int i = 0; i < outputArray.length(); i++) {
            JSONObject j = outputArray.getJSONObject(i);
            this.topic_id.put((String) j.get("name"), j.get("id").toString());
        }
    }

    /**
     * Get all topics.
     * @return topics in json format
     */
    public JSONArray getTopicList() {
        JSONArray topicList = new JSONArray(new ArrayList<>(topic_id.keySet()));
        return topicList;
    }

    /**
     * Get the id of a specific topic.
     * @param topic name of topic
     * @return id of topic
     */
    public String getTopicId(String topic) {
        return topic_id.get(topic);
    }

    /**
     * Search national parks by the topic, state, and keyword.
     * @param topic name of topic
     * @param state state code
     * @param q keyword
     * @return search results
     */
    public String search(String topic, String state, String q) {
        String parkList = getParkListByTopic(getTopicId(topic), state);
        String result = getParkInfo(parkList, topic, state, q);
        return result;
    }

    /**
     * Helper function of getParkListByTopic.
     * Get an array of parkCodes from API in the state.
     * @param url API
     * @param state state code of the target parks
     * @return parkCode (ex. parkCode1,parkCode2,parkCode3)
     */
    private String getParkListFromTopicParkAPI(String url, String state) {
        List<String> parkList = new ArrayList<>();
        String response = fetch(url);
        JSONObject output = new JSONObject(response);
        JSONObject resultObject = output.getJSONArray("data").getJSONObject(0);
        if (resultObject.has("parks")) {
            JSONArray parkArray = resultObject.getJSONArray("parks");
            for (int i = 0; i < parkArray.length(); i++) {
                JSONObject park = parkArray.getJSONObject(i);
                if (park.get("states").toString().contains(state)) {
                    parkList.add((String) park.get("parkCode"));
                }
            }
        }
        return String.join(",", parkList);
    }

    /**
     * Get an array of parks featuring a specific topic in the state.
     * @param topicId id of the topic
     * @param state state code
     * @return parkCode (ex. parkCode1,parkCode2,parkCode3)
     */
    private String getParkListByTopic(String topicId, String state) {
        String topicParkAPI = sourceURL + "topics/parks?id=" + topicId + apiKey;
        return getParkListFromTopicParkAPI(topicParkAPI, state);
    }

    /**
     * Get the info of parks in the list,
     * and write the user inputs and responses from API in MongoDB.
     * @param parkList parkCode (ex. parkCode1,parkCode2,parkCode3)
     * @param topic name of topic from user input
     * @param state state code from user input
     * @param q keyword from user input
     * @return API response in json string format
     */
    private String getParkInfo(String parkList, String topic, String state, String q) {
        JSONArray result = new JSONArray();
        Timestamp time = new Timestamp(System.currentTimeMillis()); // Get the current search time
        if (parkList != null && parkList.length() != 0) {
            String parkAPI;
            if (q == null || q.trim().length() == 0) { // without keyword
                parkAPI = sourceURL + "parks?parkCode=" + parkList + apiKey;
            } else { // with keyword
                q = q.replaceAll("[^a-zA-Z0-9 ]", "");
                q = q.replaceAll("\\s", "+");
                parkAPI = sourceURL + "parks?parkCode=" + parkList + "&q=" + q + apiKey;
            }

            String response = fetch(parkAPI);
            JSONObject output = new JSONObject(response);
            JSONArray parkArray = output.getJSONArray("data");
            if (parkArray.length() != 0) {
                for (int i = 0; i < parkArray.length(); i++) {
                    JSONObject park = parkArray.getJSONObject(i);
                    String name = park.get("fullName").toString();
                    String imgUrl = park.getJSONArray("images").getJSONObject(0).get("url").toString();
                    String description = park.get("description").toString();
                    JSONArray entranceFees = park.getJSONArray("entranceFees");
                    ArrayList<Float> fees = new ArrayList<>();
                    for (int j = 0; j < entranceFees.length(); j++) {
                        fees.add(Float.parseFloat(entranceFees.getJSONObject(j).get("cost").toString()));
                    }
                    Float minEntranceFee = Collections.min(fees);
                    Float maxEntranceFee = Collections.max(fees);
                    String url = park.get("url").toString();
                    // put only the needed information to return JSONArray
                    result.put(toJSONObject(name, imgUrl, description, minEntranceFee, maxEntranceFee, url));
                    writeToMongo(topic, state, q, name, minEntranceFee, maxEntranceFee, url, time);
                }
            } else {
                writeToMongo(topic, state, q, null, null, null, null, time);
            }

        } else {
            writeToMongo(topic, state, q, null, null, null, null, time);
        }
        return result.toString();
    }

    /**
     * Convert data to JSONObject.
     * @param name name of park
     * @param imgUrl url of image
     * @param description description of park
     * @param min minimum entrance fee
     * @param max maximum entrance fee
     * @param url url of park
     * @return JSONObject with all parameters
     */
    private JSONObject toJSONObject(String name, String imgUrl, String description, Float min, Float max, String url) {
        JSONObject p = new JSONObject();
        p.put("fullName", name);
        p.put("img", imgUrl);
        p.put("description", description);
        p.put("minEntranceFee", min);
        p.put("maxEntranceFee", max);
        p.put("url", url);
        return p;
    }

    /**
     * Fetch data from url.
     * Refer to Lab 2.
     * @param urlString source url
     * @return response
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

    /**
     * Write search logs to MongoDB.
     * @param topic name of topic from user input
     * @param state state code from user input
     * @param q keyword from user input
     * @param parkName name of park from API response
     * @param minFee minimum entrance fee from API response
     * @param maxFee maximum entrance fee from API response
     * @param url url of park from API response
     * @param searchTime search timestamp
     */
    private void writeToMongo(String topic, String state, String q, String parkName, Float minFee, Float maxFee, String url, Timestamp searchTime) {
        String currentUser = "user";
        Document doc = new Document("name", currentUser);
        doc.append("topic", topic);
        doc.append("state", state);
        doc.append("query", q);
        doc.append("park", parkName);
        doc.append("min_fee", minFee);
        doc.append("max_fee", maxFee);
        doc.append("url", url);
        doc.append("time", searchTime);
        collection.insertOne(doc);
    }

    /**
     * Set up dashboard with -
     * Three analytics:
     * 1. Top 3 most frequently searched topics
     * 2. Top 3 most frequently searched states
     * 3. Range of entrance fees among all responses
     *
     * Search Logs
     */
    public void setDashboard() {
        records = new ArrayList<>();
        Map<String, Integer> topicCount = new HashMap<>();
        Map<String, Integer> stateCount = new HashMap<>();
        FindIterable<Document> iterDoc = collection.find();
        // Iterate through MongoDB
        MongoCursor<Document> cur = iterDoc.iterator();
        while (cur.hasNext()) {
            Document d = cur.next();
            // Put topic and state with its count
            topicCount.put(d.get("topic").toString(), topicCount.getOrDefault(d.get("topic").toString(), 0) + 1);
            stateCount.put(d.get("state").toString(), stateCount.getOrDefault(d.get("state").toString(), 0) + 1);
            if (d.getDouble("min_fee") != null && d.getDouble("min_fee") < this.getMinFee()) {this.minFee = d.getDouble("min_fee");}
            if (d.getDouble("max_fee") != null && d.getDouble("max_fee") > this.getMaxFee()) {this.maxFee = d.getDouble("max_fee");}
            records.add(new Record(d.getString("topic"), d.getString("state"), d.getString("query"), String.valueOf(d.get("park")), d.getDouble("min_fee"), d.getDouble("max_fee"), String.valueOf(d.get("url")), d.get("time").toString()));
        }
        // Queue comparators
        topicQueue = new PriorityQueue<>(
                (o1, o2) -> o2.getValue() - o1.getValue());
        stateQueue = new PriorityQueue<>(
                (o1, o2) -> o2.getValue() - o1.getValue());
        // Add topic and state to priorityqueue
        for (Map.Entry<String, Integer> entry: topicCount.entrySet()) {
            topicQueue.add(entry);
        }
        for (Map.Entry<String, Integer> entry: stateCount.entrySet()) {
            stateQueue.add(entry);
        }

    }

    /**
     * Get the top 3 topics.
     * @return top 3 topics in ArrayList
     */
    public ArrayList<String> getTop3Topics() {
        int i = 3;
        ArrayList<String> top3Topics = new ArrayList<>();
        while (i > 0 && topicQueue.size() > 0) {
            top3Topics.add(topicQueue.poll().getKey());
            i--;
        }
        return top3Topics;
    }

    /**
     * Get the top 3 states
     * @return top 3 states in ArrayList
     */
    public ArrayList<String> getTop3States() {
        int i = 3;
        ArrayList<String> top3States = new ArrayList<>();
        while (i > 0 && stateQueue.size() > 0) {
            top3States.add(stateQueue.poll().getKey());
            i--;
        }
        return top3States;
    }

}



