package btm.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import btm.app.DataHolder.DataHolderQrActivesDetails;

public class QrActiveDetailActivity extends AppCompatActivity {

    private static String username_global;
    public static final String TAG = "DEV -> Qr Active";

    private Context context = this;

    public static final String USER_GLOBAL_SENDER = "USERNAME"; //this variable set username data vía intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_active_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.qr_detail));

        ImageView qr_img = (ImageView) findViewById(R.id.qr_img);
        TextView serial = (TextView) findViewById(R.id.serial_text);
        TextView value = (TextView) findViewById(R.id.value_text);
        TextView date = (TextView) findViewById(R.id.date_text);

        Glide.with(context).load("https://www.bleecard.com/api/qrcodes/" + DataHolderQrActivesDetails.getQr_image_ui() + ".png").into(qr_img);
        serial.setText("Máquina # " + DataHolderQrActivesDetails.getSerial());
        value.setText("$" + DataHolderQrActivesDetails.getValue());
        date.setText("" + DataHolderQrActivesDetails.getDate());
    }

    public void onBackPressed(){
        Log.d(TAG, "onBackPressed Called");
        Intent gotoMain = new Intent(QrActiveDetailActivity.this, BuyActivity.class);
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
