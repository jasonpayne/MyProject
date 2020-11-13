package com.payne.shop.controller;


import cn.wanghaomiao.xpath.model.JXDocument;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.payne.school.mapper.UserMapper;
import com.payne.shop.utils.HttpClientShop;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ShopController 测试
 *
 * @author xinchao.pan 2020-02-04
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    UserMapper userMapper;

    HttpClientShop HttpClient = new HttpClientShop();

    /**
     * // TODO
     * 登陆
     *
     * @param jSONObject
     * @return
     */
    @ApiOperation(value = "登陆", notes = "登陆")
    @RequestMapping(value = "/loginTest", method = RequestMethod.POST)
    @ResponseBody
    public List<String> loginTest(@RequestBody JSONObject jSONObject) {
        String LoginQuery = "https://login.yahoo.co.jp/config/login";
        String LoginParam = "login=" + "zhuangqing1005" + "&passwd=" + "langgan112233*";
        String LoginHtml = HttpClient.sendPostForLogin(LoginQuery, LoginParam, "UTF-8");
        return null;
    }


    /**
     * 登陆
     *
     * @param jSONObject
     * @return
     */
    @ApiOperation(value = "登陆222", notes = "登陆222")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public List<String> login(@RequestBody JSONObject jSONObject) {
        String loginUrl = "https://login.bizmanager.yahoo.co.jp/yidlogin.php?.scrumb=4iCpV5j5Wvv&.done=https%3a%2f%2fpro.store.yahoo.co.jp%2fpro.pp-shop";
        String loginHtml = HttpClient.sendGetForLogin(loginUrl, null, "utf-8");
        return null;
    }

    /**
     * 快递更新信息
     *
     * @param jSONObject
     * @return
     */
    @ApiOperation(value = "快递更新信息", notes = "快递更新信息")
    @RequestMapping(value = "/getTrack", method = RequestMethod.POST)
    @ResponseBody
    public List<String> getTrack(@RequestBody JSONObject jSONObject) {
        List<String> list = new ArrayList<>();
        JSONArray postIds = jSONObject.getJSONArray("postIds");
        for (Object postId : postIds) {
            try {
                String ocsInfoUrl = "http://www2.ocsworldwide.net/ExpTracking/cwbCheck.php?cwbno=" + postId;
                String ocsInfoHtml = HttpClient.sendGet(ocsInfoUrl, null, "UTF-8");
                JXDocument jxDocument = new JXDocument(ocsInfoHtml);
                List<Object> rs = jxDocument.sel("//head/meta/@CONTENT");
                String url = String.valueOf(rs.get(0));
                if (url.contains("sagawa-exp")) { //佐川转运
                    url = url.substring(url.indexOf("http://"));
                    // String url = "http://k2k.sagawa-exp.co.jp/cgi-bin/mall.mmcgi?oku01=8201048564";
                    String zcHtml = HttpClient.sendGet(url, null, "Shift_JIS");
                    JXDocument jxDocumentZc = new JXDocument(zcHtml);
                    List<Object> rs1 = jxDocumentZc.sel("//body/table/tbody/tr");
                    JXDocument jxDocumentZc2 = new JXDocument(rs1.get(1).toString());
                    List<Object> rs3 = jxDocumentZc2.sel("//table/tbody/tr/td[@class='ichiran-fg ichiran-fg-msrc2-2']/text()");
                    String code = rs3.get(0).toString();
                    String date = rs3.get(1).toString().replace("年", "/").replace("月", "/").replace("日", "");
                    list.add(postId.toString() + "--" + date + "--" + code + "--" + "佐川转运");
                } else if (url.contains("SBS_LTRC")) { // SBS转运
                    // https://www.saqura-web.com/SBS_LTRC/KF_5020.aspx?IraNo=37013889990
                    /*url = url.substring(url.indexOf("https://"));
                    String zcHtml = HttpClient.sendGet(url, null);
                    JXDocument jxDocumentZc = new JXDocument(zcHtml);*/
                    list.add(postId.toString() + "--XXXX-XX-XX--" + postId.toString() + "--" + "SBS转运(追跡サービス:https://www.saqura-web.com/SBS_LTRC/KF_5020.aspx?IraNo=" + postId.toString() + ")");
                } else if (url.contains("ocs")) { // ocs
                    /*url = url.substring(url.indexOf("https://"));
                    String zcHtml = HttpClient.sendGet(url, null,"UTF-8");
                    JXDocument jxDocumentZc = new JXDocument(zcHtml);*/
                    list.add(postId.toString() + "--XXXX-XX-XX--" + postId.toString() + "--" + "ocs自运");
                } else {
                    list.add(postId.toString() + "--XXXX-XX-XX--" + postId.toString() + "--" + "未查到");
                }
            } catch (Exception e) {
                list.add(postId.toString() + "未查到" + e.getMessage());
                continue;
            }
        }
        return list;
    }
}
