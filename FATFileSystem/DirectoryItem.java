package FATFileSystem;

import java.nio.ByteBuffer;
import java.util.Objects;

// 目录项类
public class DirectoryItem {
    private int type;           // 文件属性
    private String fileName;    // 文件名
    private int startCluster;   // 开始簇号
    private int numOfBlock;     // 块数
    public final static int unitSize = 20;

    public DirectoryItem(int type,String fileName, int startCluster, int numOfBlock) {
        this.type = type;
        this.fileName = fileName;
        this.startCluster = startCluster;
        this.numOfBlock = numOfBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DirectoryItem that = (DirectoryItem) o;
        return Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName);
    }

    public byte[] getData(){
        byte[] info = new byte[24];
        ByteBuffer buffer = ByteBuffer.wrap(info,0,info.length);
        buffer.put(ByteTransfer.getBytes(type));
        buffer.put(ByteTransfer.getBytes(fileName,8));
        buffer.put(ByteTransfer.getBytes(startCluster));
        buffer.put(ByteTransfer.getBytes(numOfBlock));
        return info;
    }

    // getter and setter
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStartCluster() {
        return startCluster;
    }

    public void setStartCluster(int startCluster) {
        this.startCluster = startCluster;
    }

    public int getNumOfBlock() {
        return numOfBlock;
    }

    public void setNumOfBlock(int numOfBlock) {
        this.numOfBlock = numOfBlock;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{\"类型：" +
                FILETYPE.getType(type) + "\",\"文件名：" +
                fileName + '\"' + ",开始簇号：" +
                startCluster + ",簇数量：" +
                numOfBlock +
                "}\n";
    }
}