
package com.ll.download.util;

import java.io.IOException;

public class Base64Util {

    public static void main(String[] args) {
        // String src = "1234567";
        String src = "http://distribution.hexxeh.net/archive/vanilla/4028.0.2013_04_20_1810-r706c4144/ChromeOS-Vanilla-4028.0.2013_04_20_1810-r706c4144-VirtualBox.zip";
        // String src =
        // "http://dldx.csdn.net/fd.php?i=393843408919813&s=cda783d1ab8b2cc2fe956e66bcebf0b6";
        // src = decode(src);
        // Log.v(TAG, "after decode()[src="+src+"]");
        // src = encode(src);
        // Log.v(TAG, "after encode()[src="+src+"]");
        float f1 = 99.99f;
        float f2 = 99.99f;
        System.out.println(f1 == f2);
    }

    private static final String TAG = Base64Util.class.getSimpleName();

//    public static String decode(String src) {
//        try {
//            byte[] bytes = new BASE64Decoder().decodeBuffer(src);
//            return new String(bytes, "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static String encode(String src) {
//        BASE64Encoder encoder = new BASE64Encoder();
//        return encoder.encode(src.getBytes());
//    }

}
