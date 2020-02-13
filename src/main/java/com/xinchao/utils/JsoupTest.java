//package com.xinchao.utils;
//
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.Unirest;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.net.URL;
//import java.net.URLConnection;
//
//public class JsoupTest {
//
//    Elements elements;
//
//    public  String sendGet(String url, String param) {
//        String result = "";
//        ByteArrayOutputStream bos = null;
//        try {
//            String urlNameString =  url;
//            if(param != null && param != ""){
//                urlNameString = "?" + param;
//            }
//            URL realUrl = new URL(urlNameString);
//            // 打开和URL之间的连接
//            URLConnection connection = realUrl.openConnection();
//            // 设置通用的请求属性
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//            // 定义 BufferedReader输入流来读取URL的响应
//            InputStream inputStream = connection.getInputStream();
//            byte[] buffer = new byte[1024];
//            int len = 0;
//            bos = new ByteArrayOutputStream();
//            while((len = inputStream.read(buffer)) != -1) {
//                bos.write(buffer, 0, len);
//            }
//            byte[] getData = bos.toByteArray();;     //获得网站的二进制数据
//            result = new String(getData, "gb2312");
//
//            Document document = Jsoup.parse(result);
//            elements = document.select("div.article-list");
//            elements = elements.first().children().select("div:not(div[style=display: none;])").remove();
//            elements = elements.select("h4>a");
//
//        } catch (Exception e) {
//            System.out.println("发送GET请求出现异常！" + e);
//            e.printStackTrace();
//        }
//        // 使用finally块来关闭输入流
//        finally {
//            try {
//                if (bos != null) {
//                    bos.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }
//
//    public void getAllPageTitle()  {
//        int page =1 ;
//        while(true) {
//            HttpResponse<String> response = Unirest.get("https://blog.csdn.net/deng214/article/month/2018/05/" + page + "?").asString();
//            String body = response.getBody();
//
//            Document document = Jsoup.parse(body);
//            elements = document.select("div.article-list");
//            elements = elements.first().children().select("div:not(div[style=display: none;])").remove();
//            elements = elements.select("h4>a");
//            int size = elements.size();
//
//            System.out.println("-------------------- 第" + page + "页,显示"+size+"条数据-------------------- ");
//
//            if (size == 0) {
//                break;
//            }
//            if (size < 20 && size > 0) {
//                getElement();
//                break;
//            } else {
//                getElement();
//                page++;
//            }
//
//        }
//    }
//
//    public void getElement(){
//        for (int i = 0; i < elements.size(); i++) {
//            Element element = elements.get(i);
//            String href = element.attr("href");
//            String text = element.text();
//            System.out.println("标题："+text+"\t 链接："+href);
//        }
//    }
//
//}