package ca.q0r.kfreqs.app.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.tasks.DownloadTask;

public class DownloadConfirmDialog {
    private Fragment fragment;
    private String profile;

    public DownloadConfirmDialog(Fragment frag, String pName) {
        fragment = frag;
        profile = pName;
    }

    public AlertDialog createConfirm(String asv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getView().getContext());

        String st = fragment.getString(R.string.download_confirm).replace("@profile_id", profile);
        st = st.replace("@asv", asv);

        builder.setMessage(st)
                .setPositiveButton(R.string.button_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DownloadTask dTask = new DownloadTask(fragment, profile);
                        dTask.execute("");
                    }})
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }});

        return builder.create();
    }
}
