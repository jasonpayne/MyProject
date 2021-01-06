package com.payne.shop.controller;


import com.payne.shop.entity.LogisticsInfo;
import com.payne.shop.model.OrderInfo;
import com.payne.shop.utils.CSVUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
     * 上传spxx文件和mjxx文件生产订单数据
     */
    @ApiOperation(value = "上传spxx文件和mjxx文件生产订单数据", notes = "上传spxx文件和mjxx文件生产订单数据")
    @RequestMapping(value = "/createOrderInfo", method = RequestMethod.POST)
    public Object createOrderInfo(HttpServletResponse response, MultipartFile file1, MultipartFile file2) {
        List<OrderInfo> orderlist = new ArrayList<>();
        try {
            orderlist = cSVUtils.uploadFile(file1, file2);
            if (CollectionUtils.isEmpty(orderlist)) {
                return "没有新增订单";
            }
            // 生成雅虎同步快递号文件
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateSuffix = sdf.format(new Date());
            cSVUtils.makeTempCSV("OrderList-" + dateSuffix + ".csv", orderlist);

//            File orderFile = cSVUtils.makeTempCSV("OrderList", head, orderlist);
            /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date();
            String newFileName = "OrderList-" + sdf.format(currentDate) + ".csv";
            Boolean flag = cSVUtils.downloadFile(response, orderFile, newFileName);*/
        } catch (Exception e) {
            e.getMessage();
        }
        return orderlist.size();
    }

    /**
     * 上传SHA2000083文件同步快递信息
     */
    @ApiOperation(value = "上传SHA2000083文件同步快递信息", notes = "上传SHA2000083文件同步快递信息")
    @RequestMapping(value = "/logisticsSynchron", method = RequestMethod.POST)
    public Map<String, Integer> logisticsSynchron(MultipartFile file) {
        return cSVUtils.uploadFileLogistics(file);
    }

    /**
     * 上传order-temple文件输出钉钉和雅虎(odstats_order.csv)同步文件
     */
    @ApiOperation(value = "上传order-temple文件输出钉钉和雅虎同步快递号文件", notes = "上传order-temple文件输出钉钉和雅虎同步快递号文件")
    @RequestMapping(value = "/synchronExpress", method = RequestMethod.POST)
    public Object synchronExpress(HttpServletResponse response, MultipartFile file) {
        List<LogisticsInfo> expressList = new ArrayList<>();
        try {
            expressList = cSVUtils.synchroExpress(file);
            // 生成钉钉同步快递号文件cSVUtils.makeTempExcelExpress("expressList", head, expressList);
            String expressFileSuffix = expressList.get(expressList.size() - 1).getOrderCode();
            cSVUtils.makeTempCSVExpress("ExpressList-" + expressFileSuffix + ".csv", expressList);
            // 生成雅虎同步快递号文件
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String yahooFileSuffix = sdf.format(new Date());
            cSVUtils.makeYahooExpress("odstats_order-" + yahooFileSuffix + ".csv", expressList);

        } catch (Exception e) {
            e.getMessage();
        }
        return expressList.size();
    }

}
