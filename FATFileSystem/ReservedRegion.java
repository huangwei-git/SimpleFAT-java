package FATFileSystem;

import com.sun.scenario.effect.Offset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

// 保留区
public class ReservedRegion {
    private int sectorSize;     // 每个扇区的字节数
    private int sectorNum;      // 每簇的扇区数
    private int reservedRegion; // 保留扇区数
    private int fatNum;         // FAT表个数
    private int fileSystemSize; // 文件系统大小(扇区数)
    private int fatSize;        // FAT表大小(扇区数)
    private int rootDir;        // 根目录起始簇号
    public static final int unitySize = 28;

    public ReservedRegion(int fileCapacity,int rootDir) {
        sectorSize = 512;
        sectorNum = 4;
        reservedRegion = 62;
        fatNum = 1;
        fileSystemSize = fileCapacity/(sectorSize * sectorNum);
        fatSize =
        this.rootDir = rootDir;
    }

    public int getSize(){
        return unitySize;
    }

    public int loadData(ShareMemory shm, int offset) throws IOException {
        byte[] info = new byte[4 * 7];
        ByteBuffer buffer = ByteBuffer.wrap(info,0,4*7);
        buffer.put(ByteTransfer.getBytes(sectorSize));
        buffer.put(ByteTransfer.getBytes(sectorNum));
        buffer.put(ByteTransfer.getBytes(reservedRegion));
        buffer.put(ByteTransfer.getBytes(fatNum));
        buffer.put(ByteTransfer.getBytes(fileSystemSize));
        buffer.put(ByteTransfer.getBytes(fatSize));
        buffer.put(ByteTransfer.getBytes(rootDir));
        MappedByteBuffer mmp = shm.getMmp();
        mmp.position(offset);
        mmp.put(info);
        return info.length;
    }

    //getter setter

    public int getSectorSize() {
        return sectorSize;
    }

    public void setSectorSize(int sectorSize) {
        this.sectorSize = sectorSize;
    }

    public int getSectorNum() {
        return sectorNum;
    }

    public void setSectorNum(int sectorNum) {
        this.sectorNum = sectorNum;
    }

    public int getReservedRegion() {
        return reservedRegion;
    }

    public void setReservedRegion(int reservedRegion) {
        this.reservedRegion = reservedRegion;
    }

    public int getFatNum() {
        return fatNum;
    }

    public void setFatNum(int fatNum) {
        this.fatNum = fatNum;
    }

    public int getFileSystemSize() {
        return fileSystemSize;
    }

    public void setFileSystemSize(int fileSystemSize) {
        this.fileSystemSize = fileSystemSize;
    }

    public int getFatSize() {
        return fatSize;
    }

    public void setFatSize(int fatSize) {
        this.fatSize = fatSize;
    }

    public int getRootDir() {
        return rootDir;
    }

    public void setRootDir(int rootDir) {
        this.rootDir = rootDir;
    }
}
