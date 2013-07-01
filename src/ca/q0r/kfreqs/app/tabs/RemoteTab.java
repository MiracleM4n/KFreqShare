package ca.q0r.kfreqs.app.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ca.q0r.kfreqs.app.R;
import ca.q0r.kfreqs.app.dialogs.DownloadConfirmDialog;
import ca.q0r.kfreqs.app.tasks.InfoDownloadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class RemoteTab extends Fragment implements View.OnClickListener {
    public String pName = "";
    ArrayList<String> list;
    HashMap<String, String> aMap;
    ArrayAdapter<String> adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = new ArrayList<String>();
        aMap = new HashMap<String, String>();

        adapter = new ArrayAdapter<String>(getView().getContext(), android.R.layout.simple_list_item_1, list);

        adapter.setNotifyOnChange(true);

        InfoDownloadTask task = new InfoDownloadTask(this, true);
        task.execute("");

        LinearLayout uploadDecline = (LinearLayout) getView().findViewById(R.id.layout_dc);
        uploadDecline.setVisibility(LinearLayout.GONE);

        final Button button = (Button) uploadDecline.findViewById(R.id.button_remote_download);
        button.setOnClickListener(this);

        final Button button2 = (Button) uploadDecline.findViewById(R.id.button_remote_cancel);
        button2.setOnClickListener(this);

        ListView lv = (ListView) getView().findViewById(R.id.view_remote_profiles);

        TextView vw = new TextView(getView().getContext());

        lv.addHeaderView(vw);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                LinearLayout uploadDecline = (LinearLayout) getView().findViewById(R.id.layout_dc);

                TextView tv = (TextView) view;

                pName = tv.getText().toString();

                String asv = aMap.get(pName);

                if (asv != null) {
                    Toast toast = Toast.makeText(getView().getContext(), "", Toast.LENGTH_SHORT);

                    toast.setText("ASV: " + asv);
                    toast.show();
                }

                uploadDecline.setVisibility(LinearLayout.VISIBLE);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_remote, container, false);
    }

    /*----------------------------
    ---------  onClick  ---------
    ----------------------------*/

    @Override
    public void onClick(View v) {
        LinearLayout uploadDecline = (LinearLayout) getView().findViewById(R.id.layout_dc);

        int id = v.getId();

        if (id == R.id.button_remote_download) {
            uploadDecline.setVisibility(LinearLayout.GONE);

            String asv = aMap.get(pName);

            if (asv != null) {
                DownloadConfirmDialog dialog = new DownloadConfirmDialog(this, pName);

                dialog.createConfirm(asv).show();
            }
        } else if (id == R.id.button_remote_cancel) {
            uploadDecline.setVisibility(LinearLayout.GONE);
        }
    }

    public void setList(ArrayList<String> nList) {
        adapter.clear();
        adapter.addAll(nList);
    }

    public void setAsvMap(HashMap<String, String> map) {
        aMap.clear();
        aMap.putAll(map);
    }
}
