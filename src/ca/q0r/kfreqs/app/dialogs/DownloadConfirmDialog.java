package ca.q0r.kfreqs.app.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.tasks.ProfileDownloadTask;

public class DownloadConfirmDialog {
    private Fragment fragment;
    private String profile;

    public DownloadConfirmDialog(Fragment frag, String pName) {
        fragment = frag;
        profile = pName;
    }

    public AlertDialog createConfirm(String asv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getView().getContext());

        String st = fragment.getString(R.string.confirm_download)
                .replace("@profile_id", profile)
                .replace("@asv", asv);

        builder.setMessage(st)
                .setPositiveButton(R.string.title_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ProfileDownloadTask dTask = new ProfileDownloadTask(fragment, profile);
                        dTask.execute("");

                        dialog.cancel();
                    }})
                .setNegativeButton(R.string.title_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }});

        return builder.create();
    }
}
