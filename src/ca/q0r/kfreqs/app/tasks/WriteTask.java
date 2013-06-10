package ca.q0r.kfreqs.app.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import ca.q0r.kfreqs.app.prop.LinkedProperties;
import ca.q0r.kfreqs.app.util.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class WriteTask extends AsyncTask<String, Void, Boolean> {
    private ProgressDialog pDialog;
    private Fragment fragment;
    private String pName;
    private HashMap<String, String> map;

    public WriteTask(Fragment frag, String profile, HashMap<String, String> pMap) {
        fragment = frag;
        pName = profile;
        map = pMap;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(fragment.getView().getContext());

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

        Toast toast = Toast.makeText(fragment.getView().getContext(), "", Toast.LENGTH_LONG);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText("Profile Writing Not Successful!");

        if (result) {
            toast.setText("Profile Writing Successful!");
        }

        toast.show();
    }
}
