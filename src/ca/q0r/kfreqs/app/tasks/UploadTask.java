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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UploadTask extends AsyncTask<String, Void, Boolean> {
    private ProgressDialog pDialog;
    private Fragment fragment;
    private JSONObject json;

    public UploadTask(Fragment frag, JSONObject jObj) {
        fragment = frag;
        json = jObj;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(fragment.getView().getContext());

        pDialog.setTitle("Uploading");
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        ConnectivityManager connMgr = (ConnectivityManager)
                fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                String id = json.getString("id");

                if (id == null || id.isEmpty()) {

                    if (pDialog != null) {
                        pDialog.setMessage("Cannot find Account Email!");
                        pDialog.cancel();
                    }
                }
            } catch (Exception ignored) { }

            DefaultHttpClient httpclient = new DefaultHttpClient();

            try {
                HttpPost httpPostRequest = new HttpPost("http://q0r.ca/abb/upload.php");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("data", json.toString()));

                httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httpPostRequest);
                HttpEntity entity = response.getEntity();

                return Boolean.valueOf(EntityUtils.toString(entity));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } else {
            if (pDialog != null) {
                pDialog.setMessage("No Internet Connection Found!");
                pDialog.cancel();
            }

            return false;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (pDialog != null) {
            pDialog.setMessage("Upload Complete!");
            pDialog.cancel();
        }

        Toast toast = Toast.makeText(fragment.getView().getContext(), "", Toast.LENGTH_LONG);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText("Upload Not Successful!");

        if (result) {
            toast.setText("Upload Successful!");
        }

        toast.show();
    }
}
