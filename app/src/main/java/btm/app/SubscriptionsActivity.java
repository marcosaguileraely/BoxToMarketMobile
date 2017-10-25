package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import btm.app.Adapters.SubscriptionAdapter;
import btm.app.Model.Subscriptions;

public class SubscriptionsActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Subscriptions";

    private static String username_global;
    private Button getData;
    private ListView listView;
    private SubscriptionAdapter adapter;
    private View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        username_global =  getIntent().getStringExtra(MainActivity.USER_GLOBAL);
        Log.d(TAG, username_global);

        listView = (ListView) findViewById(R.id.subscriptions_list);

        listSubscriptions(v);
    }

    public void listSubscriptions(View view){

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        //progress.show();

        Response.Listener<String> response = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "1 "+ response);

                progress.dismiss();
                if(response.contains(".png")){
                    SharedPreferences sharedPref    = getSharedPreferences("preferencias",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    Log.d(TAG, "Tiene al menos un resultado.");

                    // This method transforms the String to a Array String to populate the Subscriptions list
                    //getSubscriptionsList(response);
                    adapter = new SubscriptionAdapter(SubscriptionsActivity.this, getSubscriptionsList(response));
                    listView.setAdapter(adapter);

                } else {
                    Toast.makeText(SubscriptionsActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        new btm.app.Network.NetActions(this).listSubscriptions(username_global, response, progress);
    }

    public ArrayList<Subscriptions> getSubscriptionsList(String response){
        ArrayList<Subscriptions> items = new ArrayList<Subscriptions>();

        JSONArray jsonArray   = new JSONArray();
        JSONObject jsonObject = null;
        String[] mainToken    = response.split("\\|");

        for(int i=0;i<mainToken.length;i++){
            String subToken[] = mainToken[i].split(",");
            jsonObject        = new JSONObject();
            try {
                jsonObject.put("id", subToken[0]);
                jsonObject.put("qty", subToken[1]);
                jsonObject.put("img_uri", subToken[2]);
                jsonArray.put(jsonObject);
                items.add(new Subscriptions(Integer.parseInt(subToken[0]), Integer.parseInt(subToken[1]), subToken[2]));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, jsonArray.toString());
        return items;
    }

    //This code is only an sample how to convert the response to a JsonArray
    public JSONArray splitAndMakeJson(String response) {
        JSONArray jsonArray   = new JSONArray();
        JSONObject jsonObject = null;
        String[] mainToken    = response.split("\\|");

        for(int i=0;i<mainToken.length;i++){
            String subToken[] = mainToken[i].split(",");
            jsonObject        = new JSONObject();
            try {
                jsonObject.put("id", subToken[0]);
                jsonObject.put("qty", subToken[1]);
                jsonObject.put("img_uri", subToken[2]);
                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, jsonArray.toString());
        return jsonArray;
    }


}
