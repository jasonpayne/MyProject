package com.payne.shop.utils;


import com.payne.shop.entity.LogisticsInfo;
import com.payne.shop.entity.ShopOrder;
import com.payne.shop.mapper.LogisticsInfoMapper;
import com.payne.shop.mapper.ShopOrderMapper;
import com.payne.shop.model.OrderInfo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Author: Denebola
 * @Date: 2019/7/18-16:48
 * @Description: CSV工具类
 **/
@Service
public class CSVUtils {

    @Resource
    private LogisticsInfoMapper logisticsInfoMapper;

    @Resource
    private ShopOrderMapper shopOrderMapper;

    private static Logger logger = LoggerFactory.getLogger(CSVUtils.class);
    //行尾分隔符定义
    private final static String NEW_LINE_SEPARATOR = "\n";
    //上传文件的存储位置(本地服务器)
    private final static String PATHSTR = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    //上传文件的存储位置(Linux服务器)
//    private final static String PATHSTR = "/root/payne/";

    private static Boolean flag = false;

    /**
     * @return File
     * @Description 创建CSV文件
     * @Param fileName 文件名，head 表头，values 表体
     **/
    public File makeTempCSV(String fileName, List<OrderInfo> orderlist) throws IOException {
//        创建文件
//        File file = File.createTempFile(fileName, ".csv", new File(PATHSTR));
        File file = new File("/Users/payne/Downloads/" + fileName);
        if (file.exists()) {
            file.delete();
        }
        CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        CSVPrinter printer = new CSVPrinter(bufferedWriter, formator);

//      写入表头
        String[] head = {"订单日期", "订单号", "sku", "数量", "发货日期", "OCS", "黑猫", "邮编", "收件人地址", "收件人", "电话号码"};
        printer.printRecord(head);
        List<String[]> values = new ArrayList<>();
        String orderId = "";
        Boolean repeatOrderFlag = true;
        for (OrderInfo orderInfo : orderlist) {
            if (StringUtils.isNotBlank(orderId) && orderInfo.getOrderId().equals(orderId)) {
                repeatOrderFlag = false;
            } else {
                repeatOrderFlag = true;
            }
            orderId = orderInfo.getOrderId();
            String[] orderArr = {
                    repeatOrderFlag ? orderInfo.getOrderTime() : "",
                    repeatOrderFlag ? orderInfo.getOrderId() : "",
                    orderInfo.getSkuId(), orderInfo.getQuantity(),
                    "", "", "",
                    repeatOrderFlag ? orderInfo.getZipCode() : "",
                    repeatOrderFlag ? orderInfo.getAddress() : "",
                    repeatOrderFlag ? orderInfo.getShipName() : "",
                    repeatOrderFlag ? orderInfo.getPhone() : ""};
            values.add(orderArr);
        }
        if (orderlist.get(0).getRepeatFlag()) {
            String[] orderExplain = {"根据收货人或者电话号码判断出存在一起发货的订单"};
            values.add(orderExplain);
        }
//      写入内容
        for (String[] value : values) {
            printer.printRecord(value);
        }
        printer.close();
        bufferedWriter.close();
        return file;
    }

    /**
     * @return boolean
     * @Description 下载文件
     * @Param response，file
     **/
    public boolean downloadFile(HttpServletResponse response, File file, String newFileName) {
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
        response.setHeader("Content-Disposition", "attachment;fileName=" + newFileName);
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        OutputStream os = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            os = response.getOutputStream();
            //MS产本头部需要插入BOM
            //如果不写入这几个字节，会导致用Excel打开时，中文显示乱码
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            byte[] buffer = new byte[1024];
            int i = bufferedInputStream.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bufferedInputStream.read(buffer);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            //关闭流
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file.delete();
        }
        return false;
    }

    /**
     * @return File
     * @Description 上传文件
     * @Param multipartFile
     **/
    public List<OrderInfo> uploadFile(MultipartFile multipartFile1, MultipartFile multipartFile2) {
        File file1 = null;
        File file2 = null;
        try {
            // 文件1
            String fileName1 = multipartFile1.getOriginalFilename();
//            String path1 = PATH.getPath() + fileName1;
            String path1 = PATHSTR + fileName1;
            logger.info(path1);
            file1 = new File(path1);
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }
            multipartFile1.transferTo(file1);

            // 文件2
            String fileName2 = multipartFile2.getOriginalFilename();
//            String path2 = PATH.getPath() + fileName2;
            String path2 = PATHSTR + fileName2;
            file2 = new File(path2);
            if (!file2.getParentFile().exists()) {
                file2.getParentFile().mkdirs();
            }
            multipartFile2.transferTo(file2);
            List<OrderInfo> spxxList = new ArrayList();
            List<OrderInfo> mjxxList = new ArrayList();
            if (fileName1.contains("spxx")) {
                spxxList = readCSV(file1.getPath());
                mjxxList = readCSV(file2.getPath());
            } else {
                mjxxList = readCSV(file1.getPath());
                spxxList = readCSV(file2.getPath());
            }
            // 根据收货人或者电话号码判断是否能一起发货 || 是否是新进来订单
            List<String> oldOrderIds = new ArrayList<>();
            Set<String> shipNameSet = new HashSet<>();
            Set<String> iphoneSet = new HashSet<>();
            for (int i = 0; i < mjxxList.size(); i++) {
                String orderId = mjxxList.get(i).getOrderId();
                ShopOrder orderModel = shopOrderMapper.load(orderId);
                if (orderModel != null) {
                    oldOrderIds.add(orderId);
                    mjxxList.remove(i);
                    i--;
                } else {
                    iphoneSet.add(mjxxList.get(i).getShipName());
                    shipNameSet.add(mjxxList.get(i).getPhone());
                    ShopOrder shopOrder = new ShopOrder();
                    shopOrder.setOrderId(orderId);
                    shopOrder.setOrderTime(mjxxList.get(i).getOrderTime());
                    shopOrder.setPostCode(mjxxList.get(i).getZipCode());
                    shopOrder.setAddress(mjxxList.get(i).getAddress());
                    shopOrder.setConsignee(mjxxList.get(i).getShipName());
                    shopOrder.setIphone(mjxxList.get(i).getPhone());
                    shopOrderMapper.insert(shopOrder);
                }
            }
            for (int i = 0; i < spxxList.size(); i++) {
                if (oldOrderIds.contains(spxxList.get(i).getOrderId())) {
                    spxxList.remove(i);
                    i--;
                    continue;
                }
                for (OrderInfo mjxx : mjxxList) {
                    if (spxxList.get(i).getOrderId().equals(mjxx.getOrderId())) {
                        spxxList.get(i).setZipCode(mjxx.getZipCode());
                        spxxList.get(i).setAddress(mjxx.getAddress());
                        spxxList.get(i).setShipName(mjxx.getShipName());
                        spxxList.get(i).setPhone(mjxx.getPhone());
                        spxxList.get(i).setOrderTime(mjxx.getOrderTime());
                    }
                }
            }
            spxxList.sort(Comparator.comparing(OrderInfo::getOrderId));
            if (shipNameSet.size() < mjxxList.size() || iphoneSet.size() < mjxxList.size()) {
                spxxList.get(0).setRepeatFlag(true);
            }
            return spxxList;
        } catch (Exception e) {
            logger.error("上传文件失败" + e.getMessage(), e);
            return null;
        } finally {
            file1.delete();
            file2.delete();
        }
    }

    /**
     * @return List<List < String>>
     * @Description 读取spxx和mjxx文件的内容（不含表头）
     * @Param filePath 文件存储路径，colNum 列数
     **/
    public static List<OrderInfo> readCSV(String filePath) {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filePath);
            Charset charset = Charset.forName("Shift_JIS");
            inputStreamReader = new InputStreamReader(fileInputStream, charset);
            bufferedReader = new BufferedReader(inputStreamReader);

            CSVParser parser = CSVFormat.DEFAULT.parse(bufferedReader);
//          表内容集合，外层List为行的集合，内层List为字段集合
            List<OrderInfo> values = new ArrayList<>();
            int rowIndex = 0;
            for (CSVRecord record : parser.getRecords()) {
//              跳过表头
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
//              每行的内容
                OrderInfo orderInfo = new OrderInfo();
                if (filePath.contains("spxx")) {
                    // 订单ID
                    orderInfo.setOrderId(record.get(0));
                    // 商品ID
                    orderInfo.setSkuId(StringUtils.isNotBlank(record.get(3)) ? record.get(3) : record.get(2));
                    // 数量
                    orderInfo.setQuantity(record.get(4));
                    // 是否存在附带商品
                    if (StringUtils.isNotBlank(orderInfo.getSkuId()) && orderInfo.getSkuId().contains("bd1-")) {
                        if (record.get(6).contains("800")) {
                            orderInfo.setSkuId(orderInfo.getSkuId() + "(是套餐)");
                        } else {
                            orderInfo.setSkuId(orderInfo.getSkuId() + "(不是套餐)");
                        }
                    }
                } else if (filePath.contains("mjxx")) {
                    // 订单ID
                    orderInfo.setOrderId(record.get(0));
                    // 邮编
                    String zipCode = record.get(5).replace("-", "");
                    if (zipCode.length() != 7) {
                        zipCode = String.format("%07d", Integer.valueOf(zipCode));
                    }
                    zipCode = zipCode.substring(0, zipCode.length() - 4) + "-" + zipCode.substring(zipCode.length() - 4);
                    orderInfo.setZipCode(zipCode);
                    // 地址
                    String address = record.get(6) + record.get(7) + record.get(8) + record.get(9);
                    if (StringUtils.isNotBlank(record.get(11))) {
                        address = address + "（" + record.get(11) + "）";
                    }
                    orderInfo.setAddress(address);
                    // 联系人
                    orderInfo.setShipName(record.get(1) + record.get(2));
                    // 电话
                    String phone = record.get(10);
                    phone = phone.replace("-", "");
                    if (phone.length() == 9) {
                        phone = String.format("%010d", Long.valueOf(phone));
                    } else if (phone.length() == 10 && !phone.substring(0, 1).equals("0")) {
                        phone = String.format("%011d", Long.valueOf(phone));
                    }
                    phone = phone.substring(0, phone.length() - 8) + "-" + phone.substring(phone.length() - 8, phone.length() - 4) + "-" + phone.substring(phone.length() - 4);
                    orderInfo.setPhone(phone);
                    // 订单时间
                    String orderTime = record.get(14);
                    orderInfo.setOrderTime(orderTime.substring(0, orderTime.indexOf(" ")));
                }
                values.add(orderInfo);
                rowIndex++;
            }
            return values;
        } catch (IOException e) {
            logger.error("解析CSV内容失败" + e.getMessage(), e);
        } finally {
            //关闭流
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * @return File
     * @Description 上传文件
     * @Param multipartFile
     **/
    public Map<String, Integer> uploadFileLogistics(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            String path = PATHSTR + fileName;
            logger.info(path);
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            multipartFile.transferTo(file);
            Map<String, Integer> map = readCSVLogistics(file.getPath());
            file.delete();
            return map;
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * @return List<List < String>>
     * @Description 读取CSV文件的内容（不含表头）
     * @Param filePath 文件存储路径，colNum 列数
     **/
    public Map<String, Integer> readCSVLogistics(String filePath) {
        Map<String, Integer> map = new HashMap<>();
        try {
            Integer insertCount = 0;
            Integer updateCount = 0;
            FileInputStream fileInputStream = new FileInputStream(filePath);
            HSSFWorkbook sheets = new HSSFWorkbook(fileInputStream);
            HSSFSheet sheetAt = sheets.getSheetAt(0);
            for (int rowNum = 1; rowNum <= sheetAt.getLastRowNum(); rowNum++) {
                HSSFRow row = sheetAt.getRow(rowNum);
                LogisticsInfo logisticsInfo = new LogisticsInfo();
                logisticsInfo.setInTranCode(String.valueOf(row.getCell(0)));// OCS运单号
                logisticsInfo.setOutTranCode(String.valueOf(row.getCell(6)));// 转运订单号
                logisticsInfo.setObtainTime(String.valueOf(row.getCell(4)));// 取件时间
                logisticsInfo.setTranDetail(String.valueOf(row.getCell(5)));// 运单详情
                logisticsInfo.setSignTime(String.valueOf(row.getCell(8)));// 签收时间
                LogisticsInfo model = logisticsInfoMapper.load(logisticsInfo.getInTranCode());
                if (null != model) {
                    logisticsInfo.setId(model.getId());
                    logisticsInfoMapper.update(logisticsInfo);
                    updateCount++;
                } else {
                    logisticsInfoMapper.insert(logisticsInfo);
                    insertCount++;
                }
            }
            map.put("insert", insertCount);
            map.put("update", updateCount);
            //关闭workbook
            sheets.close();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @return File
     * @Description 上传文件
     * @Param multipartFile
     **/
    public List<LogisticsInfo> synchroExpress(MultipartFile multipartFile) {
        File file = null;
        try {
            String fileName = multipartFile.getOriginalFilename();
            String path = PATHSTR + fileName;
            logger.info(path);
            file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            multipartFile.transferTo(file);
            List<LogisticsInfo> list = readExcelExpress(file.getPath());
            return list;
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage(), e);
        } finally {
            file.delete();
        }
        return null;
    }

    /**
     * @return List<List < String>>
     * @Description 读取CSV文件的内容（不含表头）
     * @Param filePath 文件存储路径，colNum 列数
     **/
    public List<LogisticsInfo> readExcelExpress(String filePath) {
        List<LogisticsInfo> list = new ArrayList<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheetAt = sheets.getSheetAt(0);
            for (int rowNum = 0; rowNum <= sheetAt.getLastRowNum(); rowNum++) {
                XSSFRow row = sheetAt.getRow(rowNum);
                if (null == row) {
                    continue;
                }
                LogisticsInfo logisticsInfo = new LogisticsInfo();
                if (row.getCell(5) != null) {
                    row.getCell(5).setCellType(CellType.STRING);
                    logisticsInfo.setInTranCode(String.valueOf(row.getCell(5)));// OCS运单号
                }
                if (row.getCell(0) != null) {
                    row.getCell(0).setCellType(CellType.STRING);
                    logisticsInfo.setOrderTime(String.valueOf(row.getCell(0)));// 订单时间
                }
                if (row.getCell(1) != null) {
                    row.getCell(1).setCellType(CellType.STRING);
                    logisticsInfo.setOrderCode(String.valueOf(row.getCell(1)));// 订单号
                }
                if (row.getCell(2) != null) {
                    row.getCell(2).setCellType(CellType.STRING);
                    logisticsInfo.setSkuId(String.valueOf(row.getCell(2)));// sku_id
                }
                if (row.getCell(3) != null) {
                    row.getCell(3).setCellType(CellType.STRING);
                    logisticsInfo.setQuantity(String.valueOf(row.getCell(3)));// 数量
                }
                if (StringUtils.isNotBlank(String.valueOf(row.getCell(5)))) {
                    LogisticsInfo model = logisticsInfoMapper.load(logisticsInfo.getInTranCode());
                    if (null != model) {
                        logisticsInfo.setId(model.getId());
                        logisticsInfoMapper.update(logisticsInfo);
                        if (StringUtils.isNotBlank(model.getObtainTime())) {
                            logisticsInfo.setObtainTime(model.getObtainTime().substring(0, model.getObtainTime().indexOf(" ")));
                        }
                        if (StringUtils.isNotBlank(model.getOutTranCode())) {
                            flag = true;
                        }
                        logisticsInfo.setOutTranCode(model.getOutTranCode());
                        logisticsInfo.setTranDetail(model.getTranDetail());
                    }
                }
                if (StringUtils.isNotBlank(logisticsInfo.getSkuId())) {
                    list.add(logisticsInfo);
                }
            }
            //关闭workbook
            sheets.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public File makeTempExcelExpress(String fileName, String[] head, List<LogisticsInfo> expressList) throws IOException {
        try {
            //创建工作簿
            XSSFWorkbook wb = new XSSFWorkbook();
            //创建 Sheet页
            Sheet sheetA = wb.createSheet("A");
            for (int i = 0; i < expressList.size(); i++) {
                //创建单元行
                Row row = sheetA.createRow(i);
                LogisticsInfo logisticsInfo = expressList.get(i);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(logisticsInfo.getOrderTime());

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(logisticsInfo.getOrderCode());

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(logisticsInfo.getSkuId());

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(logisticsInfo.getQuantity());

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(logisticsInfo.getObtainTime());

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(logisticsInfo.getInTranCode());

                Cell cell6 = row.createCell(6);
                if (StringUtils.isNotBlank(logisticsInfo.getOutTranCode())) {
                    cell6.setCellValue(logisticsInfo.getOutTranCode() + "\t");
                } else {
                    cell6.setCellValue(logisticsInfo.getOutTranCode());
                }
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(logisticsInfo.getTranDetail());
                /*String[] orderArr = {logisticsInfo.getOrderCode(), logisticsInfo.getSkuId(), logisticsInfo.getQuantity(),
                        logisticsInfo.getObtainTime(), logisticsInfo.getInTranCode(),
                        StringUtils.isNotBlank(logisticsInfo.getOutTranCode()) ? logisticsInfo.getOutTranCode() + "\t" : logisticsInfo.getOutTranCode(),
                        logisticsInfo.getTranDetail()};*/
            }
            //路径需要存在
            FileOutputStream fos = new FileOutputStream(PATHSTR + fileName + ".xls");
            wb.write(fos);
            fos.close();
            wb.close();
            System.out.println("写数据结束！");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void makeTempCSVExpress(String fileName, List<LogisticsInfo> expressList) throws IOException {
        if (flag) {
//        创建文件
//        File file = File.createTempFile(fileName, ".csv", new File(PATHSTR));
            File file = new File("/Users/payne/Downloads/" + fileName);
            if (file.exists()) {
                file.delete();
            }
            CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            CSVPrinter printer = new CSVPrinter(bufferedWriter, formator);

            String[] head = {"订单日期", "订单号", "sku", "数量", "发货日期", "OCS", "运单号", "运单详情"};
            printer.printRecord(head);
            List<String[]> values = new ArrayList<>();
            for (LogisticsInfo logisticsInfo : expressList) {
                if (isChinese(logisticsInfo.getOrderTime())) {
                    continue;
                }
                String[] orderArr = {logisticsInfo.getOrderTime(), logisticsInfo.getOrderCode(), logisticsInfo.getSkuId(),
                        logisticsInfo.getQuantity(), logisticsInfo.getObtainTime(), logisticsInfo.getInTranCode(),
                        StringUtils.isNotBlank(logisticsInfo.getOutTranCode()) ? logisticsInfo.getOutTranCode() + "\t" : logisticsInfo.getOutTranCode(),
                        logisticsInfo.getTranDetail()};
                values.add(orderArr);
            }
//      写入内容
            for (String[] value : values) {
                printer.printRecord(value);
            }
            printer.close();
            bufferedWriter.close();
        }

    }

    public void makeYahooExpress(String fileName, List<LogisticsInfo> expressList) throws IOException {
        if (flag) {
            //        创建文件
//        File file = File.createTempFile(fileName, ".csv", new File(PATHSTR));
            File file = new File("/Users/payne/Downloads/" + fileName);
            if (file.exists()) {
                file.delete();
            }
            CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            CSVPrinter printer = new CSVPrinter(bufferedWriter, formator);

            String[] headYahoo = {"OrderId", "ShipMethod", "ShipCompanyCode", "ShipInvoiceNumber1", "ShipDate", "ShipStatus"};
            printer.printRecord(headYahoo);
            List<String[]> values = new ArrayList<>();
            for (LogisticsInfo logisticsInfo : expressList) {
                if (isChinese(logisticsInfo.getOrderTime()) || StringUtils.isBlank(logisticsInfo.getOutTranCode())) {
                    continue;
                }
                String ShipCompanyCode = "";
                if (logisticsInfo.getOutTranCode().length() == 12 && "299".equals(logisticsInfo.getOutTranCode().substring(0, 3))) {
                    ShipCompanyCode = "1001";
                } else {
                    ShipCompanyCode = "1002";
                }
                String[] orderArr = {
                        logisticsInfo.getOrderCode(),
                        "postage1",
                        ShipCompanyCode,
                        logisticsInfo.getOutTranCode() + "\t",
                        logisticsInfo.getObtainTime(),
                        "3"
                };
                values.add(orderArr);
            }
            //      写入内容
            for (String[] value : values) {
                printer.printRecord(value);
            }
            printer.close();
            bufferedWriter.close();
        }
    }


    // 判断一个字符串是否含有中文
    public boolean isChinese(String str) {
        if (str == null) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (isChinese(c)) {
                return true;// 有一个中文字符就返回
            }
        }
        return false;
    }

    // 判断一个字符是否是中文
    public boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }
}