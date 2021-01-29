package com.payne.shop.controller;


import com.payne.shop.entity.LogisticsInfo;
import com.payne.shop.model.OrderInfo;
import com.payne.shop.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private ExcelService excelService;

    @Value("${document.path}")
    private String path;

    /**
     * 上传spxx文件和mjxx文件生产订单数据
     */
//    @ApiOperation(value = "上传spxx文件和mjxx文件生产订单数据", notes = "上传spxx文件和mjxx文件生产订单数据")
//    @RequestMapping(value = "/createOrderInfo", method = RequestMethod.POST)
//    public Object createOrderInfo(MultipartFile file1, MultipartFile file2) {
//        List<OrderInfo> orderlist = new ArrayList<>();
//        try {
//            orderlist = excelService.uploadFile(file1, file2);
//            if (CollectionUtils.isEmpty(orderlist)) {
//                return "没有新增订单";
//            }
//            // 生成雅虎同步快递号文件
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String dateSuffix = sdf.format(new Date());
//            excelService.createOrderFileXlsx("OrderList-" + dateSuffix + ".xlsx", orderlist);
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        return orderlist.size();
//    }

    /**
     * 上传SHA2000083文件同步快递信息
     */
//    @ApiOperation(value = "上传SHA2000083文件同步快递信息", notes = "上传SHA2000083文件同步快递信息")
//    @RequestMapping(value = "/logisticsSynchron", method = RequestMethod.POST)
//    public Map<String, Integer> logisticsSynchron(MultipartFile file) {
//        return excelService.uploadFileLogistics(file);
//    }

    /**
     * 上传ExpressList.xlsx文件输出钉钉和雅虎(odstats_order.csv)同步文件
     */
//    @ApiOperation(value = "上传ExpressList文件输出钉钉和雅虎同步快递号文件", notes = "上传ExpressList文件输出钉钉和雅虎同步快递号文件")
//    @RequestMapping(value = "/synchronExpress", method = RequestMethod.POST)
//    public Object synchronExpress(MultipartFile file) {
//        List<LogisticsInfo> expressList = new ArrayList<>();
//        try {
//            Boolean flag = excelService.synchroExpress(file, expressList);
//            if (flag) {
//                // 生成钉钉同步快递号文件
//                excelService.makeTempExcelExpress("ExpressList" + ".xlsx", expressList);
//                // 生成雅虎同步快递号文件
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                String yahooFileSuffix = sdf.format(new Date());
//                excelService.makeYahooExpress("odstats_order-" + yahooFileSuffix + ".csv", expressList);
//                return expressList.size();
//            } else {
//                return "转运单号无更新";
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        return null;
//    }


    /**
     * 自动生产订单数据
     */
    @ApiOperation(value = "自动生产订单数据", notes = "自动生产订单数据")
    @RequestMapping(value = "/createOrderList", method = RequestMethod.GET)
    public Object createOrderInfo() {
        List<OrderInfo> orderlist = new ArrayList<>();
        try {
            // 文件1
            File file1 = new File(path + "mjxx.csv");
            // 文件2
            File file2 = new File(path + "spxx.csv");
            if (!file1.exists() && !file2.exists()) {
                return "缺少mjxx.csv或者spxx.csv文件";
            }
            orderlist = excelService.uploadFile(file1, file2);
            if (CollectionUtils.isEmpty(orderlist)) {
                return "没有新增订单";
            }
            // 生成雅虎同步快递号文件
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateSuffix = sdf.format(new Date());
            excelService.createOrderFileXlsx("OrderList-" + dateSuffix + ".xlsx", orderlist);
        } catch (Exception e) {
            e.getMessage();
        }
        return orderlist.size();
    }

    /**
     * 自动同步SHA2000083文件快递信息
     */
    @ApiOperation(value = "自动同步SHA2000083文件快递信息", notes = "自动同步SHA2000083文件快递信息")
    @RequestMapping(value = "/logisticsSynchron", method = RequestMethod.GET)
    public Object logisticsSynchron() {
        List<Map<String, Integer>> list = new ArrayList<>();
        File fileList = new File(path);
        for (File file : fileList.listFiles()) {
            if (file.isFile() && file.getName().contains("SHA2000083")) {
                list.add(excelService.uploadFileLogistics(file));
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        } else {
            return "缺少SHA2000083文件";
        }
    }

    /**
     * 上传ExpressList.xlsx文件输出钉钉和雅虎(odstats_order.csv)同步文件
     */
    @ApiOperation(value = "上传ExpressList文件输出钉钉和雅虎同步快递号文件", notes = "上传ExpressList文件输出钉钉和雅虎同步快递号文件")
    @RequestMapping(value = "/synchronExpress", method = RequestMethod.GET)
    public Object synchronExpress() {
        File file = new File(path + "ExpressList.xlsx");
        if (!file.exists()) {
            return "缺少ExpressList.xlsx文件";
        }
        List<LogisticsInfo> expressList = new ArrayList<>();
        try {
            Boolean flag = excelService.synchroExpress(file, expressList);
            if (flag) {
                // 生成钉钉同步快递号文件
                excelService.makeTempExcelExpress("ExpressList" + ".xlsx", expressList);
                // 生成雅虎同步快递号文件
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String yahooFileSuffix = sdf.format(new Date());
                excelService.makeYahooExpress("odstats_order-" + yahooFileSuffix + ".csv", expressList);
                return expressList.size();
            } else {
                return "转运单号无更新";
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

}
