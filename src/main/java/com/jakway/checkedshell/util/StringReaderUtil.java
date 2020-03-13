package com.jakway.checkedshell.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringReaderUtil {
    /**
     * from https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
     * @param is
     * @param enc
     * @return
     * @throws IOException
     */
    public static String inputStreamToString(
            InputStream is,
            String enc)
            throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = is.read();
        while(result != -1) {
            buf.write((byte) result);
            result = is.read();
        }

        return buf.toString(enc);
    }
}
