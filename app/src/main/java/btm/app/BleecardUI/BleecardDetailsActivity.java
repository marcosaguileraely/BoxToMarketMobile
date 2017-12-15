package btm.app.BleecardUI;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
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

    String price_data;
    String blee_id_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleecard_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        blee_price_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                price_data = parent.getItemAtPosition(position).toString();
                Log.d(TAG, price_data);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        blee_id_data = BleeDetails.getId();
        blee_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payAction(blee_id_data, price_data);
            }
        });

    }

    public void payAction(String id_blee, String price_value){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        //progress.show();

        Response.Listener<String> response = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "1 "+ response);
                progress.dismiss();

                if(response.contains("rsaToken")){
                    Log.d(TAG, "Tiene al menos un resultado. " + response + " length: "+response.length());
                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        try {
            new btm.app.Network.NetActions(this).getRsa(response, id_blee, price_value, progress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<String> textToArray(String prices){
        List<String> items = Arrays.asList(prices.split("\\s*,\\s*"));
        Log.d(TAG, " "+items);
        return items;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
