package FATFileSystem;

import java.nio.charset.StandardCharsets;

// 数据转换
public class ByteTransfer {

    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (data & 0xff);
        return bytes;
    }

    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data << 8);
        return bytes;
    }

    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] getBytes(String data){
        data = new StringBuilder(data).reverse().toString();
        return data.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] getBytes(String data,int bytelength){
        byte[] bytes = new byte[bytelength];
        byte[] tmp = data.getBytes();
        int len = tmp.length < bytelength ? tmp.length: bytelength;
        for(int i = 0;i < len;i++){
            bytes[i] = tmp[i];
        }
        return bytes;
    }

    public static String getString(byte[] bytes){
        return new String(bytes).replaceAll("\0","");
    }

    public static int getInt(byte[] bytes) {
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
    }

    public static String getHex(byte b){
        return Integer.toHexString((int)b);
    }
}


