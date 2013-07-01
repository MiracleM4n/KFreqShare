package ca.q0r.kfreqs.app.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import ca.q0r.kfreqs.app.R;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileDownloadTask extends AsyncTask<String, Void, Object> {
    private ProgressDialog pDialog;
    private Fragment fragment;
    private String pName;
    private HashMap<String, String> vMap;
    private Boolean connect;

    public ProfileDownloadTask(Fragment frag, String profile) {
        fragment = frag;
        pName = profile;
        vMap = new HashMap<String, String>();
        connect = true;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(fragment.getView().getContext());

        pDialog.setTitle(R.string.title_downloading);
        pDialog.setMessage(fragment.getString(R.string.text_please_wait));
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

                Gson gson = new Gson();
                HashMap<?,?> map = gson.fromJson(EntityUtils.toString(entity), HashMap.class);

                for (Map.Entry entry : map.entrySet()) {
                    vMap.put(entry.getKey().toString(), entry.getValue().toString());
                }
            } catch (Exception ignored) {
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } else {
            connect = false;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object obj) {
        if (!connect) {
            pDialog.setMessage(fragment.getString(R.string.text_no_internet));
            pDialog.cancel();
        }

        Toast toast = Toast.makeText(fragment.getView().getContext(), "", Toast.LENGTH_LONG);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(R.string.action_download_fail);

        if (vMap != null && !vMap.isEmpty()) {
            toast.setText(R.string.action_download_success);
        }

        toast.show();

        ProfileWriteTask wTask = new ProfileWriteTask(fragment, pName, vMap);
        wTask.execute("");
    }
}