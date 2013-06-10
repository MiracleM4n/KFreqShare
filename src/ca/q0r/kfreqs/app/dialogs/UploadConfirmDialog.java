package ca.q0r.kfreqs.app.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import ca.q0r.kfreqs.app.R;

public class UploadConfirmDialog {
    private Fragment fragment;
    private String profile;

    public UploadConfirmDialog(Fragment frag, String pName) {
        fragment = frag;
        profile = pName;
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getView().getContext());

        String st = fragment.getString(R.string.upload_confirm).replace("@profile_id", profile.split("\\.")[0]);

        builder.setMessage(st)
                .setPositiveButton(R.string.button_upload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        UploadInfoDialog iDialog = new UploadInfoDialog(fragment, profile);

                        AlertDialog d = iDialog.createDialog();

                        dialog.dismiss();
                        d.show();
                    }})
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }});

        return builder.create();
    }
}
