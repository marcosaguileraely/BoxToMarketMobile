package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;

import btm.app.Adapters.SubscriptionAdapter;

public class BuyActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Buy Subs & clubs";
    Context context  = this;
    private View v;

    private static String username_global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        username_global =  getIntent().getStringExtra(SubscriptionsActivity.USER_GLOBAL);
        Log.d(TAG, username_global);

        listClubsPublic(v);
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
                    //adapter = new SubscriptionAdapter(context, getSubscriptionsList(response));
                    //listView.setAdapter(adapter);

                } else {
                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        new btm.app.Network.NetActions(this).listSubscriptions(username_global, response, progress);
    }
}
