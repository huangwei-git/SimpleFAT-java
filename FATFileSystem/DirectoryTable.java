package FATFileSystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

//目录表
public class DirectoryTable {
    Set<DirectoryItem> dirTable = null; // 目录表
    int numOfCluster;                   // 记录当前目录使用的簇的数量
    FAT fat;

    // 构造函数
    public DirectoryTable(){
        // 实例化目录表，用匿名内部类实现Comparetor接口，按开始块顺序排列目录项
        dirTable = new TreeSet<>(new Comparator<DirectoryItem>() {
            @Override
            public int compare(DirectoryItem o1, DirectoryItem o2) {
                if(o1.getFileName().equals(o2.getFileName())) return 0;
                return o1.getStartCluster() - o2.getStartCluster();
            }
        });
        numOfCluster = 1;
    }

    public boolean add(int sign,String fileName){
        for(DirectoryItem iter : dirTable) if(iter.getFileName().equals(fileName)) return false;
        // 从fat表中获取一个未被使用的簇(fat映射为-1)
        int startCluster = fat.getACluster();
        // 将获得的簇作为起始簇
        dirTable.add(new DirectoryItem(sign,fileName,startCluster,1));
        // 在fat表中添加该项
        fat.addACluster(startCluster);
        return true;
    }

    public boolean delete(String fileName){
        // 遍历目录项
        for(DirectoryItem iter : dirTable){
            if(iter.getFileName().equals(fileName)){
                // 在fat表中将空间释放
                fat.delete(iter.getStartCluster());
                // 在目录表中删除该项
                dirTable.remove(iter);
                // 返回删除成功
                return true;
            }
        }
        // 删除失败
        return false;
    }

    public void set(int type,String fileName,int startCluster,int blockNum){
        dirTable.add(new DirectoryItem(type,fileName,startCluster,blockNum));
    }

    // 获得文件的开始簇号
    public int get(String fileName){
        int res = 0;
        if(dirTable.contains(fileName))
            for(DirectoryItem iter : dirTable)
                if(iter.equals(fileName))
                    return iter.getStartCluster();
        return res;
    }

    public void loadData(ShareMemory shm, int offset) throws IOException {
        int cnt = 0;
        MappedByteBuffer mmp = shm.getMmp();
        mmp.position(offset);
        mmp.put(ByteTransfer.getBytes(dirTable.size()));
        for(DirectoryItem iter : dirTable){
            byte[] info = new byte[20];
            ByteBuffer buffer = ByteBuffer.wrap(info,0,20);
            buffer.put(ByteTransfer.getBytes(iter.getType()));
            buffer.put(ByteTransfer.getBytes(iter.getFileName(),8));
            buffer.put(ByteTransfer.getBytes(iter.getStartCluster()));
            buffer.put(ByteTransfer.getBytes(iter.getNumOfBlock()));
            System.out.println("==========>" + new String(info));
            mmp.put(info);
        }
    }

    public int getSize(){
        return DirectoryItem.unitSize * dirTable.size() + 4;
    }

    public void setFat(FAT fat){
        this.fat = fat;
    }

    @Override
    public String toString() {
        return dirTable.toString();
    }
}
