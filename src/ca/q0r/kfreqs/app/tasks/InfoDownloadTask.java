package ca.q0r.kfreqs.app.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.tabs.RemoteTab;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.*;

public class InfoDownloadTask extends AsyncTask<String, Void, Object> {
    private ProgressDialog pDialog;
    private RemoteTab tab;
    private Boolean showToast;
    private ArrayList<String> list;
    private HashMap<String, String> aMap;
    private Boolean connect;

    public InfoDownloadTask(RemoteTab rTab, Boolean toast) {
        tab = rTab;
        list = new ArrayList<String>();
        aMap = new HashMap<String, String>();
        showToast = toast;
        connect = true;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(tab.getView().getContext());

        pDialog.setTitle(R.string.title_info);
        pDialog.setMessage(tab.getString(R.string.text_please_wait));
        pDialog.setIndeterminate(true);
        pDialog.show();
    }

    @Override
    protected Object doInBackground(String... strings) {
        ConnectivityManager connMgr = (ConnectivityManager)
                tab.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            try {
                HttpPost httpPostRequest = new HttpPost("http://q0r.ca/abb/info.php");

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("keys", "?"));

                httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httpPostRequest);
                HttpEntity entity = response.getEntity();

                String json = EntityUtils.toString(entity);

                Gson gson = new Gson();
                Map<?,?> map = gson.fromJson(json, Map.class);

                for (Map.Entry entry : map.entrySet()) {
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();

                    list.add(key);
                    aMap.put(key, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            pDialog.setMessage(tab.getString(R.string.text_no_internet));
        }

        pDialog.cancel();

        Toast toast = Toast.makeText(tab.getView().getContext(), "", Toast.LENGTH_LONG);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(R.string.action_info_fail);

        if (list != null && !list.isEmpty()
                && aMap != null && !aMap.isEmpty()) {
            tab.setList(list);
            tab.setAsvMap(aMap);

            toast.setText(R.string.action_info_success);
        }

        if (showToast) {
            toast.show();
        } else {
            toast.cancel();
        }

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
    }
}