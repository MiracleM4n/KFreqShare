package ca.q0r.kfreqs.app.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.dialogs.ApplyConfirmDialog;
import ca.q0r.kfreqs.app.dialogs.UploadConfirmDialog;
import ca.q0r.kfreqs.app.tasks.ProfileLoadTask;

import java.util.ArrayList;

public class LocalTab extends Fragment implements View.OnClickListener {
    private String pName = "";
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getView().getContext(), android.R.layout.simple_list_item_1, list);

        adapter.setNotifyOnChange(true);

        ProfileLoadTask task = new ProfileLoadTask(this, true);
        task.execute("");

        LinearLayout uploadDecline = (LinearLayout) getView().findViewById(R.id.layout_cau);
        uploadDecline.setVisibility(LinearLayout.GONE);

        final Button button1 = (Button) uploadDecline.findViewById(R.id.button_local_cancel);
        button1.setOnClickListener(this);

        final Button button2 = (Button) uploadDecline.findViewById(R.id.button_local_apply);
        button2.setOnClickListener(this);


        final Button button3 = (Button) uploadDecline.findViewById(R.id.button_local_upload);
        button3.setOnClickListener(this);

        ListView lv = (ListView) getView().findViewById(R.id.view_local_profiles);

        TextView vw = new TextView(getView().getContext());

        lv.addHeaderView(vw);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                LinearLayout uploadDecline = (LinearLayout) getView().findViewById(R.id.layout_cau);

                pName = list.get(position - 1);

                uploadDecline.setVisibility(LinearLayout.VISIBLE);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_local, container, false);
    }

    /*----------------------------
    ---------  onClick  ---------
    ----------------------------*/

    @Override
    public void onClick(View v) {
        LinearLayout uploadDecline = (LinearLayout) getView().findViewById(R.id.layout_cau);

        int id = v.getId();

        if (id == R.id.button_local_cancel) {
            uploadDecline.setVisibility(LinearLayout.GONE);
        } else if (id == R.id.button_local_apply) {
            uploadDecline.setVisibility(LinearLayout.GONE);

            new ApplyConfirmDialog(this, pName).createDialog().show();
        } else if (id == R.id.button_local_upload) {
            uploadDecline.setVisibility(LinearLayout.GONE);

            new UploadConfirmDialog(this, pName).createDialog().show();
        }
    }

    public void setList(ArrayList<String> nList) {
        adapter.clear();
        adapter.addAll(nList);
    }
}
