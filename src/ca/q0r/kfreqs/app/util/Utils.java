package ca.q0r.kfreqs.app.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.provider.Settings;
import android.text.Editable;
import android.widget.Toast;
import ca.q0r.kfreqs.app.R;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class Utils {
    public static Boolean isKernelSupported() {
        return new File("/res/uci.sh").exists();
    }

    public static ArrayList<File> getProfiles() {
        ArrayList<File> files = new ArrayList<File>();

        Collections.addAll(files, getProfilesPath().listFiles());

        return files;
    }

    public static File getDefaultProfile() {
        for (File file : getProfiles()) {
            if (file.getName().contains(".active.profile")) {
                String name = "default";

                try {
                    FileInputStream fis = new FileInputStream(file);

                    int content;
                    name = "";

                    while ((content = fis.read()) != -1) {
                        name += ((char) content);
                    }

                    name = name.replace("\n", "");
                } catch (Exception ignored) { }

                return new File(getProfilesPath(), name + ".profile");
            }
        }

        return null;
    }

    public static File getProfilesPath() {
        if (isKernelSupported()) {
            RootUtils.executeCommand(". /res/uci.sh config > /dev/null 2>&1");

            List<String> list = RootUtils.executeCommand("echo $PROFILE_PATH");
            String path = "";

            for (String st : list) {
                path += '\n' + st;
            }

            if (path.isEmpty()) {
                return null;
            }

            return new File(path.replace("\n", "").replace("\r", ""));
        }

        return null;
    }

    public static Boolean isASV(String asv) {
        Integer i = null;

        try {
            i = Integer.parseInt(asv);
        } catch(Exception ignored) {}

        return i != null && i < 13 && i > 0;
    }

    public static Boolean isNumber(String asv) {
        try {
            Integer i = Integer.parseInt(asv);

            if (i != null) {
                return true;
            }
        } catch (Exception ignored) {}

        return false;
    }

    public static Boolean checkData(Toast toast, Editable name, Editable asv) {
        if (name == null || name.toString().isEmpty()) {
            toast.setText("Name is blank!");
            toast.show();
            return false;
        }

        if (asv == null || asv.toString().isEmpty()) {
            toast.setText(R.string.text_asv_blank);
            toast.show();
            return false;
        }

        if (!isNumber(asv.toString())) {
            toast.setText(R.string.text_asv_not_number);
            toast.show();
            return false;
        }

        if (!isASV(asv.toString())) {
            toast.setText(R.string.text_not_asv);
            toast.show();
            return false;
        }

        return true;
    }

    public static String getEmail(Context context) {

        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            String email = getId(context);

            if (email == null) {
                email = UUID.randomUUID().toString();
            }

            return email + "@android.id";
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;

        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }

        return account;
    }

    public static String getId(Context context) {
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return  id != null ? id : "";
    }

    public static HashMap<String, String> getMapFromJSON(JSONObject json) {
        HashMap<String, String> map = new HashMap<String, String>();

        for (Object key : json.entrySet()) {
            map.put(key.toString(), json.get(key).toString());
        }

        return map;
    }
}
