package btm.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class CardActivity extends DataJp {

    private static final int MY_SCAN_REQUEST_CODE = 0xA1B9;
    private TextView tVCc;
    private Button buttonReg;
    private String infocard;
    private String user;
    private SharedPreferences shPref;
    private ProgressDialog pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tVCc        = (TextView) findViewById(R.id.textViewCC);
        buttonReg   = (Button) findViewById(R.id.button5);
        pb          = new ProgressDialog(this);
        pb.setMessage(getString(R.string.inf_dialog));

        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);

                // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);


        shPref = getSharedPreferences(getString(R.string.preferencias), Context.MODE_PRIVATE);
        user = shPref.getString(LoginActivity.USERNAME,"--");

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pb.show();
                //buttonReg.setEnabled(false);
                final String datos = "&username="+user
                                    +"&datos="+infocard;
                //tVCc.setText(datos);

                new Request(CardActivity.this).http_get("registrocard", datos, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //tVCc.setText(datos);
                        pb.dismiss();
                        //buttonReg.setEnabled(true);
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        alert_info(response, getString(R.string.Info), android.R.drawable.ic_dialog_info);
                    }
                }, pb);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = getString(R.string.numero_cc) + scanResult.getLastFourDigitsOfCardNumber() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += getString(R.string.expira) + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += getString(R.string.cvvcc) + scanResult.cvv.length() + getString(R.string.digitos);
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += getString(R.string.postal) + scanResult.postalCode + "\n";
                }
                String mes = (scanResult.expiryMonth  < 10)?"0"+scanResult.expiryMonth:""+scanResult.expiryMonth;
                infocard = Base64.encodeToString((scanResult.expiryYear + "|" + mes + "|" + scanResult.cvv + "|" + scanResult.getFormattedCardNumber().replace(" ","")).getBytes(), Base64.DEFAULT);
            }
            else {
                resultDisplayStr = getString(R.string.cc_cancelada);
                buttonReg.setEnabled(false);
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
            // Toast.makeText(this, resultDisplayStr, Toast.LENGTH_LONG).show();
            tVCc.setText(resultDisplayStr);

            //new Request(this).http_get("registrocard", datos, response);
        }
        // else handle other activity results
    }
}
