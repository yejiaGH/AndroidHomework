package com.yejia.nio.ioexample;

import java.io.*;

/**
 * Created by yejia_alice on 15/12/10.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("data.txt");
//        写入操作
//        if(!file.exists()){
//            file.createNewFile();
//        }
//
//        FileOutputStream fileOutputStream = new FileOutputStream(file);
//        fileOutputStream.write("Hello Java IO".getBytes("UTF-8"));
//        fileOutputStream.close();
//    读取操作
//        if(file.exists()){
//            FileInputStream fileInputStream = new FileInputStream(file);
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
//
//            byte[] buffer = new byte[1024];
//
////            for(int result = 0; result!=-1;result=fileInputStream.read(buffer)){
////                bos.write(buffer,0,result);
////            }
//
//            int result = 0;
//            while((result=fileInputStream.read(buffer))!=-1){
//                bos.write(buffer,0,result);
//            }
//            byte[] bytesRead = bos.toByteArray();
//
//            bos.close();
//            fileInputStream.close();
//
//            System.out.println(new String(bytesRead,"UTF-8"));
//        }

        if(file.exists()){
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream,"UTF-8"));

            String line= null;
            StringBuffer content = new StringBuffer();
            while((line=bufferedReader.readLine())!=null){
                content.append(line);
            }
            fileInputStream.close();
            System.out.println(content);
        }



        System.out.println("End");
    }
}
