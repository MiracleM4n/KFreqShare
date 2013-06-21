package ca.q0r.kfreqs.app.tasks;

import android.support.v4.app.Fragment;
import android.widget.Toast;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.tabs.LocalTab;
import ca.q0r.kfreqs.app.tabs.RemoteTab;

public class RefreshTask {
    private Fragment fragment;

    public RefreshTask(Fragment frag) {
        fragment = frag;
    }

    public void reload() {
        Toast toast = Toast.makeText(fragment.getActivity().getCurrentFocus().getContext(), "", Toast.LENGTH_LONG);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(R.string.action_reload_fail);

        if (fragment instanceof LocalTab) {
            LocalTab lTab = (LocalTab) fragment;

            ProfileTask task = new ProfileTask(lTab, false);
            task.execute("");

            toast.setText(R.string.action_reload_success);
        } else if (fragment instanceof RemoteTab) {
            RemoteTab rTab = (RemoteTab) fragment;

            InfoTask task = new InfoTask(rTab, false);
            task.execute("");

            toast.setText(R.string.action_reload_success);
        }

        toast.show();
    }
}
