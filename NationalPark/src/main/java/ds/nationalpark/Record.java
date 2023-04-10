package ds.nationalpark;

import java.sql.Timestamp;

public class Record {
    private String topic;
    private String state;
    private String query;
    private String park;
    private Double min_fee;
    private Double max_fee;
    private String url;
    private String time;

    Record () {

    }

    Record (String topic, String state, String query, String park, Double min_fee, Double max_fee, String url, String time) {
        this.topic = topic;
        this.state = state;
        this.query = query;
        this.park = park;
        this.min_fee = min_fee;
        this.max_fee = max_fee;
        this.url = url;
        this.time = time;
    }
    public String getTopic() {return this.topic;}
    public String getState() {return this.state;}
    public String getQuery() {return this.query;}
    public String getPark() {return this.park;}
    public Double getMinFee() {return this.min_fee;}
    public Double getMaxFee() {return this.max_fee;}
    public String getUrl() {return this.url;}
    public String getTime() {return this.time;}
}