package mobile.smartmarket.smartmarket.helpers;

import android.content.Context;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager.NameNotFoundException;
        import android.os.AsyncTask;
        import android.util.Log;

        import com.google.android.gms.gcm.GoogleCloudMessaging;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.StatusLine;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.net.URI;
        import java.net.URISyntaxException;
        import java.net.URLEncoder;
        import java.util.HashMap;
        import java.util.Locale;
        import java.util.Map;

/**
 * Created by omar on 11/6/16.
 */
public class GCMRegister extends AsyncTask<Void, Void, String> {
    private static final String LOG_TAG = Constants.STR_LOG_TAG.concat("GCMRegister");
    private static final boolean IS_DEBBUG = Constants.isDebbud;
    Context ctx = null;
    GoogleCloudMessaging gcm = null;
    int appVersion = 0;
    int idClient = 0;
    String appVersionName = Constants.STR_EMPTY;

    private String SENDER_ID = Constants.STR_MY_ID_PROJECT_NUMBER;
    private String regid = null;

    public GCMRegister(Context ctx, GoogleCloudMessaging gcm, int appVersion, String appVersionName) {
        Log.d(LOG_TAG, "RegisterApp ...");
        this.ctx = ctx;
        this.gcm = gcm;
        this.appVersion = appVersion;
        this.appVersionName = appVersionName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... arg0) {
        if(IS_DEBBUG) Log.d(LOG_TAG, "doInBackground ...");
        StringBuffer msg = new StringBuffer();
        try {
            if (gcm == null) {
                if(IS_DEBBUG) Log.d(LOG_TAG, "gcm null, wait ...");
                gcm = GoogleCloudMessaging.getInstance(ctx);
            }
            regid = gcm.register(SENDER_ID);

            sendRegistrationIdToBackend();

            storeRegistrationId();

            msg.append("Device registered, registration ID = ");
            msg.append(regid);
            msg.append(", idClient: ");
            msg.append(idClient);
        } catch (IOException ex) {
            msg.append("Error :");
            msg.append(ex.getMessage());
        }
        return msg.toString();
    }

    private void storeRegistrationId() {
        if(IS_DEBBUG) Log.d(LOG_TAG, "doInBackground ...");
        PreferenceManagerImp.getInstance(ctx).updatePreferenceValue(Constants.STR_PREFERENCES_01, regid);
        PreferenceManagerImp.getInstance(ctx).updatePreferenceValueInt(Constants.STR_PREFERENCES_02, appVersion);
        PreferenceManagerImp.getInstance(ctx).updatePreferenceValue(Constants.STR_PREFERENCES_03, appVersionName);
        PreferenceManagerImp.getInstance(ctx).updatePreferenceValueInt(Constants.STR_PREFERENCES_04, idClient);
    }

    private void sendRegistrationIdToBackend() {
        String language = Locale.getDefault().toString();
        StringBuffer jsonData = new StringBuffer();
        jsonData.append("{");
        jsonData.append("\"");
        jsonData.append("lan");
        jsonData.append("\"");
        jsonData.append(":");
        jsonData.append("\"");
        jsonData.append(language);
        jsonData.append("\"");
        jsonData.append(",");
        jsonData.append("\"");
        jsonData.append("vappname");
        jsonData.append("\"");
        jsonData.append(":");
        jsonData.append("\"");
        jsonData.append(getAppVersionName());
        jsonData.append("\"");
        jsonData.append(",");
        jsonData.append("\"");
        jsonData.append("vappcode");
        jsonData.append("\"");
        jsonData.append(":");
        jsonData.append(getAppVersionCode());
        jsonData.append("}");
        if(IS_DEBBUG) Log.d(LOG_TAG, jsonData.toString());

        StringBuffer builder = new StringBuffer();
        PackageInfo pakInfo = getPackageInfo();
        StringBuffer urlBuf = new StringBuffer();

        urlBuf.append(Constants.STR_URL_MYSERVER_GCM);
        urlBuf.append("?idmobile=");
        urlBuf.append(regid);
        urlBuf.append("&pname=");
        urlBuf.append(ctx.getPackageName());
        if(pakInfo==null){
            urlBuf.append("&vname=");
            urlBuf.append("none");
        }else{
            urlBuf.append("&vname=");
            urlBuf.append(pakInfo.versionName);
        }
        try {
            urlBuf.append("&jsondata=");
            urlBuf.append(URLEncoder.encode(jsonData.toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            stackTraceToString(e);
        }

        URI url = null;
        try {
            if(IS_DEBBUG) Log.d(LOG_TAG, urlBuf.toString());
            url = new URI(urlBuf.toString());
        } catch (URISyntaxException e) {
            if(IS_DEBBUG) Log.e(LOG_TAG, e.getMessage());
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(url);
        try {
            HttpResponse response = httpclient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                content.close();

                Map<String, String> mapJson = parseJSonURL(builder.toString());
                if(!mapJson.get("v0").equalsIgnoreCase("-1")){
                    idClient = Integer.valueOf(mapJson.get("v0"));
                }

            } else {
                builder.setLength(0);
            }
        } catch (ClientProtocolException e) {
            stackTraceToString(e);
        } catch (IOException e) {
            stackTraceToString(e);
        } catch (Exception e) {
            stackTraceToString(e);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(IS_DEBBUG) Log.d(LOG_TAG, result);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private PackageInfo getPackageInfo() {
        try {
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return packageInfo;
        } catch (NameNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }
    private String getAppVersionName() {
        try {
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
            return Constants.STR_EMPTY;
        }
    }
    private int getAppVersionCode() {
        try {
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
            return 0;
        }
    }

    private Map<String, String> parseJSonURL(String stringChain) {
        Map<String, String> response = null;
        if(stringChain!=null){
            try {
                response = new HashMap<String, String>();
                JSONObject jArray = new JSONObject(stringChain);
                JSONArray keys = jArray.names();
                int largo = keys.length();
                for (int indice=0;indice < largo; indice++) {
                    response.put(keys.getString(indice), jArray.getString(keys.getString(indice)));
                }

            } catch (Exception e) {
                return null;
            }
        }
        return response;
    }

    private void stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage());

        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append(" \n ");
        }

        Log.e(LOG_TAG, sb.toString());
        return;
    }


}
