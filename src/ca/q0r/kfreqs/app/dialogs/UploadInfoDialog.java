package ca.q0r.kfreqs.app.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.tasks.ProfileUploadTask;
import ca.q0r.kfreqs.app.util.Utils;
import com.google.gson.JsonObject;

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

    private JsonObject getJsonFromProfile(String profile) {
        File file = new File(Utils.getProfilesPath(), profile);
        JsonObject json = new JsonObject();

        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream(file));

            for (Map.Entry map : prop.entrySet()) {
                String key = map.getKey().toString();
                String value = map.getValue().toString();

                if (key.startsWith("CPU_VOLT_")) {
                    try {
                        json.addProperty(key.replace("CPU_VOLT_", ""), value);
                    } catch (Exception ignored) {}
                }

                if (key.startsWith("arm_slice_")) {
                    try {
                        key = key.replace("arm_slice_", "").replace("_volt", "");
                        key = "slice" + key;

                        Integer i = Integer.parseInt(value) * 1000;

                        json.addProperty(key, i.toString());
                    } catch (Exception ignored) { }
                }
            }
        } catch (Exception ignored) { }

        return json;
    }

    private void uploadProfile(String profile, String name, String asv) {
        JsonObject json = getJsonFromProfile(profile);

        if (json.entrySet().size() == 0) {
            return;
        }

        try {
            json.addProperty("name", name);
            json.addProperty("asv", asv);
            json.addProperty("id", Utils.getEmail(fragment.getView().getContext()));
            json.addProperty("model", Build.MODEL.replace("GT-", "").toLowerCase());
        } catch (Exception ignored) { }

        upload(json);
    }

    private void upload(JsonObject json) {
        ProfileUploadTask task = new ProfileUploadTask(fragment, json);
        task.execute("");
    }
}
