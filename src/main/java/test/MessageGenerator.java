package test;

/**
 * Created by mike on 3/13/17.
 */
public class MessageGenerator {
    public static final int _MB = 1024 * 1024;

    public static final int _KB = 1024;

    private static String msgOf1MB;

    private static String msgOf1KB;

    private static String msgOf1B;

    static {
        msgOf1B = "a";
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < _KB; i++) tmp.append('t');
        msgOf1KB = tmp.toString();
        StringBuffer tmp2 = new StringBuffer();
        for (int i = 0; i < (_MB / _KB); i++) tmp2.append(tmp);
        msgOf1MB = tmp2.toString();
    }

    public static String duplicateString(int n, String s) {
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < n; i++) tmp.append(s);
        return tmp.toString();
    }

    public static String generateMessage(int length) {
        StringBuffer tmpM = new StringBuffer(duplicateString(length / _MB, msgOf1MB));

        length = length % _MB;
        StringBuffer tmpK = new StringBuffer(duplicateString(length / _KB, msgOf1KB));

        length = length % _KB;
        StringBuffer tmpB = new StringBuffer(duplicateString(length, msgOf1B));

        tmpM.append(tmpK);
        tmpM.append(tmpB);
        return tmpM.toString();
    }
}
