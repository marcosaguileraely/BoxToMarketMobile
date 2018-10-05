package btm.app;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.app.AlertDialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import btm.app.BleecardUI.BleListActivity;
import btm.app.DataHolder.DataHolder;
import btm.app.DataHolder.DataHolderMachineSearch;
import btm.app.Utils.Utils;

import static btm.app.SearchedMachineActivity.MACHINEDATA;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static String pais;
    private static String data;
    private static String username_global;

    public static final String USER_GLOBAL = "USERNAME";
    public static final String MACHINEDATA = "";
    public static final String TAG = "DEV -> Main";

    private static final int REQUEST_ENABLE_BT = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BluetoothAdapter mBluetoothAdapter;
    LocationManager locationManager;

    public Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_home_white_24dp);
        //getSupportActionBar().setTitle("  "+getString(R.string.mi_billetera));
        getSupportActionBar().setTitle("  "+getString(R.string.app_name));

        ////if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
            // fire an intent to display a dialog asking the user to grant permission to enable it.
            // REQUEST ACCESS_FINE_LOCATION for +M versions
            ////checkLocationPermission();
        ////} else {
        ////    Log.d(TAG, "Your android version is -M. Bluethoot ya activado");
        ////}

        ////final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ////mBluetoothAdapter = bluetoothManager.getAdapter();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        ////if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
        ////    Toast.makeText(this, "Device not supported", Toast.LENGTH_SHORT).show();
        ////    finish();
        ////}

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //pais          = getIntent().getStringArrayExtra(LoginActivity.PAIS)[1];
               pais     = DataHolder.getId_country();
        String username = DataHolder.getUsername();
               data     = DataHolder.getData();

        //Log.d(TAG, "-> pais:" + pais + "-> user:"+ username + "-> data:" + data);
        data            = getIntent().getStringExtra(LoginActivity.DATOS);
        username_global = getIntent().getStringExtra(LoginActivity.USERNAME);
    }

    public void onCC(View view){
        Intent intent = new Intent(this, CardActivity.class);
        this.startActivity(intent);
    }

    public void onTransfer(View view){
        Intent intent = new Intent(this, TransferActivity.class);
        this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_pass:
                Intent gotoChangePass = new Intent(context, ChangePassActivity.class);
                startActivity(gotoChangePass);
                return true;

            case R.id.action_perfil:
                return true;

            case R.id.action_bp:
                return true;

            case R.id.action_close:
                finish();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_DATA = "section_data";
        private TextView textViewTrm, textViewSaldoCreditos, textViewSaldoCompensacion;
        public Button Subscriptions, Bleecard, AddMoney, RequestMoney, AddBankAccount;
        public Button BuyToken;

        Utils utils = new Utils();

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String sectionData) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_DATA, sectionData);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            View rootView               = inflater.inflate(R.layout.fragment_main, container, false);
            textViewTrm                 = (TextView) rootView.findViewById(R.id.textViewTrm);
            textViewSaldoCompensacion   = (TextView) rootView.findViewById(R.id.textViewSaldoCompensacion);
            textViewSaldoCreditos       = (TextView) rootView.findViewById(R.id.textViewSaldoCredito);
            Subscriptions               = (Button) rootView.findViewById(R.id.my_subscriptions_btn);
            Bleecard                    = (Button) rootView.findViewById(R.id.bleecard_btn);
            AddMoney                    = (Button) rootView.findViewById(R.id.button_add_money);
            RequestMoney                = (Button) rootView.findViewById(R.id.button_request);
            AddBankAccount              = (Button) rootView.findViewById(R.id.button_bank_account);
            BuyToken                    = (Button) rootView.findViewById(R.id.button10);
            final SearchView searchMachine    = (SearchView) rootView.findViewById(R.id.search_machine_searchview);

            searchMachine.setQueryHint("Para comprar digita el # de la máquina");
            searchMachine.setIconifiedByDefault(false);

            searchMachine.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String wsBtmData = utils.getSearchDetails(query);
                    Log.w(TAG, " " + wsBtmData.toString());

                    DataHolderMachineSearch.setSearch_data(wsBtmData);
                    DataHolderMachineSearch.setMachine_code(query);

                    Intent goToMachineSearch = new Intent(getActivity(), SearchedMachineActivity.class);
                    startActivity(goToMachineSearch);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //Log.w(TAG, "Something happens when Text Changes");
                    return false;
                }
            });

            Subscriptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEV -> Main ", "-> " + username_global);
                    Intent intent = new Intent(getActivity(), BuyActivity.class);
                    //Intent intent = new Intent(getActivity(), SubscriptionsActivity.class); //<-- Before
                    startActivity(intent.putExtra(USER_GLOBAL, username_global));
                    startActivity(intent);
                }
            });

            Bleecard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoBleecard = new Intent(getActivity(), BleListActivity.class);
                    startActivity(gotoBleecard);
                }
            });

            AddMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ChargeActivity.class);
                    startActivity(intent);
                }
            });

            RequestMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), Compensacion.class);
                    startActivity(intent);
                }
            });

            AddBankAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), RegistrarBanco.class);
                    startActivity(intent);
                }
            });

            BuyToken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CompraToken.class);
                    startActivity(intent);
                }
            });

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();

            Response.Listener<String> response = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String[] resptrm = response.replace("|",";").split(";");
                    String[] country_name = getResources().getStringArray(R.array.pais_select);
                    textViewTrm.setText(String.format(getResources().getString(R.string.dolar_hoy),resptrm[0], country_name[Integer.valueOf(getArguments().getInt(ARG_SECTION_NUMBER))]));

                    SharedPreferences sharedPref = getContext().getSharedPreferences(getResources().getString(R.string.preferencias), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(LoginActivity.TOKEN, resptrm[1])
                          .apply();
                    editor.commit();
                }
            };

            //I need to fix something here!
            String datos = "&pais="+getArguments().getInt(ARG_SECTION_NUMBER);
            //String datos = "&pais="+Integer.valueOf(pais);
            //String datos = data;
            Log.d("DEV -> Main ", " datos -> " + datos + " -> ARG_SECTION_NUMBER "+ARG_SECTION_NUMBER);
            new Request(getContext()).http_get("trm",datos, response);

            //datos = getArguments().getString(ARG_SECTION_DATA);
            String datos_saldos = DataHolder.getData();
            //String datos_saldos = getArguments().getString(ARG_SECTION_DATA);
            Log.d("DEV -> Main ", " datos -> " + datos_saldos + " -> ARG_SECTION_DATA "+ARG_SECTION_DATA);

            response = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.contains("credito")){
                        String[] saldos = response.replace("|",";").split(";");
                        textViewSaldoCreditos.setText(String.format(getResources().getString(R.string.saldo),saldos[1]));
                        textViewSaldoCompensacion.setText(String.format(getResources().getString(R.string.saldo),saldos[3]));
                    }

                    Log.d("Response", response);
                }
            };
            new Request(getContext()).http_get("saldos", datos_saldos, response);
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment_sell extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment_sell() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment_sell newInstance(int sectionNumber) {
            PlaceholderFragment_sell fragment = new PlaceholderFragment_sell();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.sell_fragment, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.textViewTrm);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)+3));
            return rootView;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment_buy extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment_buy() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment_buy newInstance(int sectionNumber) {
            PlaceholderFragment_buy fragment = new PlaceholderFragment_buy();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.sell_fragment, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.textViewTrm);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)+3));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position){
                case 0:
                    return PlaceholderFragment.newInstance(Integer.valueOf(pais), data);
                    //break;

                /*case 1:
                    return PlaceholderFragment_sell.newInstance(1);
                    //break;

                case 2:
                    return PlaceholderFragment_buy.newInstance(1);
                    //break;*/
            }

            return PlaceholderFragment.newInstance(position + 1,"");
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    //return WordUtils.uncapitalize(getString(R.string.mi_billetera));
                    return getString(R.string.mi_billetera);
                /*case 1:
                    return getString(R.string.vender);
                case 2:
                    return getString(R.string.comprar);*/
            }
            return null;
        }
    }

    public void onClose(View view){
        finish();
    }

    public void onResume(){
        super.onResume();
        pais            = DataHolder.getId_country();
        username_global = DataHolder.getUsername();
    }

    public void onBackPressed(){
        Toast.makeText(context, getString(R.string.log_out), Toast.LENGTH_LONG).show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.ui_ble_mini_blee_list_auth_title))
                        .setCancelable(false)
                        .setMessage(getString(R.string.ui_ble_mini_blee_list_auth_body_text))
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;

        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton("Accept", null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }
}