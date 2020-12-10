package com.payne.shop.controller;


import com.payne.shop.model.LogisticsInfo;
import com.payne.shop.model.OrderInfo;
import com.payne.shop.utils.CSVUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ExcelController 测试
 *
 * @author xinchao.pan 2020-02-04
 */
@RestController
@CrossOrigin
@RequestMapping("/excel")
public class ExcelController {

    @Resource
    private CSVUtils cSVUtils;

    /**
     * 上传EXCEL(spxx文件和mjxx文件)
     */
    @ApiOperation(value = "上传spxx文件和mjxx文件", notes = "上传spxx文件和mjxx文件")
    @RequestMapping(value = "/uploadOrderExcel", method = RequestMethod.POST)
    public Object uploadOrderExcel(HttpServletResponse response, MultipartFile file1, MultipartFile file2) {
        List<OrderInfo> orderlist = new ArrayList<>();
        try {
            orderlist = cSVUtils.uploadFile(file1, file2);
            String[] head = {"订单日期", "订单号", "sku", "数量", "发货日期", "OCS", "黑猫", "邮编", "收件人地址", "收件人", "电话号码"};
            File orderFile = cSVUtils.makeTempCSV("OrderList", head, orderlist);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date();
            String newFileName = "OrderList-" + sdf.format(currentDate) + ".csv";
            Boolean flag = cSVUtils.downloadFile(response, orderFile, newFileName);
        } catch (Exception e) {
            e.getMessage();
        }
        return orderlist;
    }

    /**
     * 上传SHA2000083文件
     */
    @ApiOperation(value = "上传SHA2000083文件", notes = "上传SHA2000083文件")
    @RequestMapping(value = "/uploadLogistics", method = RequestMethod.POST)
    public Map<String, Integer> uploadLogistics(MultipartFile file) {
        return cSVUtils.uploadFileLogistics(file);
    }

    /**
     * 上传synchro-express-info文件同步快递号信息
     */
    @ApiOperation(value = "上传order-temple文件同步快递号信息", notes = "上传order-temple文件同步快递号信息")
    @RequestMapping(value = "/synchroExpress", method = RequestMethod.POST)
    public Object synchroExpress(HttpServletResponse response, MultipartFile file) {
        List<LogisticsInfo> expressList = new ArrayList<>();
        try {
            expressList = cSVUtils.synchroExpress(file);
            String[] head = {"订单号", "sku", "数量", "发货日期", "OCS", "黑猫", "运单详情"};
            File orderFile = cSVUtils.makeTempCSVExpress("expressList", head, expressList);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date();
            String newFileName = "ExpressList-" + sdf.format(currentDate) + ".csv";
            Boolean flag = cSVUtils.downloadFile(response, orderFile, newFileName);
        } catch (Exception e) {
            e.getMessage();
        }
        return expressList;
    }

}
