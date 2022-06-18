import FATFileSystem.ByteTransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class test{

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println(new String(ByteTransfer.getBytes("aaaaaaaa",8)));

        in.close();
    }
}
