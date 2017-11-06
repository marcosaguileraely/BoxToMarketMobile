package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import btm.app.Adapters.ClubAdapter;
import btm.app.Adapters.SubscriptionAdapter;
import btm.app.Model.Clubs;
import btm.app.Model.Subscriptions;

public class BuyActivity extends AppCompatActivity {
    private static String username_global;
    public static final String TAG = "DEV -> Buy Subs & clubs";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<String> dataSet;
    private View v;

    Context context  = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        username_global =  getIntent().getStringExtra(SubscriptionsActivity.USER_GLOBAL);
        Log.d(TAG, username_global);

        listClubsPublic(v);

        dataSet = new ArrayList<>();
        for (int i=0; i<=50; i++){
            dataSet.add("Title movie # " + i);
        }


        recyclerView = (RecyclerView) findViewById(R.id.ClubsRVCardView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ClubAdapter(dataSet);
        recyclerView.setAdapter(adapter);
    }


    public void listClubsPublic(View view){

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        //progress.show();

        Response.Listener<String> response = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "1 "+ response);

                progress.dismiss();
                if(response.contains(".png")){
                    SharedPreferences sharedPref    = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    Log.d(TAG, "Tiene al menos un resultado.");

                    // This method transforms the String to a Array String to populate the Subscriptions list
                    // getSubscriptionsList(response);
                    // adapter = new SubscriptionAdapter(context, getSubscriptionsList(response));
                    // listView.setAdapter(adapter);

                } else {
                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        new btm.app.Network.NetActions(this).listSubscriptions(username_global, response, progress);
    }


    public ArrayList<Clubs> getClubsList(String response){
        ArrayList<Clubs> items = new ArrayList<Clubs>();

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
                items.add(new Clubs(Integer.parseInt(subToken[0]), subToken[1], subToken[2]));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, jsonArray.toString());
        return items;
    }
}
