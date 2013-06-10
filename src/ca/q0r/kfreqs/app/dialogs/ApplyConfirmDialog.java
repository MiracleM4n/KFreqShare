package ca.q0r.kfreqs.app.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.tasks.ApplyTask;

public class ApplyConfirmDialog {
    private Fragment fragment;
    private String profile;

    public ApplyConfirmDialog(Fragment frag, String pName) {
        fragment = frag;
        profile = pName;
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getView().getContext());

        String st = fragment.getString(R.string.apply_confirm).replace("@profile_id", profile.split("\\.")[0]);

        builder.setMessage(st)
                .setPositiveButton(R.string.button_apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ApplyTask task = new ApplyTask(fragment, profile);

                        task.execute("");

                        dialog.dismiss();
                    }})
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }});

        return builder.create();
    }
}
