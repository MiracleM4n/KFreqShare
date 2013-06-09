package ca.q0r.kfreqs.app.tabs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.prop.LinkedProperties;
import ca.q0r.kfreqs.app.util.Utils;
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

import java.io.*;
import java.util.*;

public class DownloadTab extends Fragment implements View.OnClickListener {
    public String pName = "";
    ArrayList<String> list;
    HashMap<String, String> iMap;
    HashMap<String, String> map;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = new ArrayList<String>();
        iMap = new HashMap<String, String>();
        map = new HashMap<String, String>();

        LinearLayout uploadDecline = (LinearLayout) getView().findViewById(R.id.layout_dc);
        uploadDecline.setVisibility(LinearLayout.GONE);

        final Button button = (Button) uploadDecline.findViewById(R.id.button_remote_download);
        button.setOnClickListener(this);

        final Button button2 = (Button) uploadDecline.findViewById(R.id.button_remote_cancel);
        button2.setOnClickListener(this);

        ListView lv = (ListView) getView().findViewById(R.id.view_remote_profiles);

        TextView vw = new TextView(getView().getContext());

        lv.addHeaderView(vw);

        InfoTask task = new InfoTask();

        task.execute("");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getView().getContext(), android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                LinearLayout uploadDecline = (LinearLayout) getView().findViewById(R.id.layout_dc);

                TextView tv = (TextView) view;

                pName = tv.getText().toString();

                String asv = iMap.get(pName);

                if (asv != null) {
                    Toast toast = Toast.makeText(getView().getContext(), "", Toast.LENGTH_SHORT);

                    toast.setText("ASV: " + asv);
                    toast.show();
                }

                uploadDecline.setVisibility(LinearLayout.VISIBLE);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.download_tab, container, false);
    }

    /*----------------------------
    ---------  onClick  ---------
    ----------------------------*/

    @Override
    public void onClick(View v) {
        LinearLayout uploadDecline = (LinearLayout) getView().findViewById(R.id.layout_dc);

        int id = v.getId();

        if (id == R.id.button_remote_download) {
            uploadDecline.setVisibility(LinearLayout.GONE);

            String asv = iMap.get(pName);

            if (asv != null) {
                createConfirm(asv).show();
            }
        } else if (id == R.id.button_remote_cancel) {
            uploadDecline.setVisibility(LinearLayout.GONE);
        }
    }

    /*----------------------------
    ----------  Dialogs  ----------
    ----------------------------*/

    private AlertDialog createConfirm(String asv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());

        String st = getString(R.string.download_confirm).replace("@profile_id", pName);
        st = st.replace("@asv", asv);

        builder.setMessage(st)
                .setPositiveButton(R.string.button_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DownloadTask dTask = new DownloadTask();
                        dTask.execute("");
                    }})
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }});

        return builder.create();
    }

    /*----------------------------
    -----------  Tasks  -----------
    ----------------------------*/

    private class InfoTask extends AsyncTask<String, Void, Object> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getView().getContext());

            pDialog.setTitle("Downloading Info");
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(String... strings) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                DefaultHttpClient httpclient = new DefaultHttpClient();

                try {
                    HttpPost httpPostRequest = new HttpPost("http://q0r.ca/abb/download.php");

                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                    nameValuePairs.add(new BasicNameValuePair("keys", "?"));

                    httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpclient.execute(httpPostRequest);

                    HttpEntity entity = response.getEntity();

                    JSONParser parser = new JSONParser();

                    return parser.parse(EntityUtils.toString(entity));
                } catch (Exception e) {
                    e.printStackTrace();
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
            pDialog.setMessage("Info Download Complete!");
            pDialog.cancel();

            Toast toast = Toast.makeText(getView().getContext(), "", Toast.LENGTH_LONG);

            toast.setDuration(Toast.LENGTH_LONG);

            toast.setText("Info Download Not Successful!");

            if (obj instanceof JSONObject) {
                JSONObject jObj = (JSONObject) obj;

                for (Object kV : jObj.entrySet()) {
                    String key = kV.toString().split("=")[0];
                    String value = jObj.get(key).toString();

                    list.add(key);
                    iMap.put(key, value);
                }

                toast.setText("Info Download Successful!");
            }

            toast.show();

            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, Object> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getView().getContext());

            pDialog.setTitle("Downloading Profile");
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(String... strings) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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
                } catch (Exception e) {
                    e.printStackTrace();
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

            Toast toast = Toast.makeText(getView().getContext(), "", Toast.LENGTH_LONG);

            toast.setDuration(Toast.LENGTH_LONG);

            toast.setText("Profile Download Not Successful!");

            if (obj instanceof JSONObject) {
                JSONObject jObj = (JSONObject) obj;

                for (Object kV : jObj.entrySet()) {
                    String key = kV.toString().split("=")[0];
                    String value = jObj.get(key).toString();

                    map.put(key, value);
                }

                toast.setText("Profile Download Successful!");
            }

            toast.show();

            WriteTask wTask = new WriteTask();
            wTask.execute("");
        }
    }

    private class WriteTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getView().getContext());

            pDialog.setTitle("Writing Profile");
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String profile = pName;
            File prf = new File(Utils.getProfilesPath(), profile + "_r.profile");
            Boolean result = false;

            LinkedProperties wProp = new LinkedProperties();

            if (!prf.exists()) {
                try {
                    if (!prf.createNewFile()
                            || !prf.setReadable(true, false)
                            || !prf.setWritable(true, false)
                            || !prf.setExecutable(true, false)) {
                        result = false;
                    }

                    InputStream inStream = new FileInputStream(Utils.getDefaultProfile());
                    OutputStream outStream = new FileOutputStream(prf);

                    byte[] buffer = new byte[1024];

                    int length;
                    while ((length = inStream.read(buffer)) > 0){
                        outStream.write(buffer, 0, length);
                    }

                    inStream.close();
                    outStream.close();
                } catch (Exception ignored) { }
            }

            try {
                wProp.load(new FileInputStream(prf));

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (key.startsWith("slice")) {
                        key = "arm_slice_" + key.replace("slice", "") + "_volt";

                        Integer i = Integer.parseInt(value) / 1000;

                        wProp.put(key, i.toString());
                    } else if (key.contains("0")) {
                        key = "CPU_VOLT_" + key;

                        wProp.put(key, value);
                    }

                }

                wProp.store(new FileOutputStream(prf), null);

                result = true;
            } catch (Exception ignored) { }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.setMessage("Profile Writing Complete!");
            pDialog.cancel();
            pDialog.dismiss();

            Toast toast = Toast.makeText(getView().getContext(), "", Toast.LENGTH_LONG);

            toast.setDuration(Toast.LENGTH_LONG);

            toast.setText("Profile Writing Not Successful!");

            if (result) {
                toast.setText("Profile Writing Successful!");
            }

            toast.show();
        }
    }
}
