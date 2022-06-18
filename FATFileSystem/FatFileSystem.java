package FATFileSystem;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.Scanner;

public class FatFileSystem {
    ShareMemory shareMemory = null;
    ReservedRegion reservedRegion = null;
    DirectoryTable directoryTable = null;
    FAT fat = null;
    int offsetReservedRegion;
    int offsetDirectoryTable;

    public FatFileSystem(String path,int capacity) throws IOException {
        try {
            shareMemory = new ShareMemory(capacity,path);
        } catch (IOException e) {
            System.out.println("打开共享内存失败");
            System.exit(-1);
        }
        reservedRegion = new ReservedRegion(shareMemory.getCapacity(),2);
        directoryTable = new DirectoryTable();
        fat = new FAT();
        directoryTable.setFat(fat);
        fat.setDt(directoryTable);

//        print();
//        open("aaaa");
//        flush();
//        print();
//        print();

        reservedRegion.loadData(shareMemory,0);
        loadFAT();
        loadDT();

    }

    // 载入FAT表
    private void loadFAT() throws IOException {
        MappedByteBuffer mmp = shareMemory.getMmp();
        // 找到FAT表的起始位置
        mmp.position(reservedRegion.getSize());
        byte[] key = new byte[4];
        byte[] value = new byte[4];
        byte[] numBytes = new byte[4];
        mmp.get(numBytes);
        int num = ByteTransfer.getInt(numBytes);
        for(int i = 0;i < num;i++) {
            mmp.get(key);
            mmp.get(value);
            fat.set(ByteTransfer.getInt(key), ByteTransfer.getInt(value));
        }
    }

    private void loadDT() throws IOException {
        MappedByteBuffer mmp = shareMemory.getMmp();
        // 目录表的起始位置
        mmp.position(reservedRegion.getSize() + fat.getSize());
        byte[] numBytes = new byte[4];
        mmp.get(numBytes);
        int num = ByteTransfer.getInt(numBytes);

        byte[] type = new byte[4];
        byte[] fileName = new byte[8];
        byte[] startCluster = new byte[4];
        byte[] blockNum = new byte[4];
        for(int i = 0;i < num;i++){
            mmp.get(type);
            mmp.get(fileName);
            mmp.get(startCluster);
            mmp.get(blockNum);
            directoryTable.set(ByteTransfer.getInt(type),
                    ByteTransfer.getString(fileName),
                    ByteTransfer.getInt(startCluster),
                    ByteTransfer.getInt(blockNum));
        }
    }

    private void flush() throws IOException {
        fat.loadData(shareMemory,reservedRegion.getSize());
        directoryTable.loadData(shareMemory,reservedRegion.getSize() + fat.getSize());
    }


    // 创建目录
    public void mkdir(String fileName){
        directoryTable.add(FILETYPE.DIRECTORY,fileName);
        fat.addACluster(directoryTable.get(fileName));
    }

    // 删除目录

    // 创建文件
    public boolean touch(String fileName) throws IOException {
        if(!directoryTable.add(FILETYPE.FILE,fileName)) return false;
        flush();// 刷新共享内存的内容
        return true;
    }

    // 删除文件
    public boolean rm(String fileName) throws IOException {
        boolean res = directoryTable.delete(fileName);
        flush();
        return res;
    }

    // 结构
    public void ls(){
        System.out.println(directoryTable);
    }

    // 获得非数据区大小
    public int getSize(){
        return reservedRegion.getSize() + fat.getSize() + directoryTable.getSize();
    }

    public void print() throws IOException {
        shareMemory.read("保留区",0,reservedRegion.getSize(),4);
        shareMemory.read("FAT表",reservedRegion.getSize() + 4, fat.getSize() - 4,FAT.unitSize);
        shareMemory.read("目录表",reservedRegion.getSize() + fat.getSize() + 4, directoryTable.getSize() - 4,DirectoryItem.unitSize);
    }

    public int getAddress(int clusterNum){
        // 获得0号簇的位置
        int offset = reservedRegion.getSize() + fat.getSize() + directoryTable.getSize();
        // 计算偏移量
        int bias = reservedRegion.getSectorSize() * reservedRegion.getSectorNum();
        return offset + bias;
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        FatFileSystem ffs = new FatFileSystem("./src/test.txt",1000);
        ffs.print();
        ffs.fat.getFat().remove(1);
        ffs.fat.getFat().remove(2);
        while (in.hasNext()){
            String op = in.next();
            if(op.equals("open")){
                String fileName = in.next();
                if(!ffs.touch(fileName)) System.out.println("文件" + fileName + "已存在，创建失败");
                else System.out.println("文件" + fileName + "创建成功");
            }else if(op.equals("rm")){
                String fileName = in.next();
                if(ffs.rm(fileName)) System.out.println("文件" + fileName + "删除成功");
                else System.out.println("文件" + fileName + "不存在");
            }else if(op.equals("ls")){
                ffs.ls();
                System.out.println(ffs.fat.getFat());
            }else if(op.equals("exit")){
                ffs.print();
                break;
            }
        }

        in.close();
    }

}