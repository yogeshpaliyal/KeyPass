package com.yogeshpaliyal.keypass.utils;

/*
 * @author Yogesh Paliyal
 * techpaliyal@gmail.com
 * https://techpaliyal.com
 * created on 21-02-2021 13:36
 */
public class ByteUtil {
    public static long byteArray5ToLong(byte[] bytes, int offset) {
        return
                ((bytes[offset]     & 0xffL) << 32) |
                        ((bytes[offset + 1] & 0xffL) << 24) |
                        ((bytes[offset + 2] & 0xffL) << 16) |
                        ((bytes[offset + 3] & 0xffL) << 8) |
                        ((bytes[offset + 4] & 0xffL));
    }
}


