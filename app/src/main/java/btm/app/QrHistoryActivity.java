package btm.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import btm.app.Adapters.MachinesSearchAdapter;
import btm.app.Adapters.QRHistoryAdapter;
import btm.app.DataHolder.DataHolder;
import btm.app.Model.QRHistory;
import btm.app.Utils.Utils;

import static btm.app.BuyActivity.USER_GLOBAL_SENDER;

public class QrHistoryActivity extends AppCompatActivity {

    public static final String TAG = "DEV -> Qr history";

    Context context = this;
    private QRHistoryAdapter adapter;

    View v;
    ListView qr_history;

    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.qr_history));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        qr_history = (ListView) findViewById(R.id.qr_history_listview);

        listQRHistory(v);
    }

    public void listQRHistory(View v){
        String response = utils.getQRHistory(DataHolder.getUsername());
        Log.w(TAG, " "+response);

        if(response.contains("png") || response.contains("jpg")){

            adapter = new QRHistoryAdapter(QrHistoryActivity.this, getQRHistoryList(response));
            adapter.notifyDataSetChanged();
            qr_history.setAdapter(adapter);

        } else {
            Toast.makeText(context, response, Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<QRHistory> getQRHistoryList(String response){
        ArrayList<QRHistory> items = new ArrayList<QRHistory>();
        JSONArray jsonArray   = new JSONArray();
        JSONObject jsonObject = null;
        String[] mainToken    = response.split("\\|");

        for(int i = 0 ; i < mainToken.length ; i++){
            String subToken[] = mainToken[i].split(",");
            jsonObject        = new JSONObject();

            try {
                jsonObject.put("serial", subToken[0]);
                jsonObject.put("created_at", subToken[1]);
                jsonObject.put("consumed_at", subToken[2]);
                jsonObject.put("total", subToken[3]);
                jsonObject.put("img_qr_src", subToken[4]);

                jsonArray.put(jsonObject);

                items.add(new QRHistory(subToken[0], subToken[1], subToken[2], subToken[3], subToken[4]));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, jsonArray.toString());
        return items;
    }

    public void onBackPressed(){
        Intent gotoBuy = new Intent(QrHistoryActivity.this, BuyActivity.class);
        gotoBuy.putExtra(USER_GLOBAL_SENDER, DataHolder.getUsername());
        startActivity(gotoBuy);
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
