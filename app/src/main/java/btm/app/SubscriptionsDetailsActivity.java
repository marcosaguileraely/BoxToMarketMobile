package btm.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import btm.app.Adapters.SubsPublicAdapter;
import btm.app.DataHolder.DataHolderSubs;
import btm.app.Model.Clubs;
import btm.app.Model.SubscriptionsDetails;
import btm.app.Model.SubscriptionsPublic;

import static btm.app.R.id.textView;
import static java.util.Arrays.asList;

public class SubscriptionsDetailsActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Subs details";

    public static final String USER_GLOBAL_SENDER = "USERNAME";

    private static String username_global, img_uri_txt, title;
    private static int id;

    private Context context = this;
    private View v;

    ArrayList<SubscriptionsDetails> subscriptionsDetailsArrayList;

    private TextView name, type, category, qty, price, seller, location, description;
    private ImageView img_uri;
    private Button pay_subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_subscriptions_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username_global  = getIntent().getStringExtra(SubsPublicAdapter.USER_GLOBAL);
        id               = getIntent().getIntExtra(SubsPublicAdapter.ID_GLOBAL, 0);
        img_uri_txt      = getIntent().getStringExtra(SubsPublicAdapter.URI_IMG);
        title            = getIntent().getStringExtra(SubsPublicAdapter.TITLE);

        name             = (TextView) findViewById(R.id.detail_title);
        type             = (TextView) findViewById(R.id.detail_data_type);
        category         = (TextView) findViewById(R.id.detail_data_category);
        qty              = (TextView) findViewById(R.id.detail_data_qty);
        price            = (TextView) findViewById(R.id.detail_data_price);
        seller           = (TextView) findViewById(R.id.detail_data_seller);
        description      = (TextView) findViewById(R.id.detail_data_description);
        img_uri          = (ImageView) findViewById(R.id.detail_img);
        pay_subscription = (Button) findViewById(R.id.pay_subscription);

        //Toast.makeText(context, "Data passed-> Username: "+username_global + " id: "+id, Toast.LENGTH_SHORT).show();
        pay_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoPay = new Intent(context, BuySubscriptionConfirmActivity.class);
                startActivity(gotoPay);
            }
        });
        new AsyncGetHttpData().execute(""); //AsyncTask to improve the performance in the app *
    }

    private class AsyncGetHttpData extends AsyncTask<String, Void, String> {
        ProgressDialog progress = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            progress.setMessage(getString(R.string.inf_dialog));
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //runOnUiThread(new Runnable() {
            //    @Override
            //    public void run() {
                    getSubscriptionsPublicDetails(SubscriptionsDetailsActivity.this);
            //    }
            //});
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
           progress.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void getSubscriptionsPublicDetails(Activity view){
        try {
            final String data = new btm.app.Network.NetActions(this).getFullDetails(username_global, id);
            int data_lenth = data.length();
            Log.d(TAG, "OkHttp: " + data);

            if(data.contains("|")){
                Log.d(TAG, "Tiene al menos un resultado.");
                Log.d(TAG, "size " + data_lenth);

                ArrayList<SubscriptionsDetails> model = getSubscriptionsDetailsList(data);

                final String type_data        = model.get(0).getType();        //getting type from model
                final String category_data    = model.get(1).getCategory();    //getting category from model
                final int qty_data            = model.get(2).getQty();         //getting qty = quantity from model
                final String aux_data         = model.get(3).getAux();         //getting aux from model
                final String price_data       = model.get(4).getPrice();       //getting price from model
                final String seller_data      = model.get(5).getSeller();      //getting seller from model
                final String location_data    = model.get(6).getLocation();    //getting location from model
                final String description_data = model.get(7).getDescription(); //getting description from model

                Log.d(TAG, "I'm in! "+ "-> " + type + "-> "+category);

                view.runOnUiThread(new Runnable() {
                    public void run(){

                        Glide.with(context)
                                .load(img_uri_txt)
                                .into(img_uri);

                        name.setText(title);
                        type.setText(type_data);
                        category.setText(category_data);
                        qty.setText(String.valueOf(qty_data));
                        price.setText(price_data);
                        seller.setText(seller_data);
                        description.setText(description_data);
                    }
                });

            } else {
                view.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "¡Ups! algo no esta funcionando bien. Verifica tu red e intenta nuevamente.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Some mesagge: " + data);
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<SubscriptionsDetails> getSubscriptionsDetailsList(String response){
        ArrayList<SubscriptionsDetails> items = new ArrayList<SubscriptionsDetails>();

        JSONArray jsonArray   = new JSONArray();
        JSONObject jsonObject = null;
        String[] mainToken    = response.split("\\|");
        Log.d(TAG, "MainToken size "+ mainToken.length);

        for(int i = 0; i < mainToken.length; i++){
            //String subToken[] = mainToken[i].split(",");
            jsonObject  = new JSONObject();
            try {
                jsonObject.put("type", mainToken[0]);
                jsonObject.put("category", mainToken[1]);
                jsonObject.put("qty", mainToken[2]);
                jsonObject.put("aux", mainToken[3]);
                jsonObject.put("price", mainToken[4]);
                jsonObject.put("seller", mainToken[5]);
                jsonObject.put("location", mainToken[6]);
                jsonObject.put("description", mainToken[8]);

                items.add(new SubscriptionsDetails( mainToken[0],
                                                    mainToken[1],
                                                    Integer.valueOf(mainToken[2]),
                                                    mainToken[3],
                                                    mainToken[4],
                                                    mainToken[5],
                                                    mainToken[6],
                                                    mainToken[8]));

                DataHolderSubs.setId(id);
                DataHolderSubs.setImg_url(img_uri_txt);
                DataHolderSubs.setName(title);
                DataHolderSubs.setProduct_type(mainToken[0]);
                DataHolderSubs.setPrice(mainToken[4]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jsonArray.put(jsonObject);

        Log.d(TAG, jsonArray.toString());
        return items;
    }

    @Override
    public void onBackPressed() {
        Intent gotoBuy = new Intent(context, BuyActivity.class);
        gotoBuy.putExtra(USER_GLOBAL_SENDER, username_global);
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
