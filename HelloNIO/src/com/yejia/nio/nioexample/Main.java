package com.yejia.nio.nioexample;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by yejia_alice on 15/12/10.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        new Main();
        System.out.println("End");
    }

    public Main() throws IOException {
//        write();
        read();
    }

    void write() throws IOException {
        File file = new File("data.txt");
        if(!file.exists()){
            file.createNewFile();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileChannel channel = fileOutputStream.getChannel();
        channel.write(ByteBuffer.wrap("Hello Java NIO".getBytes("UTF-8")));
        channel.close();
        fileOutputStream.close();

    }

    void read() throws IOException {
        File file = new File("data.txt");
        if(file.exists()){
            FileInputStream fileInputStream = new FileInputStream(file);

            FileChannel channel = fileInputStream.getChannel();

            ByteArrayOutputStream dist = new ByteArrayOutputStream();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int result = 0;
            while((result=channel.read(buffer))!=-1){
                buffer.flip();
                dist.write(buffer.array(),0,buffer.remaining());
                buffer.clear();
            }

//            System.out.println(new String(buffer.array(),0,buffer.position(),"UTF-8"));
            System.out.println(new String(dist.toByteArray(),"UTF-8"));
            fileInputStream.close();
        }

    }
}
