package util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class HttpUtil {
    private final static String TAG = "HttpUtil";

    public static String upload(String uploadUrl, String uploadFile, String token) {
        String resp = "";
        String fileName = uploadFile.substring(uploadFile.lastIndexOf("/"));
        String end = "\r\n"; // 结束字符串
        String hyphens = "--"; // 连接字符串
        String boundary = "WUm4580jbtwfJhNp7zi1djFEO3wNNm"; // 边界字符串

        try (FileInputStream fis = new FileInputStream(uploadFile)) {
            URL url = new URL(uploadUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("Accept", "image/*");
            connection.setRequestProperty("Accept-Language", "zh_CN");
            connection.setRequestProperty("User-Encoding", "gzip, deflate");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestProperty("token", token);

            DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
            ds.writeBytes(hyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                        + "name=\"file\";filename=\"" + fileName + "\"" + end);
            ds.writeBytes(end);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = fis.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(hyphens + boundary + hyphens + end);
            ds.close();
            resp = getUnzipString(connection);
            Log.d(TAG, "状态码 " + connection.getResponseCode() + ", 应答报文 " + resp);
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resp;
    }

    // 把输入流中的数据按照指定字符编码转换为字符串。处理大量数据需要使用本方法
    private static String isToString(InputStream is, String charset) {
        String result = "";
        // 创建一个字节数组的输出流对象
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int i = -1;
            while ((i = is.read()) != -1) { // 循环读取输入流中的字节数据
                baos.write(i); // 把字节数据写入字节数组输出流
            }
            // 把字节数组输出流转换为字节数组
            result = baos.toString(charset); // 将字节数组按照指定的字符编码生成字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result; // 返回转换后的字符串
    }

    // 从HTTP连接中获取已解压且重新编码后的应答报文
    private static String getUnzipString(HttpURLConnection conn) throws IOException {
        String contentType = conn.getContentType(); // 获取应答报文的内容类型（包括字符编码）
        String charset = "UTF-8"; // 默认的字符编码为UTF-8
        if (contentType != null) {
            if (contentType.toLowerCase().contains("charset=gbk")) { // 应答报文采用gbk编码
                charset = "GBK"; // 字符编码改为GBK
            } else if (contentType.toLowerCase().contains("charset=gb2312")) { // 采用gb2312编码
                charset = "GB2312"; // 字符编码改为GB2312
            }
        }
        String contentEncoding = conn.getContentEncoding(); // 获取应答报文的压缩方式
        InputStream is = conn.getInputStream(); // 获取HTTP连接的输入流对象
        String result = "";
        if (contentEncoding != null && contentEncoding.contains("gzip")) { // 应答报文使用gzip压缩
            // 根据输入流对象构建压缩输入流
            try (GZIPInputStream gis = new GZIPInputStream(is)) {
                // 把压缩输入流中的数据按照指定字符编码转换为字符串
                result = isToString(gis, charset);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 把输入流中的数据按照指定字符编码转换为字符串
            result = isToString(is, charset);
        }
        return result; // 返回处理后的应答报文
    }
}
