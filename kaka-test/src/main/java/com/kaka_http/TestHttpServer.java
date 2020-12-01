package com.kaka_http;

import com.http.core.JsonFilterGroup;
import com.kaka.Startup;
import com.kaka.net.HttpServer;
import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import com.kaka.util.Charsets;
import com.kaka.util.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.*;

public class TestHttpServer extends Startup {

    public static void main(String[] args) {
//        Facade facade = FacadeFactory.getFacade();
//
//        facade.registerProxy(JsonFilterGroup.class);
//
//        TestHttpServer test = new TestHttpServer();
//        test.scan("com.http.core", "com.http.business");
//
//        HttpServer server = new HttpServer("myweb");
//        server.start(8080, 10, 0);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFFER_SIZE * 4);
        File[] files = new File[] {
                new File("D:/a.txt"),
                new File("D:/b.txt"),
                new File("D:/c.txt")
        };
        toZip(Arrays.asList(files), bos);

        byte[] bytes = bos.toByteArray();  //zip文件字节数组
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes));
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zis.getNextEntry()) != null) {
                String name = zipEntry.getName();
                if (name.equals("指定文件")) {
                    byte[] bs = getZipEntryData(zis); //指定文件的字节数组
                    System.out.println(bs.length);
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static byte[] getZipEntryData(InflaterInputStream zis) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] temp = new byte[1024];
            byte[] buf;
            int length;
            while ((length = zis.read(temp, 0, 1024)) != -1) {
                bout.write(temp, 0, length);
            }
            buf = bout.toByteArray();
            bout.close();
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final int BUFFER_SIZE = 2 * 1024;

    private static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                ZipEntry zipEntry = new ZipEntry(srcFile.getName());
                zos.putNextEntry(zipEntry);
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
