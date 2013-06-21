package ca.q0r.kfreqs.app.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.tasks.UploadTask;
import ca.q0r.kfreqs.app.util.Utils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

public class UploadInfoDialog {
    private Fragment fragment;
    private String profile;

    public UploadInfoDialog(Fragment frag, String pName) {
        fragment = frag;
        profile = pName;
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getView().getContext());

        builder.setTitle(R.string.email_usage);

        LayoutInflater inflater = LayoutInflater.from(fragment.getView().getContext());

        final View input = inflater.inflate(R.layout.dialog_confirm, null);

        builder.setView(input)
                .setPositiveButton(R.string.title_upload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText eName = (EditText) input.findViewById(R.id.dialog_name);
                        EditText eAsv = (EditText) input.findViewById(R.id.dialog_asv);

                        Editable name = eName.getText();
                        Editable asv = eAsv.getText();

                        Toast toast = Toast.makeText(fragment.getView().getContext(), "", Toast.LENGTH_LONG);

                        if (Utils.checkData(toast, name, asv)) {
                            uploadProfile(profile, name.toString(), asv.toString());

                            dialog.cancel();
                        }
                    }
                })
                .setNegativeButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    private JSONObject getJsonFromProfile(String profile) {
        File file = new File(Utils.getProfilesPath(), profile);
        JSONObject ob = new JSONObject();

        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream(file));

            for (Map.Entry map : prop.entrySet()) {
                String key = map.getKey().toString();
                String value = map.getValue().toString();

                if (key.startsWith("CPU_VOLT_")) {
                    try {
                        ob.put(key.replace("CPU_VOLT_", ""), value);
                    } catch (Exception ignored) {}
                }

                if (key.startsWith("arm_slice_")) {
                    try {
                        key = key.replace("arm_slice_", "").replace("_volt", "");
                        key = "slice" + key;

                        Integer i = Integer.parseInt(value) * 1000;

                        ob.put(key, i.toString());
                    } catch (Exception ignored) { }
                }
            }
        } catch (Exception ignored) { }

        return ob;
    }

    private void uploadProfile(String profile, String name, String asv) {
        JSONObject ob = getJsonFromProfile(profile);

        if (ob.length() == 0) {
            return;
        }

        try {
            ob.put("name", name);
            ob.put("asv", asv);
            ob.put("id", Utils.getEmail(fragment.getView().getContext()));
        } catch (Exception ignored) { }

        upload(ob);
    }

    private void upload(JSONObject json) {
        UploadTask task = new UploadTask(fragment, json);
        task.execute("");
    }
}
