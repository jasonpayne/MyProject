package com.payne.shop.utils;

import com.alibaba.druid.util.Base64;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientTest {

    String cookieval = null;

    public String sendGet(String url, String param) {
//
//        HttpClient httpClient = new HttpClient();
//        httpClient.getHostConfiguration().setProxy("192.168.101.1", 5608);
//        httpClient.getParams().setAuthenticationPreemptive(true);
////如果代理需要密码验证，这里设置用户名密码
//        httpClient.getState().setProxyCredentials(AuthScope.ANY, new UsernamePasswordCredentials("llying.iteye.com","llying"));


        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            String urlNameString =  url;
            if(param != null && param != ""){
                urlNameString = "?" + param;
            }
            URL realUrl = new URL(urlNameString);

            /*// 创建代理服务器
            InetSocketAddress addr = new InetSocketAddress("40.73.34.218",20000);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理

            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection(proxy);

            //以下三行是在需要验证时，输入帐号密码信息
            String headerkey = "Proxy-Authorization";
            String headerValue = "Basic "+ Base64Utils.encodeToString(":tgy-ss-server".getBytes()); //帐号密码用:隔开，base64加密方式
            connection.setRequestProperty(headerkey, headerValue);*/


            URLConnection connection = realUrl.openConnection();

            // 设置通用的请求属性
            if(StringUtils.isNotBlank(cookieval)){
                connection.setRequestProperty("Cookie", cookieval);
            }


            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0(Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
            connection.setUseCaches(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应charset
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();;     //获得网站的二进制数据
            result = new String(getData, url.contains("http://222.22.63.178")?"UTF-8":"gb2312");
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            return "发送GET请求出现异常";
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
     * 获得字符集
     */
    public String getCharset(String siteUrl) throws Exception{
        URL url = new URL(siteUrl);
        Document doc = Jsoup.parse(url, 6*1000);
        Elements elements = doc.select("meta[http-equiv=Content-Type]");
        Matcher matcher = Pattern.compile("(?<=charset=)(.+)(?=\")").matcher(elements.get(0).toString());
        if (matcher.find()){
            return matcher.group();
        }
        return "gb2312";
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
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            if(StringUtils.isNotBlank(cookieval)){
                connection.setRequestProperty("Cookie", cookieval);
            }
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0(Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
//            connection.connect();
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            bos = new ByteArrayOutputStream();
            while((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();;     //获得网站的二进制数据
            result = new String(getData, "gb2312");
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            return "发送POST请求出现异常";
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
