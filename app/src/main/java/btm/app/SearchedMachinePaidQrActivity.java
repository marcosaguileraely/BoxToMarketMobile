package btm.app;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import btm.app.DataHolder.DataHolderMachineSearch;

public class SearchedMachinePaidQrActivity extends AppCompatActivity {

    public static final String TAG = "DEV -> QR ";

    ImageView qr_img;
    TextView total_pay;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_machine_paid_qr);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Compra exitosa");

        qr_img = (ImageView) findViewById(R.id.qr_img);
        total_pay = (TextView) findViewById(R.id.qr_total_pay);

        Log.w(TAG, " QR: " + DataHolderMachineSearch.getQr_code());

        Glide.with(context).load(DataHolderMachineSearch.getQr_code()).into(qr_img);
        total_pay.setText("Valot Total: $" + DataHolderMachineSearch.getTotal_pay());
    }

    public void onBackPressed(){
        Intent gotoMain = new Intent(context, MainActivity.class);
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
