package ds.edu.cmu.nationalparkapp;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Apr 6, 2023
 *
 * Main Activity of National Park App.
 */
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

public class NationalPark extends AppCompatActivity {
    // This class
    NationalPark npac = this;
    // Reference to this object to update UI
    NationalPark npui;
    // Dropdown view for topic and state
    Spinner topicDropdown, stateDropdown;
    // Text for user input keyword
    EditText query;
    // Selected topic and state
    String topic, state;
    // Submit button view
    Button submitButton;
    // Search results ListView
    ListView resultList;
    // TextView if no result is found
    TextView notFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        npui = this;
        // Asynchronous tasks to get topic and state list from web service, and update to dropdown lists.
        GetDropdownInfo getDropdownInfo = new GetDropdownInfo();
        getDropdownInfo.get(npac, npui);
        // Bind to UI items.
        submitButton = (Button) findViewById(R.id.submit);
        query = (EditText) findViewById(R.id.query);
        notFound = findViewById(R.id.notFound);
        notFound.setVisibility(View.INVISIBLE);
        // Set up click listener for submit button
        submitButton.setOnClickListener(view -> {
            String q = query.getText().toString();
            // Asynchronous tasks to search park information and update to listview.
            GetParkInfo gpi = new GetParkInfo();
            gpi.search(topic, state, q, npac, npui);
        });

    }

    /**
     * Get the search result ListView ready for park information.
     * @param parkList list of park information
     */
    public void listReady(ArrayList<ParkInfo> parkList) {
        resultList = findViewById(R.id.resultList);
        // if parks have found
        if (parkList != null && parkList.size() != 0) {
            ParkInfoAdapter parkInfoAdapter = new ParkInfoAdapter(this, R.layout.activity_park_list_view, parkList);
            resultList.setAdapter(parkInfoAdapter);
            resultList.setVisibility(View.VISIBLE);
            notFound.setVisibility(View.INVISIBLE);
        } else { // if no result is found
            resultList.setVisibility(View.INVISIBLE);
            notFound.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Get the dropdown list ready.
     * @param topicList list of topics
     * @param stateList list of states
     */
    public void dropdownReady(ArrayList<String> topicList, ArrayList<String> stateList) {
        topicDropdown = (Spinner) findViewById(R.id.topic);
        stateDropdown = (Spinner) findViewById(R.id.state);
        // Set adapter
        ArrayAdapter<String> topicAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, topicList);
        topicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicDropdown.setAdapter(topicAdapter);
        // Set up listener for topic dropdown list
        topicDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                topic = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Set adapter
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateDropdown.setAdapter(stateAdapter);
        // Set up listener for state dropdown list
        stateDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                state = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Set the first item as the default option
        topicDropdown.setSelection(0);
        stateDropdown.setSelection(0);
    }


}