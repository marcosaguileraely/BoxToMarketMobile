package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import btm.app.Adapters.ClubAdapter;
import btm.app.Adapters.QrActivesAdapter;
import btm.app.Adapters.SubsPublicAdapter;
import btm.app.DataHolder.DataHolder;
import btm.app.DataHolder.DataHolderQrAux;
import btm.app.Model.Clubs;
import btm.app.Model.QrActives;
import btm.app.Model.SubscriptionsPublic;
import btm.app.Utils.Utils;

public class BuyActivity extends AppCompatActivity {
    private static String username_global;
    public static final String TAG = "DEV -> Buy Subs & clubs";

    public static final String USER_GLOBAL_SENDER = "USERNAME"; //this variable set username data vía intent

    private GridView subsGridView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    //private SubsPublicAdapter qrActivesAdapter;
    private QrActivesAdapter qrActivesAdapter;
    private Button view_qr_history;
    private View v;

    Context context  = this;
    Utils utils = new Utils();

    //String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.qr_availables));

        username_global =  getIntent().getStringExtra(SubscriptionsActivity.USER_GLOBAL);
        subsGridView    = (GridView) findViewById(R.id.SubsGridView);

        view_qr_history = (Button) findViewById(R.id.qr_code_history);

        //Log.d(TAG, username_global);

        listClubsPublic(v);
        listSubscriptionsPublic(v);

        view_qr_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToHistory =  new Intent(context, QrHistoryActivity.class);
                startActivity(goToHistory);
            }
        });
    }

    public void listClubsPublic(View view){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        //progress.show();

        Response.Listener<String> response = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

                progress.dismiss();
                if(response.contains("png") || response.contains("jpg")){
                    SharedPreferences sharedPref    = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    //Log.d(TAG, "Tiene al menos un resultado.");

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

                    //subsPublicAdapter = new SubsPublicAdapter(context, getSubscriptionsPublicList(response), username_global);
                    //subsGridView.setAdapter(subsPublicAdapter);

                    qrActivesAdapter = new QrActivesAdapter(context, getActivesQRList(response), DataHolder.getUsername());
                    subsGridView.setAdapter(qrActivesAdapter);

                    /* Si estas viendo esta pieza de código quizá
                     * estes buscando la función que realiza el Intent,
                     * dicha funcion no se encuentra aquí, para verla dirigete la clase: Adapters > SubsPublicAdapter.class
                     * y en la línea 58 lo encontrarás.
                     */

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

        for(int i=0 ; i < mainToken.length ; i++){
            String subToken[] = mainToken[i].split(",");
            jsonObject        = new JSONObject();

            try {
                jsonObject.put("type", subToken[0]);
                jsonObject.put("img_uri", subToken[1]);
                jsonObject.put("available_qty", subToken[2]);

                jsonArray.put(jsonObject);

                items.add(new Clubs(subToken[0], subToken[1], subToken[2]));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Log.d(TAG, jsonArray.toString());
        return items;
    }

    public ArrayList<QrActives> getActivesQRList(String response){
        ArrayList<QrActives> items = new ArrayList<QrActives>();

        JSONArray jsonArray   = new JSONArray();
        JSONObject jsonObject = null;
        String[] mainToken    = response.split("\\|");

        for(int i=0;i<mainToken.length;i++){
            String subToken[] = mainToken[i].split(",");
            jsonObject        = new JSONObject();
            try {
                jsonObject.put("serial", subToken[0]);
                jsonObject.put("date_time", subToken[1]);
                jsonObject.put("brand_image_img", subToken[2]);
                jsonObject.put("total", subToken[3]);
                jsonObject.put("qr_code", subToken[4]);
                jsonArray.put(jsonObject);
                items.add(new QrActives(subToken[0], subToken[1], subToken[2], subToken[3], subToken[4]));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Log.d(TAG, jsonArray.toString());
        return items;
    }

    class RenderGridTopAsync extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            Log.d(TAG + " Pre Execute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            String response = utils.getBrandsMachines(DataHolder.getUsername());
            DataHolderQrAux.setResponse(response);

            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

    class RenderGridBigAsync extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            Log.d(TAG + " Pre Execute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            String response = utils.getQRActives(DataHolder.getUsername());
            DataHolderQrAux.setBigResponse(response);

            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

    public void onResume(){
        super.onResume();

        username_global =  getIntent().getStringExtra(SubscriptionsDetailsActivity.USER_GLOBAL_SENDER);
        subsGridView    = (GridView) findViewById(R.id.SubsGridView);

        //Log.d(TAG, username_global);

        listClubsPublic(v);
        listSubscriptionsPublic(v);
    }

    public void onBackPressed(){
        Log.d(TAG, "onBackPressed Called");
        Intent gotoMain = new Intent(BuyActivity.this, MainActivity.class);
        gotoMain.putExtra(USER_GLOBAL_SENDER, username_global);
        startActivity(gotoMain);
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
