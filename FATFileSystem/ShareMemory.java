package FATFileSystem;

import com.sun.deploy.nativesandbox.NativeSandboxBroker;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class ShareMemory{
    private int capacity;                   // 共享内存容量
    private int size;                       // 共享内存当前使用的容量
    private String sharePath = null;        // 共享内存路径
    private RandomAccessFile RAFile = null; // 随机访问文件对象
    private FileChannel channel = null;     // 共享内存文件管道
    private MappedByteBuffer mmp = null;    // 共享内存缓冲区对象
    private FileLock lock = null;           // 文件锁

    // 创建或打开一个共享内存
    public ShareMemory(int capacity, String sharePath) throws IOException {
        // 初始化成员变量
        this.size = 0;
        this.capacity = capacity;
        this.sharePath = sharePath;
        // 获得对应路径的随机访问文件对象引用
        RAFile = new RandomAccessFile(sharePath,"rw");
        // 通过随机访问文件对象RAFile，获得该文件的管道
        channel = RAFile.getChannel();
        // 获取管道对应的内存缓冲对象
        mmp = channel.map(FileChannel.MapMode.READ_WRITE,0,capacity);
    }

    // read
    public void read(String msg,int offset,int length,int wrap) throws IOException {
        StringBuilder res = new StringBuilder();
        res.append(msg + "\n");
        mmp.position(offset);
        mmp = mmp.force();
        for(int i = 1;i <= length;i++){
            res.append(mmp.get() + " ");
            if(wrap != 0 && i % wrap == 0 && i != 0) res.append("\n");
        }
        res.append("\n");
        System.out.println(res.toString());
    }

    private String extendString(String s){
        switch (s.length()){
            case 1:s = "0" + s;
        }
        return s;
    }

    // write
    public void write(DirectoryItem item,byte[] data,int offset,int length) {
        synchronized (item){
            ByteBuffer buffer = ByteBuffer.wrap(data,offset,length);
            mmp.put(data,offset, length);
        }

    }

    // getter and setter
    public int getCapacity(){ return capacity;}
    public void setCapacity(int capacity) { this.capacity = capacity;}
    public int getSize(){ return size;}
    public void setSize(int size) { this.size = size;}
    public String getSharePath(){ return sharePath;}
    public void setSharePath(String sharePath) { this.sharePath = sharePath;}
    public RandomAccessFile getRAFile(){ return RAFile;}
    public void setRAFile(RandomAccessFile RAFile) { this.RAFile = RAFile;}
    public FileChannel getChannel(){ return channel;}
    public void setChannel(FileChannel channel) { this.channel = channel;}
    public MappedByteBuffer getMmp() throws IOException {return mmp;}
    public void setMmp(MappedByteBuffer mmp) { this.mmp = mmp;}
    public FileLock getLock(){ return lock;}
    public void setLock(FileLock lock) { this.lock = lock;}
}

