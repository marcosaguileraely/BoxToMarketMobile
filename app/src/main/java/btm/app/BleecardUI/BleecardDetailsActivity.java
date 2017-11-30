package btm.app.BleecardUI;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import btm.app.Model.BleeDetails;
import btm.app.Model.Clubs;
import btm.app.R;

import static java.security.AccessController.getContext;

public class BleecardDetailsActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Blee details ";
    Context context = this;


    Button    blee_pay;
    Spinner   blee_price_list;
    ImageView blee_img;
    TextView  blee_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleecard_details);

        blee_id         = (TextView) findViewById(R.id.blee_id);
        blee_img        = (ImageView) findViewById(R.id.blee_img_uri);
        blee_pay        = (Button) findViewById(R.id.blee_pay_btn);
        blee_price_list = (Spinner) findViewById(R.id.blee_prices);

        Log.d(TAG, "id: " + BleeDetails.getId() + " img_uri: "+BleeDetails.getImg_uri());

        blee_id.setText("BtM: " + BleeDetails.getId());
        Glide.with(context)
                .load("https://www.bleecard.com/"+BleeDetails.getImg_uri())
                .into(blee_img);

        List<String> array = textToArray(BleeDetails.getPrices());

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, array); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blee_price_list.setAdapter(spinnerArrayAdapter);

    }

    public List<String> textToArray(String prices){
        List<String> items = Arrays.asList(prices.split("\\s*,\\s*"));
        Log.d(TAG, " "+items);
        return items;
    }
}
