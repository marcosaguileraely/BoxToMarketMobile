package btm.app.Network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.android.volley.Request.Method.GET;

/**
 * Created by maguilera on 10/12/17.
 */

public class NetActions {
    Context context;

    public NetActions(Context context) {
        this.context = context;
    }

    public void resetPassword(String metodo, String datos, Response.Listener<String> response, final ProgressDialog pd){
        pd.show();
        String url = "https://www.boxtomarket.com/index.php?r=app/" + metodo + "&datos="+this.datoBase64(datos) + "&token=" + this.tkTime();
        Log.d("Url Response", url.toString());

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(GET, url, response,
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pd.dismiss();
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    public String tkTime(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String tk_ = ("aplicacionbtm1"+formatter.format(date));
        Log.d("Time gen ", "data: "+tk_);
        Log.d("Time gen ", "data: "+Base64.encodeToString(tk_.getBytes(), Base64.DEFAULT));
        return Base64.encodeToString(tk_.getBytes(), Base64.DEFAULT);
    }

    public String datoBase64(String datos){
        return Base64.encodeToString(datos.getBytes(), Base64.DEFAULT);
    }

    /*
    * Este metódo obtiene el listado de suscripciones del usuario
    * Interfaz Inicial > Botón Subscripciones BTM Mini
    * ::Listview
    * */
    public void listSubscriptions(String username, Response.Listener<String> response, final ProgressDialog pd){
        pd.show();

        Log.d("DEV -> NetActions ", this.tkTime());

        String url = "https://www.boxtomarket.com/index.php?r=app/listadosuscripciones&username=" + username + "&token=" + this.tkTime();
        Log.d("DEV -> NetActions ", url);

        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest Request = new StringRequest(GET, url,
                response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DEV -> NetActions ", "That didn't work!");
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
        });

        queue.add(Request.setRetryPolicy(new DefaultRetryPolicy(30000,
                                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    /*
    * Este metódo obtiene el listado de suscripciones de la sección
    * Interfaz Inicial > Botón Subscripciones BTM Mini> Comprar Subcripciones
    * ::Gridview -> 3 columnas (Cardview)
    * */
    public void listSubscriptionsPublic(String username, Response.Listener<String> response, final ProgressDialog pd){
        pd.show();

        Log.d("DEV -> NetActions ", this.tkTime());
        //listadoclubes
        String url = "https://www.boxtomarket.com/index.php?r=app/suscripciones&username=" + username + "&token=" + this.tkTime();
        Log.d("DEV -> NetActions ", url);

        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest Request = new StringRequest(GET, url,
                response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DEV -> NetActions ", "That didn't work!");
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(Request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    /*
    * Este metódo obtiene el listado de clubes de la sección
    * Interfaz Inicial > Botón Subscripciones BTM Mini> Comprar Subcripciones
    * ::RecyclerView -> Scroll Horizontal
    * */
    public void listClubsPublic(String username, Response.Listener<String> response, final ProgressDialog pd){
        pd.show();

        Log.d("DEV -> NetActions ", this.tkTime());
        //listadoclubes
        String url = "https://www.boxtomarket.com/index.php?r=app/listadoclubes&username=" + username + "&token=" + this.tkTime();
        Log.d("DEV -> NetActions ", url);

        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest Request = new StringRequest(GET, url,
                response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DEV -> NetActions ", "That didn't work!");
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(Request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    /*
    * Este metódo obtiene el listado de clubes de la sección
    * Interfaz Inicial > Botón Subscripciones BTM Mini> Comprar Subcripciones
    * ::RecyclerView -> Scroll Horizontal
    * */
    public void getDetailsSubscriptions(String username, Response.Listener<String> response, final ProgressDialog pd, int idSub){
        pd.show();

        Log.d("DEV -> NetActions ", this.tkTime());
        //listadoclubes
        String url = "https://www.boxtomarket.com/index.php?r=app/detalleproductossuscripcion&username=" + username
                     +"&token=" + this.tkTime() + "&id=" + idSub;
        Log.d("DEV -> NetActions ", url);

        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest Request = new StringRequest(GET, url,
                response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DEV -> NetActions ", "That didn't work!");
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(Request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    public void http_get_data(String metodo, String datos, Response.Listener<String> response){

        String url = "https://www.boxtomarket.com/index.php?r=app/"
                + metodo
                + datos
                + "&token="+this.tkTime();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                response,
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    /*
    * Este metódo permite la compra de suscripciones
    * Interfaz Inicial > Botón Subscripciones BTM Mini> Comprar Subcripciones > Seleccionar suscripciones > detalles > comprar
    * */
    public void buySubscription(String datos, Response.Listener<String> response, final ProgressDialog pd){

        String url = "https://www.boxtomarket.com/index.php?r=app/comprarsuscripcion&token="+this.tkTime()
                   + "&datos="+this.datoBase64(datos);
        Log.d("->", "->"+url + " ---- "+datos);

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                response,
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    public void getBleeCardData(String datos, Response.Listener<String> response, String ssidsdata,final ProgressDialog pd){

        String url = "https://www.bleecard.com/api/getMachines.do"
                   + "operation=dataMachines"
                   + "&token=tk_test_ZQokik736473jklWgH4olfk2"
                   + "&key=pk_test_6pRNAHGGoqiwFHFKjkj4XMrh"
                   + "&ssids="+ssidsdata;
        Log.d("->", "->"+ url);

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                response,
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context ,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }
}
