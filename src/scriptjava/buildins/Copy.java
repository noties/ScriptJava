package scriptjava.buildins;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Created by Дмитрий on 23.08.2016.
 */
public class Copy {

    // copies value to clipboard and returns it unmodified
    public static <T> T copy(T value) {
        try {
            final String text = Str.str(value);
            final StringSelection selection = new StringSelection(text);
            final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return value;
    }

    private Copy() {

    }
}
