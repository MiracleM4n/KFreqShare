package ca.q0r.kfreqs.app.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import ca.q0r.kfreqs.app.R;
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

public class ProfileUploadTask extends AsyncTask<String, Void, Boolean> {
    private ProgressDialog pDialog;
    private Fragment fragment;
    private JSONObject json;
    private boolean connect;

    public ProfileUploadTask(Fragment frag, JSONObject jObj) {
        fragment = frag;
        json = jObj;
        connect = true;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(fragment.getView().getContext());

        pDialog.setTitle(R.string.title_uploading);
        pDialog.setMessage(fragment.getString(R.string.text_please_wait));
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
            connect = false;
            return false;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (connect) {
            pDialog.setMessage(fragment.getString(R.string.action_upload_complete));
            pDialog.cancel();
        } else {
            pDialog.setMessage(fragment.getString(R.string.text_no_internet));
            pDialog.cancel();
        }

        Toast toast = Toast.makeText(fragment.getView().getContext(), "", Toast.LENGTH_LONG);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(R.string.action_upload_fail);

        if (result) {
            toast.setText(R.string.action_upload_success);
        }

        toast.show();
    }
}
