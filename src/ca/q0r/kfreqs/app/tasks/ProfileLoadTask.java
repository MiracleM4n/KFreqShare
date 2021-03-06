package ca.q0r.kfreqs.app.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.tabs.LocalTab;
import ca.q0r.kfreqs.app.util.Utils;

import java.io.File;
import java.util.ArrayList;

public class ProfileLoadTask extends AsyncTask<String, Void, Boolean> {
    private ProgressDialog pDialog;
    private LocalTab tab;
    private Boolean showToast;
    public ArrayList<String> list;

    public ProfileLoadTask(LocalTab lTab, Boolean toast) {
        tab = lTab;
        list = new ArrayList<String>();
        showToast = toast;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(tab.getView().getContext());

        pDialog.setTitle(R.string.title_profiles);
        pDialog.setMessage(tab.getString(R.string.text_please_wait));
        pDialog.setIndeterminate(true);
        pDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        for (File file : Utils.getProfiles()) {
            String name = file.getName();

            if (name.startsWith(".")) {
                continue;
            }

            if (name.endsWith(".profile")) {
                list.add(name);
            }
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        pDialog.setMessage(tab.getString(R.string.action_profile_complete));
        pDialog.cancel();

        Toast toast = Toast.makeText(tab.getView().getContext(), "", Toast.LENGTH_LONG);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(R.string.action_profile_fail);

        if (result) {
            tab.setList(list);

            toast.setText(R.string.action_profile_success);
        }


        if (showToast) {
            toast.show();
        } else {
            toast.cancel();
        }
    }
}
