package ioLearning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * @auther chen.haitao
 * @date 2019-01-28
 */
public class NioTest {

    public static void main(String[] args) throws Exception {
        test6();
    }

    public static void test1() throws Exception {

        FileInputStream fileInputStream = new FileInputStream("IoTest.txt");
        FileChannel fileChannel = fileInputStream.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(512);
        System.out.println("总容量capacity = " + buffer.capacity() + ",limit" + buffer.limit() + ",position" + buffer.position());

        fileChannel.read(buffer);
        System.out.println("总容量capacity = " + buffer.capacity() + ",limit" + buffer.limit() + ",position" + buffer.position());

        buffer.flip();

        while (buffer.remaining() > 0) {
            System.out.println("总容量capacity = " + buffer.capacity() + ",limit" + buffer.limit() + ",position" + buffer.position());

            byte b = buffer.get();

            System.out.println("Character：" + (char) b);

            fileInputStream.close();
        }
    }

    public static void test2() throws Exception{

        FileOutputStream fileOutPutStream = new FileOutputStream("IoTest.txt");
        Channel outChannel = fileOutPutStream.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(512);

        byte[] buf = "hello world !".getBytes();
        for (int i = 0; i < buf.length; i++) {
            buffer.put(buf[i]);
            System.out.println("总容量capacity = " + buffer.capacity() + ",limit" + buffer.limit() + ",position" + buffer.position());
        }

        buffer.flip();
        System.out.println("总容量capacity = " + buffer.capacity() + ",limit" + buffer.limit() + ",position" + buffer.position());
        ((FileChannel) outChannel).write(buffer);
        fileOutPutStream.close();
    }

    /**
     * 测试，FileInputStream和FileOutputStream同时打开会清空文件内容？
     * @throws Exception
     */
    public static void test3() throws Exception{
        FileInputStream fileInPutStream = new FileInputStream("IoTest.txt");
        FileOutputStream fileOutPutStream = new FileOutputStream("IoTest.txt");

        fileInPutStream.close();
        fileOutPutStream.close();
    }


    /**
     * buffer.clear()特别重要
     * @throws Exception
     */
    public static void test4() throws Exception{
        FileInputStream fileInPutStream = new FileInputStream("input.txt");
        FileOutputStream fileOutPutStream = new FileOutputStream("output.txt");

        ByteBuffer buffer = ByteBuffer.allocate(128);

        FileChannel inCHannel = fileInPutStream.getChannel();
        FileChannel outChannel = fileOutPutStream.getChannel();


        while (true){
            buffer.clear();
            int read = inCHannel.read(buffer);
            if(-1 == read){
                break;
            }
            buffer.flip();
            outChannel.write(buffer);
        }

        fileInPutStream.close();
        fileOutPutStream.close();
    }


    /**
     * ByteBuffer具有类型化put以及类型化get，可以自动转化为其他类型的变量。
     */
    public static void test5(){
        ByteBuffer buffer = ByteBuffer.allocate(64);

        buffer.putInt(1);
        buffer.putLong(1);
        buffer.putChar('j');
        buffer.putShort((short) 6);
        buffer.putDouble(1.2);

        buffer.flip();

        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getChar());
        System.out.println(buffer.getShort());
        System.out.println(buffer.getDouble());
    }


    /**
     * slice  复制片段到另一个buffer，然后其实是指向同一块内存。
     */
    public static void test6(){
        ByteBuffer buffer = ByteBuffer.allocate(10);

        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte)i);
        }

        buffer.position(2);
        buffer.limit(6);
        ByteBuffer slice = buffer.slice();

        for(int i = 0; i<slice.capacity();i++){
            byte b = slice.get(i);
            b*=2;
            slice.put(i, b);
        }

        buffer.position(0);
        buffer.limit(buffer.capacity());

        while(buffer.hasRemaining()){
            System.out.println(buffer.get());
        }


    }

    /**
     * 小知识点：openjdk和oraclejdk的区别，开源的程度
     * 理解directBuffer
     *
     * 因为也是new出来的，也是在堆上面，但是也有堆外内存（可以看一下allocate那个初始化源码），就是说不在java内部。分为direct和native
     * 可以看到buffer中，有一个address，就是direct用来存放native中的。之所以放到父类当中，是因为netty需要调用。
     *
     * 操作系统进行io，不直接操作java堆内存，有一个拷贝的过程，如果使用direct，就实现了零拷贝。
     * @throws Exception
     */
    public static void test7() throws Exception{
        FileInputStream fileInPutStream = new FileInputStream("input.txt");
        FileOutputStream fileOutPutStream = new FileOutputStream("output.txt");

        //初始化 有三种，wrap(Array[] array),allocateDirect(),allocate();
        //第一种可以通过参数中的array直接操作


        ByteBuffer buffer = ByteBuffer.allocateDirect(128);


        FileChannel inCHannel = fileInPutStream.getChannel();
        FileChannel outChannel = fileOutPutStream.getChannel();


        while (true){
            buffer.clear();
            int read = inCHannel.read(buffer);
            if(-1 == read){
                break;
            }
            buffer.flip();
            outChannel.write(buffer);
        }

        fileInPutStream.close();
        fileOutPutStream.close();
    }




}