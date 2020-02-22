package com.xinchao.utils;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClient {

    String cookieval = null;

    public String sendGet(String url, String param) {
        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            String urlNameString =  url;
            if(param != null && param != ""){
                urlNameString = "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Cookie", cookieval);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setUseCaches(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            InputStream inputStream = connection.getInputStream();
            String charset = "UTF-8";
            Matcher matcher = Pattern.compile("charset=\\S*").matcher(connection.getContentType());
            if (matcher.find()) {
                charset = matcher.group().replace("charset=", "");
            }
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();;     //获得网站的二进制数据
            result = new String(getData, charset);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, String param) {
        PrintWriter out = null;
        ByteArrayOutputStream bos = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            InputStream inputStream = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();;     //获得网站的二进制数据
            result = new String(getData, "gb2312");
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public String sendGetNoRedirects(String url, String param) {
        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            String urlNameString =  url;
            if(param != null && param != ""){
                urlNameString = "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setUseCaches(true);

            // 设置通用的请求属性
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            cookieval = connection.getHeaderField("set-cookie").split(";")[0];
            return cookieval;

            /*InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();*/
            /*// 定义 BufferedReader输入流来读取URL的响应
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();;     //获得网站的二进制数据
            result = new String(getData, "gb2312");*/
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
