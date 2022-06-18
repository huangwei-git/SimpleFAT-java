package FATFileSystem;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.*;

//FAT表
public class FAT {
    private Map<Integer,Integer> fat = null;        // FAT表
    private int memoryCapacity = 100 * 1024 * 1024; // 共享内存大小
    private int clusterSize = 2048;                 // 每个簇的大小
    public static final int unitSize = 8;           // 单位大小8B
    private DirectoryTable dt;

    public FAT(){
        fat = new HashMap<>();
    }

    // 为num号簇所在文件添加一个簇
    public void addACluster(int clusterNum){
        int newCluster = getACluster();
        fat.put(clusterNum,newCluster);
        fat.put(newCluster,-1);
    }

    // 获得一个空的簇号
    public int getACluster(){
        int ret = -1;
        while(fat.keySet().contains(++ret)) ;
        return ret;
    }

    // 获得下一个簇号
    public int nextCluster(int currentCluster){
        return fat.get(currentCluster);
    }

    // 获得文件的簇列表
    public int getFileSize(int startCluster){
        int cnt = 0;
        int ptr = startCluster;
        while(fat.keySet().contains(ptr)){
            cnt++;
            ptr = fat.get(ptr);
        }
        return cnt;
    }

    public void set(int key,int value){
        fat.put(key, value);
    }

    // 删除文件
    public void delete(int startCluster){
        // ptr指向当前需要释放的簇
        int ptr = startCluster;
        // nptr指向下一个要删除的簇，若下一个簇号为-1，则结束
        int nptr = -1;
        while(fat.keySet().contains(ptr)){
            nptr = fat.get(ptr);
            fat.remove(ptr);
            ptr = nptr;
        }
    }

    public void loadData(ShareMemory shm, int offset){
        try {
            MappedByteBuffer mmp = shm.getMmp();
            mmp.position(offset);
            mmp.put(ByteTransfer.getBytes(fat.size()));
            for(Map.Entry<Integer,Integer> entry : fat.entrySet()){
                byte[] b = ByteTransfer.getBytes(entry.getKey());
                mmp.put(ByteTransfer.getBytes(entry.getKey()));
                mmp.put(ByteTransfer.getBytes(entry.getValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDt(DirectoryTable dt){
        this.dt = dt;
    }

    public int getSize(){
        return unitSize * fat.size() + 4;
    }

    public Map<Integer, Integer> getFat() {
        return fat;
    }
}
