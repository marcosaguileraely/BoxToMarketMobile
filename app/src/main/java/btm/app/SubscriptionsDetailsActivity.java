package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import btm.app.Adapters.SubsPublicAdapter;
import btm.app.Model.Clubs;
import btm.app.Model.SubscriptionsDetails;
import btm.app.Model.SubscriptionsPublic;

import static java.util.Arrays.asList;

public class SubscriptionsDetailsActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Subs details";

    public static final String USER_GLOBAL_SENDER = "USERNAME";

    private static String username_global, img_uri_txt;
    private static int id;

    private Context context = this;
    private View v;

    ArrayList<SubscriptionsDetails> subscriptionsDetailsArrayList;

    private TextView type, category, qty, price, seller, location, description;
    private ImageView img_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_subscriptions_details);

        username_global = getIntent().getStringExtra(SubsPublicAdapter.USER_GLOBAL);
        id              = getIntent().getIntExtra(SubsPublicAdapter.ID_GLOBAL, 0);
        img_uri_txt     = getIntent().getStringExtra(SubsPublicAdapter.URI_IMG);

        type        = (TextView) findViewById(R.id.detail_data_type);
        category    = (TextView) findViewById(R.id.detail_data_category);
        qty         = (TextView) findViewById(R.id.detail_data_qty);
        price       = (TextView) findViewById(R.id.detail_data_price);
        seller      = (TextView) findViewById(R.id.detail_data_seller);
        description = (TextView) findViewById(R.id.detail_data_description);
        img_uri     = (ImageView) findViewById(R.id.detail_img);

        Toast.makeText(context, "Data passed-> Username: "+username_global + " id: "+id, Toast.LENGTH_SHORT).show();

        getSubscriptionsPublicDetails(v);
    }

    public void getSubscriptionsPublicDetails(View view){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.inf_dialog));
        //progress.show();

        Response.Listener<String> response = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "1 "+ response);

                progress.dismiss();
                if(response.contains("|")){
                    SharedPreferences sharedPref    = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    Log.d(TAG, "Tiene al menos un resultado.");
                    Log.d(TAG, getSubscriptionsDetailsList(response).toString());
                    Log.d(TAG, "size " + String.valueOf(getSubscriptionsDetailsList(response).size()));

                    int response_size = getSubscriptionsDetailsList(response).size();
                    ArrayList<SubscriptionsDetails> model = getSubscriptionsDetailsList(response);

                    String type_data        = model.get(0).getType();        //getting type from model
                    String category_data    = model.get(1).getCategory();    //getting category from model
                    int qty_data            = model.get(2).getQty();         //getting qty=quantity from model
                    String aux_data         = model.get(3).getAux();         //getting aux from model
                    String price_data       = model.get(4).getPrice();       //getting price from model
                    String seller_data      = model.get(5).getSeller();      //getting seller from model
                    String location_data    = model.get(6).getLocation();    //getting location from model
                    String description_data = model.get(7).getDescription(); //getting description from model

                    Log.d(TAG, "I'm in! "+ "-> "+type + "-> "+category);

                    Glide.with(context).load(img_uri_txt).into(img_uri);
                    type.setText(type_data);
                    category.setText(category_data);
                    qty.setText(String.valueOf(qty_data));
                    price.setText(price_data);
                    seller.setText(seller_data);
                    description.setText(description_data);

                } else {
                    Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        new btm.app.Network.NetActions(this).getDetailsSubscriptions(username_global, response, progress, id);
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

}
