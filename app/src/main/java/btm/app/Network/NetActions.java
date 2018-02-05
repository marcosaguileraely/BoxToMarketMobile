package btm.app.Network;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;

import static com.android.volley.Request.Method.GET;

/**
 * Created by maguilera on 10/12/17.
 */

public class NetActions {
    public static final String TAG = "DEV -> Net ";

    Context context;

    public NetActions(Context context) {
        this.context = context;
    }

    public void resetPassword(String metodo, String datos, Response.Listener<String> response, final ProgressDialog pd) {
        pd.show();
        String url = "https://www.boxtomarket.com/index.php?r=app/" + metodo + "&datos=" + this.datoBase64(datos) + "&token=" + this.tkTime();
        Log.d("Url Response", url.toString());

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(GET, url, response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        pd.dismiss();
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    public String tkTime() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String tk_ = ("aplicacionbtm1" + formatter.format(date));
        Log.d("Time gen ", "data: " + tk_);
        Log.d("Time gen ", "data: " + Base64.encodeToString(tk_.getBytes(), Base64.DEFAULT));
        return Base64.encodeToString(tk_.getBytes(), Base64.DEFAULT);
    }

    public String datoBase64(String datos) {
        return Base64.encodeToString(datos.getBytes(), Base64.DEFAULT);
    }

    /*
    * Este metódo obtiene el listado de suscripciones del usuario
    * Interfaz Inicial > Botón Subscripciones BTM Mini
    * ::Listview
    * */
    public void listSubscriptions(String username, Response.Listener<String> response, final ProgressDialog pd) {
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
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void listSubscriptionsPublic(String username, Response.Listener<String> response, final ProgressDialog pd) {
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
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void listClubsPublic(String username, Response.Listener<String> response, final ProgressDialog pd) {
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
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(Request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    /*
     * Using OkHTTP
     * Este metódo obtiene el listado de clubes de la sección
     * Interfaz Inicial > Botón Subscripciones BTM Mini> Comprar Subcripciones
     * ::RecyclerView -> Scroll Horizontal
     **/
    private OkHttpClient client = new OkHttpClient();

    /**
     *
     * @throws IOException
     * @throws NullPointerException
     */
    public String getFullDetails(String username, int idSub) throws IOException, NullPointerException {
        Log.d(TAG, " id: " + idSub);
        String url = "https://www.boxtomarket.com/index.php?format=json&r=app/detalleproductossuscripcion&username="
                   + username
                   + "&token=" + this.tkTime() + "&id=" + idSub;
        Log.d("DEV -> NetActions ", url);

        okhttp3.Request requesthttp = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(requesthttp).execute();
        return response.body().string();
    }

    /**
     *
     * @throws IOException
     * @throws NullPointerException
     */
    public String getCardList(String datos) throws IOException, NullPointerException {
        Log.d(TAG, " Datos card list: " + datos);
        String url = "https://www.boxtomarket.com/index.php?r=app/menutarjetas"
                   + "&token=" + this.tkTime()
                   + "&username=" + datos;
        Log.d("DEV -> NetActions ", url);

        okhttp3.Request requesthttp = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(requesthttp).execute();
        return response.body().string();
    }

    /**
     *
     * @throws IOException
     * @throws NullPointerException
     */
    public String requestCompensation(String datos) throws IOException, NullPointerException {
        Log.d(TAG, " Datos card list: " + datos);

        String url = "https://www.boxtomarket.com/index.php?r=app/solicitarcompensacion"
                + "&token=" + this.tkTime()
                + "&datos=" + this.datoBase64(datos);
        Log.d("DEV -> NetActions ", url);

        okhttp3.Request requesthttp = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(requesthttp).execute();
        return response.body().string();
    }

    public void http_get_data(String metodo, String datos, Response.Listener<String> response) {

        String url = "https://www.boxtomarket.com/index.php?r=app/"
                + metodo
                + datos
                + "&token=" + this.tkTime();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    /**
     *
     * @throws IOException
     * @throws NullPointerException
     */
    public String changePassword(String datos) throws IOException, NullPointerException {
        Log.d(TAG, " id: " + datos);
        String url = "https://www.boxtomarket.com/index.php?r=app/cambiarclave"
                + "&token=" + this.tkTime() + "&datos=" + this.datoBase64(datos);
        Log.d("DEV -> NetActions ", url);

        okhttp3.Request requesthttp = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(requesthttp).execute();
        return response.body().string();
    }

    /**
     *
     * @throws IOException
     * @throws NullPointerException
     */
    public String chargeMoneyToMyWallet(String datos) throws IOException, NullPointerException {
        Log.d(TAG, " id: " + datos);
        String url = "https://www.boxtomarket.com/index.php?r=app/recargar"
                   + "&token=" + this.tkTime() + "&datos=" + this.datoBase64(datos);
        Log.d("DEV -> NetActions ", url);

        okhttp3.Request requesthttp = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(requesthttp).execute();
        return response.body().string();
    }

    /*
    * Este metódo permite la Transfer Money to another user
    * Interfaz Inicial > Transfer
    * */
    /**
     *
     * @throws IOException
     * @throws NullPointerException
     */
    public String transferMoneyToUser(String datos) throws IOException, NullPointerException {
        Log.d(TAG, " id: " + datos);
        String url = "https://www.boxtomarket.com/index.php?r=app/transferir"
                   + "&token=" + this.tkTime() + "&datos=" + this.datoBase64(datos);
        Log.d("DEV -> NetActions ", url);

        okhttp3.Request requesthttp = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(requesthttp).execute();
        return response.body().string();
    }

    /*
    * Este metódo permite la Transfer de Compensación a Mi Billetera
    * Interfaz Inicial > Transfer -> Transferor de Compensación a Mi Billetera
    * */
    /**
     *
     * @throws IOException
     * @throws NullPointerException
     */
    public String transferCompensation(String datos) throws IOException, NullPointerException {
        Log.d(TAG, " id: " + datos);
        String url = "https://www.boxtomarket.com/index.php?r=app/autotransferir"
                + "&token=" + this.tkTime() + "&datos=" + this.datoBase64(datos);
        Log.d("DEV -> NetActions ", url);

        okhttp3.Request requesthttp = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(requesthttp).execute();
        return response.body().string();
    }

    /*
    * Este metódo permite la compra de tokens
    * Interfaz Inicial > Comprar Token
    * */
    public String buyToken(String datos) throws IOException, NullPointerException {
        Log.d(TAG, " id: " + datos);
        String url = "https://www.boxtomarket.com/index.php?r=app/comprartoken"
                + "&token=" + this.tkTime() + "&datos=" + this.datoBase64(datos);
        Log.d("DEV -> NetActions ", url);

        okhttp3.Request requesthttp = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(requesthttp).execute();
        return response.body().string();
    }

    /*
    * Este metódo permite la compra de suscripciones
    * Interfaz Inicial > Botón Subscripciones BTM Mini> Comprar Subcripciones > Seleccionar suscripciones > detalles > comprar
    * */
    public String buySubscription(String datos) throws IOException, NullPointerException {
        String url = "https://www.boxtomarket.com/index.php?r=app/comprarsuscripcion" +
                     "&token=" + this.tkTime()
                   + "&datos=" + this.datoBase64(datos);
        Log.d("DEV -> NetActions ", url);

        okhttp3.Request requesthttp = new okhttp3.Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(requesthttp).execute();
        return response.body().string();
    }

    /*
    * Este metódo permite obtener los detalles de la blee card entregandole la mac address
    * Interfaz Inicial > Botón Bleecard > Listado de dispositivos Bluethooth
    * */
    public void getBleeCardData(Response.Listener<String> response, final String ssidsdata, final ProgressDialog pd)throws JSONException {

        String url = "https://www.bleecard.com/api/getMachines.do";

        /*
         * Genera un Array de Array de objecto
         * No debe convertirse en String ya que el webservice espera la el ssid así:
         *            [[ { "mac": ""xx:xx:xx:xx:xx } ]]  <-- debe pasarse así
         * en vez de "[[ { "mac": ""xx:xx:xx:xx:xx } ]]" <-- no debe pasarse así
         * notese que al ser String es invalido y debe pasarse en forma de Arraglo y no String
         */

        JSONObject obmacAdd = new JSONObject();
                   JSONArray array     = new JSONArray();
                   JSONArray array2    = new JSONArray();
                   obmacAdd.put("mac", ssidsdata);
                   array.put(obmacAdd);
                   array2.put(array);

        JSONObject jsonBody = new JSONObject();
                   jsonBody.put("operation", "dataMachines");
                   jsonBody.put("key", "pk_test_6pRNAHGGoqiwFHFKjkj4XMrh");
                   jsonBody.put("token", "tk_test_ZQokik736473jklWgH4olfk2");
                   jsonBody.put("ssids", array2);

        final String mRequestBody = jsonBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    /*
    * Este metódo permite obtener el código RSA enviando el id del Bleecard y el precio a pagar
    * Interfaz Inicial > Botón Bleecard > Listado de dispositivos Bluethooth > detalles del
    * bleecard seleccionado
    * */
    public void getRsa(Response.Listener<String> response, final String id_blee, final String price_blee, final ProgressDialog pd)throws JSONException {

        String url = "https://www.bleecard.com/api/getRsa.do";

        JSONObject jsonBody = new JSONObject();
                   jsonBody.put("operation", "dataRsa");
                   jsonBody.put("key", "pk_test_6pRNAHGGoqiwFHFKjkj4XMrh");
                   jsonBody.put("token", "tk_test_ZQokik736473jklWgH4olfk2");
                   jsonBody.put("id", id_blee);
                   jsonBody.put("price", price_blee);
                   jsonBody.put("info", "enviado por @marcode_ey");

        final String mRequestBody = jsonBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }
}
