package com.example.zs.callimgapplicaiton;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/26 0026.
 */

public class Util {

    public static void closeQuietly(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
