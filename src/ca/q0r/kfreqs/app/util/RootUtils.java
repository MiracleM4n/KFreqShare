package ca.q0r.kfreqs.app.util;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

import java.util.ArrayList;
import java.util.List;

public class RootUtils {
    private static Shell shell;
    private static Boolean rooted = false;

    public static void initRoot() {
        if (RootTools.isRootAvailable()
                || RootTools.isAccessGiven()) {
            try {
                shell = RootTools.getShell(true);

                rooted = true;
            } catch (Exception ignored) {
                rooted = false;
            }
        }
    }

    public static Boolean isRooted() {
        return rooted;
    }

    public static void reset() {
        try {
            shell.close();
        } catch (Exception ignored) { }

        rooted = false;
    }

    public static List<String> executeCommand(String comm) {
        final List<String> output = new ArrayList<String>();

        if (rooted) {
            CommandCapture command = new CommandCapture(0, comm){
                @Override
                public void output(int id, String line) {
                    output.add(line);
                }
            };

            try {
                shell.add(command).waitForFinish();
            } catch (Exception ignored) { }
        }

        return output;
    }
}
