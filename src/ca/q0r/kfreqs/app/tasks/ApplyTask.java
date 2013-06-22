package ca.q0r.kfreqs.app.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.util.RootUtils;
import ca.q0r.kfreqs.app.util.Utils;

import java.io.BufferedReader;
import java.io.FileReader;

//TODO: Finish Implementing
public class ApplyTask extends AsyncTask<String, Void, Boolean> {
    private ProgressDialog pDialog;
    private Fragment fragment;
    private String profile;

    public ApplyTask(Fragment frag, String pName) {
        fragment = frag;
        profile = pName.split("\\.")[0];
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(fragment.getView().getContext());

        pDialog.setTitle(R.string.title_applying);
        pDialog.setMessage(fragment.getString(R.string.text_please_wait));
        pDialog.setIndeterminate(true);
        pDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String file = Utils.getProfilesPath().getAbsolutePath() + "/.active.profile";
        String comm = "echo " + profile + " > " + file;

        RootUtils.executeCommand(comm);

        String p =  "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            try {
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                p = sb.toString();
            } finally {
                br.close();
            }
        } catch (Exception ignored) { }

        return p.contains(profile);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        pDialog.setMessage(fragment.getString(R.string.action_apply_complete));
        pDialog.cancel();

        Toast toast = Toast.makeText(fragment.getView().getContext(), "", Toast.LENGTH_LONG);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(R.string.action_apply_fail);


        if (result) {
            toast.setText(R.string.action_apply_success);
        }

        toast.show();
    }
}
