package ca.q0r.kfreqs.app.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadTask extends AsyncTask<String, Void, Object> {
    private ProgressDialog pDialog;
    private Fragment fragment;
    private String pName;
    private HashMap<String, String> vMap;

    public DownloadTask(Fragment frag, String profile) {
        fragment = frag;
        pName = profile;
        vMap = new HashMap<String, String>();
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(fragment.getView().getContext());

        pDialog.setTitle("Downloading Profile");
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.show();
    }

    @Override
    protected Object doInBackground(String... strings) {
        ConnectivityManager connMgr = (ConnectivityManager)
                fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            try {
                HttpPost httpPostRequest = new HttpPost("http://q0r.ca/abb/download.php");

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("values", pName));

                httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httpPostRequest);
                HttpEntity entity = response.getEntity();

                JSONParser parser = new JSONParser();

                return parser.parse(EntityUtils.toString(entity));
            } catch (Exception ignored) {
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } else {
            pDialog.setMessage("No Internet Connection Found!");
            pDialog.cancel();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object obj) {
        pDialog.setMessage("Profile Download Complete!");
        pDialog.cancel();

        Toast toast = Toast.makeText(fragment.getView().getContext(), "", Toast.LENGTH_LONG);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText("Profile Download Not Successful!");

        if (obj instanceof JSONObject) {
            JSONObject jObj = (JSONObject) obj;

            for (Object kV : jObj.entrySet()) {
                String key = kV.toString().split("=")[0];
                String value = jObj.get(key).toString();

                vMap.put(key, value);
            }

            toast.setText("Profile Download Successful!");
        }

        toast.show();

        WriteTask wTask = new WriteTask(fragment, pName, vMap);
        wTask.execute("");
    }
}