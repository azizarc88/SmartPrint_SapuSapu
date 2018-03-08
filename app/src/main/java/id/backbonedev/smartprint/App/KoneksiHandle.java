package id.backbonedev.smartprint.App;

import android.app.Activity;
import android.app.PendingIntent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.backbonedev.smartprint.R;

/**
 * Created by Aziz Nur Ariffianto on 0006, 06 Jun 2017.
 */

public class KoneksiHandle
{
    private static String TAG = "####KoneksiHandle####";
    private static int TIME_OUT = 20000;
    private static boolean stop = false;

    public static boolean isStop()
    {
        return stop;
    }

    public static void setStop(boolean stop)
    {
        KoneksiHandle.stop = stop;
    }

    public interface OnResponObjekSukses
    {
        void onPanggil(JSONObject object);
    }

    public interface OnResponObjekGagal
    {
        void onPanggil(JSONObject object);
    }

    public interface OnResponArraySukses
    {
        void onPanggil(JSONArray array, JSONObject respon);
    }

    public interface OnResponArrayGagal
    {
        void onPanggil(JSONArray array, JSONObject respon);
    }

    public interface OnSemuaRespon {
        void onPanggil(JSONObject object);
    }

    public void AmbilObjek(final Activity activity, final boolean silent, final List<String[]> data, final String link, final OnResponObjekSukses listenersukses)
    {
        

        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + CoreApp.server + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CoreApp.server + link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;
                            JSONObject js = new JSONObject(response);

                            if (js.getString("hasil").equalsIgnoreCase("sukses"))
                            {
                                listenersukses.onPanggil(js);
                                sukses = true;
                            }
                            else
                            {
                                if (silent && !stop)
                                {
                                    AmbilObjek(activity, silent, data, link, listenersukses);
                                }
                            }

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + response);
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + response + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilObjek(activity, silent, data, link, listenersukses);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 11346876";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (CoreApp.isdebug)
                        {
                            Log.d(TAG, "RESPON ErrorListener " + link + " : " + error.getMessage());
                        }

                        if (!silent)
                        {
                            String pesan = "";

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            CoreApp.Error(activity, activity.getResources().getString(R.string.app_name), pesan, null);
                        }
                        else if (!stop)
                        {
                            AmbilObjek(activity, silent, data, link, listenersukses);
                        }
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, link);
        Log.d(TAG, "%%%SELESAI KONEKSI " + CoreApp.server + link);
    }

    public void AmbilObjekCache(final Activity activity, final boolean silent, final List<String[]> data, final String link, final OnResponObjekSukses listenersukses)
    {
        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + CoreApp.server + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CoreApp.server + link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;
                            JSONObject js = new JSONObject(response);

                            if (js.getString("hasil").equalsIgnoreCase("sukses"))
                            {
                                listenersukses.onPanggil(js);
                                sukses = true;
                            }
                            else
                            {
                                if (silent && !stop)
                                {
                                    AmbilObjek(activity, silent, data, link, listenersukses);
                                }
                            }

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + response);
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + response + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilObjek(activity, silent, data, link, listenersukses);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 11346876";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (CoreApp.isdebug)
                        {
                            Log.d(TAG, "RESPON ErrorListener " + link + " : " + error.getMessage());
                        }

                        if (!silent)
                        {
                            String pesan = "";

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            CoreApp.Error(activity, activity.getResources().getString(R.string.app_name), pesan, null);
                        }
                        else if (!stop)
                        {
                            AmbilObjek(activity, silent, data, link, listenersukses);
                        }
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().invalidate(link, true);
    }

    public void AmbilObjek(final Activity activity, final boolean silent, final List<String[]> data, final String link, final OnResponObjekSukses listenersukses, final OnResponObjekGagal listenergagal)
    {
        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + CoreApp.server + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CoreApp.server + link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;

                            JSONObject js = new JSONObject(response);

                            if (js.getString("hasil").equalsIgnoreCase("sukses"))
                            {
                                listenersukses.onPanggil(js);
                                sukses = true;
                            }
                            else
                            {
                                listenergagal.onPanggil(js);
                            }

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + response);
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + response + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilObjek(activity, silent, data, link, listenersukses, listenergagal);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 11346876";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                                try
                                {
                                    listenergagal.onPanggil((new JSONObject()).put("hasil", "GAGAL").put("pesan", pesan));
                                } catch (JSONException e1)
                                {
                                    listenergagal.onPanggil(null);
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (!silent)
                        {
                            String pesan = "";

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            try
                            {
                                listenergagal.onPanggil((new JSONObject()).put("hasil", "GAGAL").put("pesan", pesan));
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else if (!stop)
                        {
                            AmbilObjek(activity, silent, data, link, listenersukses, listenergagal);
                        }
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, link);
        Log.d(TAG, "%%%SELESAI KONEKSI " + CoreApp.server + link);
    }

    public void AmbilObjekLuar(final Activity activity, final boolean silent, final List<String[]> data, final String link, final OnResponObjekSukses listenersukses, final OnResponObjekGagal listenergagal)
    {
        VolleyLog.DEBUG = true;

        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;

                            JSONObject js = new JSONObject(response);
                            listenersukses.onPanggil(js);
                            sukses = true;

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + response);
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + response + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilObjek(activity, silent, data, link, listenersukses, listenergagal);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 11346876";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                                try
                                {
                                    listenergagal.onPanggil((new JSONObject()).put("hasil", "GAGAL").put("pesan", pesan));
                                } catch (JSONException e1)
                                {
                                    listenergagal.onPanggil(null);
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        try
                        {
                            listenergagal.onPanggil((new JSONObject()).put("hasil", "GAGAL").put("pesan", error.getMessage()));
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, link);
        Log.d(TAG, "%%%SELESAI KONEKSI " + link);
    }

    public void AmbilObjekLuarArray(final Activity activity, final boolean silent, final List<String[]> data, final String link, final OnResponArraySukses listenersukses, final OnResponArrayGagal listenergagal)
    {
        VolleyLog.DEBUG = true;

        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;

                            JSONArray js = new JSONArray(response);
                            listenersukses.onPanggil(js, new JSONObject(response));
                            sukses = true;

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + response);
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + response + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilObjekLuarArray(activity, silent, data, link, listenersukses, listenergagal);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 11346876";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                                try
                                {
                                    listenergagal.onPanggil(null, new JSONObject(response));
                                } catch (JSONException e1)
                                {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        NetworkResponse response = error.networkResponse;
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers));
                            // Now you can use any deserializer to make sense of data
                            Log.d(TAG, "onErrorResponse: PESANPESAN" + res.toString());
                            JSONObject obj = new JSONObject(res);
                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        } catch (JSONException e2) {
                            // returned data is not JSONObject?
                            e2.printStackTrace();
                        }

                        if (!silent)
                        {
                            String pesan = "";

                            Log.d(TAG, "onErrorResponse: PESAN" + error.getMessage());

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            CoreApp.Error(activity, activity.getResources().getString(R.string.app_name), pesan, null);
                            listenergagal.onPanggil(null, new JSONObject());
                        }
                        else if (!stop)
                        {
                            AmbilObjekLuarArray(activity, silent, data, link, listenersukses, listenergagal);
                        }
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(stringRequest, link);
        Log.d(TAG, "%%%SELESAI KONEKSI " + link);
    }

    public void AmbilObjekLuarToken(final Activity activity, final boolean silent, final String token, final List<String[]> data, final String link, final OnResponObjekSukses listenersukses, final OnResponObjekGagal listenergagal)
    {
        VolleyLog.DEBUG = true;
        Map<String,String> params = new HashMap<>();

        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
                params.put(item[0], item[1]);
            }
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, link, new JSONObject(params), new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject object)
                {
                    listenersukses.onPanggil(object);

                    if (CoreApp.isdebug)
                    {
                        Log.d(TAG, "RESPON " + link + " : " + object.toString());
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    NetworkResponse response = error.networkResponse;
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                        // Now you can use any deserializer to make sense of data
                        Log.d(TAG, "onErrorResponse: PESANPESAN" + res.toString());
                        JSONObject obj = new JSONObject(res);

                        try
                        {
                            listenergagal.onPanggil((new JSONObject()).put("hasil", "GAGAL").put("pesan", obj.getString("message")));
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + token);
                headers.put("User-agent", "My useragent");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(req, link);
        Log.d(TAG, "%%%SELESAI KONEKSI " + CoreApp.server + link);
    }

    public void AmbilObjekCrypt(final Activity activity, final boolean silent, final List<String[]> data, final String link, final OnResponObjekSukses listenersukses, final OnResponObjekGagal listenergagal)
    {
        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + CoreApp.server + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CoreApp.server + link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;

                            JSONObject js = new JSONObject(Crypt.Decrypt(response));

                            if (js.getString("hasil").equalsIgnoreCase("sukses"))
                            {
                                listenersukses.onPanggil(js);
                                sukses = true;
                            }
                            else
                            {
                                if (silent && !stop)
                                {
                                    AmbilObjekCrypt(activity, silent, data, link, listenersukses, listenergagal);
                                }
                                else
                                {
                                    listenergagal.onPanggil(js);
                                }
                            }

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + Crypt.Decrypt(response));
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + Crypt.Decrypt(response) + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilObjekCrypt(activity, silent, data, link, listenersukses, listenergagal);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 11346876";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                                try
                                {
                                    listenergagal.onPanggil((new JSONObject()).put("hasil", "GAGAL").put("pesan", pesan));
                                } catch (JSONException e1)
                                {
                                    listenergagal.onPanggil(null);
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (!silent)
                        {
                            String pesan = "";

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            CoreApp.Error(activity, activity.getResources().getString(R.string.app_name), pesan, null);
                        }
                        else if (!stop)
                        {
                            Log.d(TAG, "onErrorResponse: " + error.toString());
                            AmbilObjekCrypt(activity, silent, data, link, listenersukses, listenergagal);
                        }

                        listenergagal.onPanggil(null);
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, link);

        Log.d(TAG, "%%%SELESAI KONEKSI " + CoreApp.server + link);
    }

    public void AmbilObjekCryptArray(final Activity activity, final boolean silent, final List<String[]> data, final String link, final OnResponArraySukses listenersukses, final OnResponArrayGagal listenergagal)
    {
        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + CoreApp.server + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CoreApp.server + link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;

                            JSONObject js = new JSONObject(Crypt.Decrypt(response));
                            JSONArray array = new JSONArray(js.getString("data"));

                            if (js.getString("hasil").equalsIgnoreCase("sukses"))
                            {
                                listenersukses.onPanggil(array, js);
                                sukses = true;
                            }
                            else
                            {
                                if (silent && !stop)
                                {
                                    AmbilObjekCryptArray(activity, silent, data, link, listenersukses, listenergagal);
                                }
                                else
                                {
                                    listenergagal.onPanggil(array, js);
                                }
                            }

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + Crypt.Decrypt(response));
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + Crypt.Decrypt(response) + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilObjekCryptArray(activity, silent, data, link, listenersukses, listenergagal);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 11346876";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                                try
                                {
                                    listenergagal.onPanggil(null, (new JSONObject()).put("hasil", "GAGAL").put("pesan", pesan));
                                } catch (JSONException e1)
                                {
                                    listenergagal.onPanggil(null, null);
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (!silent)
                        {
                            String pesan = "";

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            CoreApp.Error(activity, activity.getResources().getString(R.string.app_name), pesan, null);
                        }
                        else if (!stop)
                        {
                            Log.d(TAG, "onErrorResponse: " + error.toString());
                            AmbilObjekCryptArray(activity, silent, data, link, listenersukses, listenergagal);
                        }

                        listenergagal.onPanggil(null, null);
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, link);

        Log.d(TAG, "%%%SELESAI KONEKSI " + CoreApp.server + link);
    }

    public void AmbilObjek(final Activity activity, final boolean silent, final List<String[]> data, final String link, final OnResponObjekSukses listenersukses, final OnResponObjekGagal listenergagal, final OnSemuaRespon listenersemua)
    {
        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + CoreApp.server + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CoreApp.server + link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;

                            JSONObject js = new JSONObject(response);

                            if (js.getString("hasil").equalsIgnoreCase("sukses"))
                            {
                                listenersukses.onPanggil(js);
                                sukses = true;
                            }
                            else
                            {
                                if (silent && !stop)
                                {
                                    AmbilObjek(activity, silent, data, link, listenersukses, listenergagal, listenersemua);
                                }
                                else
                                {
                                    listenergagal.onPanggil(js);
                                }
                            }

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + response);
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + response + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilObjek(activity, silent, data, link, listenersukses, listenergagal, listenersemua);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 11346876";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                                listenergagal.onPanggil(null);
                            }

                            listenersemua.onPanggil(null);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (!silent)
                        {
                            String pesan = "";

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            CoreApp.Error(activity, activity.getResources().getString(R.string.app_name), pesan, null);
                        }
                        else if (!stop)
                        {
                            AmbilObjek(activity, silent, data, link, listenersukses, listenergagal, listenersemua);
                        }

                        listenersemua.onPanggil(null);
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, link);
        Log.d(TAG, "%%%SELESAI KONEKSI " + CoreApp.server + link);
    }

    public void AmbilArray(final Activity activity, final boolean silent, final List<String[]> data, final String namadata, final String link, final OnResponArraySukses listenersukses)
    {
        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + CoreApp.server + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CoreApp.server + link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;

                            JSONObject js = new JSONObject(response);
                            JSONArray array = new JSONArray(js.getString(namadata));

                            if (js.getString("hasil").equalsIgnoreCase("sukses"))
                            {
                                listenersukses.onPanggil(array, js);
                                sukses = true;
                            }
                            else
                            {
                                if (silent && !stop)
                                {
                                    AmbilArray(activity, silent, data, namadata, link, listenersukses);
                                }
                                else
                                {
                                    listenersukses.onPanggil(array, js);
                                }
                            }

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + response);
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + response + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilArray(activity, silent, data, namadata, link, listenersukses);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 64561235";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (!silent)
                        {
                            String pesan = "";

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            CoreApp.Error(activity, activity.getResources().getString(R.string.app_name), pesan, null);
                        }
                        else if (!stop)
                        {
                            AmbilArray(activity, silent, data, namadata, link, listenersukses);
                        }
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().invalidate(link, true);
    }

    public void AmbilArray(final Activity activity, final boolean silent, final List<String[]> data, final String namadata, final String link, final OnResponArraySukses listenersukses, final OnResponArrayGagal listenergagal)
    {
        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + CoreApp.server + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CoreApp.server + link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;

                            JSONObject js = new JSONObject(response);
                            JSONArray array = new JSONArray(js.getString(namadata));

                            if (js.getString("hasil").equalsIgnoreCase("sukses"))
                            {
                                listenersukses.onPanggil(array, js);
                                sukses = true;
                            }
                            else
                            {
                                listenersukses.onPanggil(array, js);
                            }

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + response);
                            }
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + response + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilArray(activity, silent, data, namadata, link, listenersukses, listenergagal);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 64561235";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                                listenergagal.onPanggil(null, null);
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (!silent)
                        {
                            String pesan = "";

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            CoreApp.Error(activity, activity.getResources().getString(R.string.app_name), pesan, null);
                        }
                        else if (!stop)
                        {
                            AmbilArray(activity, silent, data, namadata, link, listenersukses, listenergagal);
                        }
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().invalidate(link, true);
    }

    public void AmbilArray(final Activity activity, final boolean silent, final List<String[]> data, final String namadata, final String link, final OnResponArraySukses listenersukses, final OnResponArrayGagal listenergagal, final OnSemuaRespon listenersemua)
    {
        if (CoreApp.isdebug)
        {
            Log.d(TAG, "###MULAI KONEKSI " + CoreApp.server + link);
        }

        if (data != null)
        {
            for (String[] item : data)
            {
                Log.d(TAG, "DATA " + item[0] + "=" + item[1]);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CoreApp.server + link,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            boolean sukses = false;

                            JSONObject js = new JSONObject(response);
                            JSONArray array = new JSONArray(js.getString(namadata));

                            if (js.getString("hasil").equalsIgnoreCase("sukses"))
                            {
                                listenersukses.onPanggil(array, js);
                                sukses = true;
                            }
                            else
                            {
                                if (silent && !stop)
                                {
                                    AmbilArray(activity, silent, data, namadata, link, listenersukses, listenergagal, listenersemua);
                                }
                                else
                                {
                                    listenersukses.onPanggil(array, js);
                                }
                            }

                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON " + (sukses ? "SUKSES " : "GAGAL ") + link + " : " + response);
                            }

                            listenersemua.onPanggil(null);
                        }
                        catch (JSONException e)
                        {
                            if (CoreApp.isdebug)
                            {
                                Log.d(TAG, "RESPON CATCH " + link + " : " + response + " [ERROR] " + e.toString());
                            }

                            if (silent && !stop)
                            {
                                AmbilArray(activity, silent, data, namadata, link, listenersukses, listenergagal, listenersemua);
                            }
                            else
                            {
                                String pesan = "";

                                if (e.getMessage().toLowerCase().contains("cannot be converted"))
                                {
                                    pesan = "silakan coba lagi nanti. Kode: 64561235";
                                }

                                Toast.makeText(activity, "Terjadi kesalahan, " + pesan, Toast.LENGTH_LONG).show();
                                listenergagal.onPanggil(null, null);
                            }

                            listenersemua.onPanggil(null);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (!silent)
                        {
                            String pesan = "";

                            if (error instanceof NetworkError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46114587";
                            } else if (error instanceof ServerError) {
                                pesan = "Tidak dapat terhubung ke server, harap coba beberapa saat lagi. Kode: 11345861";
                            } else if (error instanceof AuthFailureError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 46977816";
                            } else if (error instanceof ParseError) {
                                pesan = "Terjadi kesalahan, silakan coba lagi nanti. Kode: 46335897";
                            } else if (error instanceof NoConnectionError) {
                                pesan = "Tidak dapat terhubung ke internet, periksa koneksi Anda. Kode: 31126679";
                            } else if (error instanceof TimeoutError) {
                                pesan = "Koneksi tidak terhubung dalam waktu lama, periksa koneksi internet Anda. Kode: 99886379";
                            }

                            CoreApp.Error(activity, activity.getResources().getString(R.string.app_name), pesan, null);
                        }
                        else if (!stop)
                        {
                            AmbilArray(activity, silent, data, namadata, link, listenersukses, listenergagal, listenersemua);
                        }

                        listenersemua.onPanggil(null);
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (data != null)
                {
                    for (String[] item : data)
                    {
                        params.put(item[0], item[1]);
                    }
                }
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().invalidate(link, true);
    }
}
