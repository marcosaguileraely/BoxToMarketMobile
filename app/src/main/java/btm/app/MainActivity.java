package btm.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

import com.android.volley.Response;

import org.apache.commons.lang3.text.WordUtils;

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
    private String pais;
    private static String data;
    private static String username_global;

    public static final String USER_GLOBAL = "USERNAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_toolbar);
        getSupportActionBar().setTitle("");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        pais            = getIntent().getStringArrayExtra(LoginActivity.PAIS)[1];
        data            = getIntent().getStringExtra(LoginActivity.DATOS);
        username_global = getIntent().getStringExtra(LoginActivity.USERNAME);
    }

    public void onCC(View view){
        Intent intent = new Intent(this, CardActivity.class);
        this.startActivity(intent);
    }

    public void onRecargar(View view){
        Intent intent = new Intent(this, Recargar.class);
        this.startActivity(intent);
    }

    public void onSolicitar(View view){
        Intent intent = new Intent(this, Compensacion.class);
        this.startActivity(intent);
    }

    public void onRegistrarCuenta(View view){
        Intent intent = new Intent(this, RegistrarBanco.class);
        this.startActivity(intent);
    }

    public void onComprarToken(View view){
        Intent intent = new Intent(this, CompraToken.class);
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
                return true;

            case R.id.action_perfil:
                return true;

            case R.id.action_bp:
                return true;

            case R.id.action_close:
                finish();
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
        private Button Subscriptions;

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView               = inflater.inflate(R.layout.fragment_main, container, false);
            textViewTrm                 = (TextView) rootView.findViewById(R.id.textViewTrm);
            textViewSaldoCompensacion   = (TextView) rootView.findViewById(R.id.textViewSaldoCompensacion);
            textViewSaldoCreditos       = (TextView) rootView.findViewById(R.id.textViewSaldoCredito);
            Subscriptions               = (Button) rootView.findViewById(R.id.my_subscriptions_btn);

            Subscriptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEV -> Main ", username_global);
                    Intent intent = new Intent(getActivity(), SubscriptionsActivity.class);
                    startActivity(intent.putExtra(USER_GLOBAL, username_global));
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

            String datos = "&pais="+getArguments().getInt(ARG_SECTION_NUMBER);
            new Request(getContext()).http_get("trm",datos, response);
            datos = getArguments().getString(ARG_SECTION_DATA);
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
            new Request(getContext()).http_get("saldos",datos, response);

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

                case 1:
                    return PlaceholderFragment_sell.newInstance(1);
                    //break;

                case 2:
                    return PlaceholderFragment_buy.newInstance(1);
                    //break;
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
}