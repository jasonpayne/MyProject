package com.payne.shop.controller;


import com.payne.school.dao.mapper.*;

import com.payne.school.service.QuestionService;
import com.payne.shop.utils.HttpClientTest;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * QuestionController 测试
 *
 * @author xinchao.pan 2020-02-04
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    UserMapper userMapper;

    HttpClientTest HttpClient = new HttpClientTest();

    @RequestMapping(value = "/getTrack", method = RequestMethod.GET)
    public String register() {
        try {
            /*String examineDetailUrl = "http://www2.ocsworldwide.net/ExpTracking/cwbCheck.php?cwbno=37013035953";
            String examineDetailHtml = HttpClient.sendGet(examineDetailUrl, null);
            String examineDetailUrl2 = "https://www.saqura-web.com/SBS_LTRC/?number01=37013035953";
            String examineDetailHtml2 = HttpClient.sendGet(examineDetailUrl2, null);

            Document document = Jsoup.parse(examineDetailHtml);
            Document document2 = Jsoup.parse(examineDetailHtml2);*/

            String examineDetailUrl = "http://www2.ocsworldwide.net/ExpTracking/cwbCheck.php?cwbno=37013061120";
            String examineDetailHtml = HttpClient.sendGet(examineDetailUrl, null);
            String examineDetailUrl2 = "http://k2k.sagawa-exp.co.jp/cgi-bin/mall.mmcgi?oku01=8201048564";
            String examineDetailHtml2 = HttpClient.sendGet(examineDetailUrl2, null);

            Document document = Jsoup.parse(examineDetailHtml);
            Document document2 = Jsoup.parse(examineDetailHtml2);


        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }


}
