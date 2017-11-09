package btm.app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import btm.app.DataHolder.DataHolder;
import btm.app.DataHolder.DataHolderSubs;
import btm.app.Network.NetActions;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BuySubscriptionConfirmActivity extends AppCompatActivity {
    public static final String TAG = "DEV -> Pagar subs";
    public Context context = this;

    private Button pay;
    public Spinner payment_method, credit_card;
    private ImageView detail_img;
    private TextView title, price, type;
    public EditText token;

    public ArrayList<CC> listTc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_subscription_confirm);

        final NetActions netActions =  new NetActions(context);

        payment_method = (Spinner) findViewById(R.id.spinnerTipoRecarga);
        credit_card    = (Spinner) findViewById(R.id.spinnerCreditCards);
        token          = (EditText) findViewById(R.id.token);

        detail_img     = (ImageView) findViewById(R.id.pay_detail_img);
        title          = (TextView) findViewById(R.id.pay_detail_title);
        price          = (TextView) findViewById(R.id.pay_detail_price);
        type           = (TextView) findViewById(R.id.pay_detail_type);

        String img_uri    = DataHolderSubs.getImg_url();
        String type_prod  = "Tipo de producto: " + DataHolderSubs.getProduct_type();
        String price_prod = "Precio: " + DataHolderSubs.getPrice();

        token.setVisibility(GONE);
        listTc = new ArrayList<CC>();

        Glide.with(context)
                .load(img_uri)
                .centerCrop()
                .into(detail_img);
        type.setText(type_prod);
        price.setText(price_prod);
        title.setText(DataHolderSubs.getName());

        payment_method.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String modo = parent.getItemAtPosition(position).toString();
                Log.d(TAG, modo);

                if( modo.equals("Tarjeta de Crédito") || modo.equals("Credit card") ){
                    Log.d(TAG, "metodo tarjeta");
                    credit_card.setVisibility(VISIBLE);
                    token.setVisibility(GONE);

                    String datos = "&username="+ DataHolder.getUsername();
                    Log.d(TAG, "datos: "+datos);

                    netActions.http_get_data("menutarjetas", datos, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("Token")) {
                                Toast.makeText(getApplicationContext(), response + getString(R.string.error_token), Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }

                            if (response.contains("No tiene")) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                return;
                            }

                            String[] inf = response.split("\\|");
                            for (String cc : inf) {
                                Log.d(TAG, "cc: "+cc);
                                listTc.add(new CC(cc));
                            }

                            ArrayAdapter<CC> adapter = new ArrayAdapter<CC>(getApplicationContext(), R.layout.item_card, listTc);
                            credit_card.setAdapter(adapter);
                        }
                    });

                }
                if( modo.contains("Créditos BtM") || modo.contains("BtM Credit")){
                    Log.d(TAG, "metodo btm");
                    token.setVisibility(GONE);
                    credit_card.setVisibility(GONE);

                }
                if( modo.equals("token") || modo.equals("Token")){
                    Log.d(TAG, "metodo token");
                    token.setVisibility(VISIBLE);
                    credit_card.setVisibility(GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}
