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
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import btm.app.Adapters.ClubAdapter;
import btm.app.Adapters.SubsPublicAdapter;
import btm.app.Adapters.SubscriptionAdapter;
import btm.app.Model.Clubs;
import btm.app.Model.Subscriptions;
import btm.app.Model.SubscriptionsPublic;

public class BuyActivity extends AppCompatActivity {
    private static String username_global;
    public static final String TAG = "DEV -> Buy Subs & clubs";

    private GridView subsGridView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private SubsPublicAdapter subsPublicAdapter;
    private View v;

    Context context  = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        username_global =  getIntent().getStringExtra(SubscriptionsActivity.USER_GLOBAL);
        subsGridView    = (GridView) findViewById(R.id.SubsGridView);

        Log.d(TAG, username_global);

        listClubsPublic(v);
        listSubscriptionsPublic(v);
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
                if(response.contains("png") || response.contains("jpg")){
                    SharedPreferences sharedPref    = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    Log.d(TAG, "Tiene al menos un resultado.");

                    recyclerView = (RecyclerView) findViewById(R.id.ClubsRVCardView);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter = new ClubAdapter(getClubsList(response));
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        new btm.app.Network.NetActions(this).listClubsPublic(username_global, response, progress);
    }

    public void listSubscriptionsPublic(View view){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        //progress.show();

        Response.Listener<String> response = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "1 "+ response);

                progress.dismiss();
                if(response.contains("png") || response.contains("jpg")){
                    SharedPreferences sharedPref    = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    Log.d(TAG, "Tiene al menos un resultado.");

                    subsPublicAdapter = new SubsPublicAdapter(context, getSubscriptionsPublicList(response));
                    subsGridView.setAdapter(subsPublicAdapter);

                } else {
                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        new btm.app.Network.NetActions(this).listSubscriptionsPublic(username_global, response, progress);

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
                jsonObject.put("title", subToken[1]);
                jsonObject.put("img_uri", subToken[2]);
                jsonArray.put(jsonObject);
                items.add(new Clubs(subToken[0], subToken[1], subToken[2]));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, jsonArray.toString());
        return items;
    }

    public ArrayList<SubscriptionsPublic> getSubscriptionsPublicList(String response){
        ArrayList<SubscriptionsPublic> items = new ArrayList<SubscriptionsPublic>();

        JSONArray jsonArray   = new JSONArray();
        JSONObject jsonObject = null;
        String[] mainToken    = response.split("\\|");

        for(int i=0;i<mainToken.length;i++){
            String subToken[] = mainToken[i].split(",");
            jsonObject        = new JSONObject();
            try {
                jsonObject.put("value", subToken[0]);
                jsonObject.put("title", subToken[1]);
                jsonObject.put("img_uri", subToken[2]);
                jsonObject.put("id", subToken[3]);
                jsonArray.put(jsonObject);
                items.add(new SubscriptionsPublic(subToken[0], subToken[1], subToken[2], Integer.valueOf(subToken[3])));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, jsonArray.toString());
        return items;
    }
}
