package FATFileSystem;

public class FILETYPE {
    public final static int CURRENT_DIRECTORY = 0;  // 当前目录
    public final static int DIRECTORY = 1;          // 子目录
    public final static int PARENT_DIRECTORY = 2;   // 上级目录
    public final static int FILE = 3;               // 普通文件

    public static String getType(int num){
        String res = "";
        switch (num){
            case 0:res = "./";break;
            case 1:res = "目录";break;
            case 2:res = "../";break;
            case 3:res = "文件";break;
            default:res = "error";
        }
        return res;
    }
}

