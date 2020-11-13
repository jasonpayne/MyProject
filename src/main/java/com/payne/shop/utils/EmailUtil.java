//package com.payne.shop.utils;
//
//public class EmailUtil {
//
//    @Override
//    public boolean receiveEmail() throws Exception {
//        /**
//         * 1. 取得链接
//         */
//        IMAPStore imapStore = getConnect();
//        if (null == imapStore) {
//            Logs.error("获取邮箱imap连接失败。方法执行失败。退出当前方法！！！！！！！");
//            return false;
//        }
//        Folder folder = imapStore.getFolder("INBOX");
//        folder.open(Folder.READ_WRITE);
//        Message[] messages = folder.getMessages(folder.getMessageCount() - folder.getUnreadMessageCount() + 1, folder.getMessageCount());
//        Logs.info("未读邮件数量:　{}", messages.length);
//        for (Message msg : messages) {
//            try {
//                //邮件发送日期超过两天 退出遍历方法
//                if (outDays(msg)) {
//                    Logs.info("邮件{}已过期，退出循环", msg.getSubject());
//                    continue;
//                }
//                //不是最新邮件，跳过
//                if (isNew(msg)) {
//                    Logs.info("邮件不是新邮件，跳过");
//                    continue;
//                }
//                //是否属于需要处理的邮件
//                if (!needProcessEmail(msg)) {
//                    Logs.info("邮件不需要处理，跳过");
//                    continue;
//                }
//                /**
//                 * 处理邮件信息
//                 */
//                processEmailData(msg);
//            } catch (Exception e) {
//                Logs.error(e.getMessage(), e);
//            }
//        }
//        //关闭资源
//        folder.close(true);
//        imapStore.close();
//        return false;
//    }
//
//    /**
//     * @param
//     * @param msg
//     * @return boolean
//     * @author FeianLing
//     * @date 2019/8/20
//     * @desc 检查当前邮件是否已超过1天, 接收时间大于1天以前 返回true
//     */
//    private boolean outDays(Message msg) throws MessagingException {
//        Date date = DateUtils.offsetDate(new Date(), emailOutDays);
//        Date receiveDate = msg.getReceivedDate();
//        Logs.info("邮件接收时间：{}", DateUtils.formatYmdHms(receiveDate));
//        return receiveDate.getTime() - date.getTime() < 0;
//    }
//
//    /**
//     * @param
//     * @param msg
//     * @return boolean
//     * @author FeianLing
//     * @date 2019/8/20
//     * @desc 判断邮件是否是新的邮件
//     */
//    private boolean isNew(Message msg) throws MessagingException {
//        boolean isNewFlag = false;
//        Flags flags = msg.getFlags();
//        Flags.Flag[] flagsArr = flags.getSystemFlags();
//        Logs.info("邮件状态：" + JSONObject.toJSONString(flagsArr));
//        for (Flags.Flag flag : flagsArr) {
//            if (flag == Flags.Flag.SEEN) {
//                isNewFlag = true;
//                Logs.info("当前邮件为未读状态！");
//                break;
//            }
//        }
//        return isNewFlag;
//    }
//
//    /**
//     * @param
//     * @param msg
//     * @return boolean
//     * @author FeianLing
//     * @date 2019/8/20
//     * @desc 检查邮件内容是否需要我们处理
//     * 1. 检查发件人是否满足要求
//     * 2. 检查是否包含附件
//     * 3. 检查是否有满足条件的附件
//     */
//    private boolean needProcessEmail(Message msg) throws Exception {
//        Logs.info("needProcessEmail  > 当前邮件的标题：{}", msg.getSubject());
//        // 1. 检查发件人邮箱是否包含在我们监控的邮箱列表里面
//        String from = getFrom(msg);
//        if (!monitoringEmail.contains(from)) {
//            Logs.info("当前邮件的发件人[{}]不是我们要监控的对象", from);
//            return false;
//        }
//        if (!isContainAttach((Part) msg)) {
//            Logs.info("发件人满足要求但是附件为空，不满足我们监控的需求！");
//            return false;
//        }
//        Map<String, InputStream> fileMap = new HashMap<>();
//        getFileInputStream(msg, fileMap);
//        if (fileMap.isEmpty()) {
//            Logs.info("尽管邮件中有附件但是邮件中的附件却无一个满足要求！");
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * @param
//     * @param part
//     * @return java.io.InputStream
//     * @author FeianLing
//     * @date 2019/8/20
//     * @desc 获取文件输入流
//     */
//    private void getFileInputStream(Part part, Map<String, InputStream> inputStreamMap) throws Exception {
//        String fileName;
//        if (part.isMimeType("multipart/*")) {
//            Multipart mp = (Multipart) part.getContent();
//            for (int i = 0; i < mp.getCount(); i++) {
//                BodyPart mPart = mp.getBodyPart(i);
//                String disposition = mPart.getDisposition();
//                if ((disposition != null)
//                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
//                        .equals(Part.INLINE)))) {
//                    fileName = mPart.getFileName();
//                    if (fileName.toLowerCase().indexOf("gb2312") != -1) {
//                        fileName = MimeUtility.decodeText(fileName);
//                    }
//                    if (checkFileName(fileName)) {
//                        inputStreamMap.put(fileName, mPart.getInputStream());
//                    }
//                } else if (mPart.isMimeType("multipart/*")) {
//                    Logs.info("子邮件里面的附件");
//                    getFileInputStream(mPart, inputStreamMap);
//                } else {
//                    fileName = mPart.getFileName();
//                    if ((fileName != null)
//                            && (fileName.toLowerCase().indexOf("GB2312") != -1)) {
//                        fileName = MimeUtility.decodeText(fileName);
//                        if (checkFileName(fileName)) {
//                            inputStreamMap.put(fileName, mPart.getInputStream());
//                        }
//                    }
//                }
//            }
//        } else if (part.isMimeType("message/rfc822")) {
//            getFileInputStream((Part) part.getContent(), inputStreamMap);
//        }
//    }
//
//    /**
//     * @param
//     * @param fileName
//     * @return boolean
//     * @author FeianLing
//     * @date 2019/8/20
//     * @desc 检查文件名称是否符合要求 FTK-88584316 FTK-申请号.pdf
//     */
//    private boolean checkFileName(String fileName) {
//        return EmailServiceImpl.FILENAME_REGEX.matcher(fileName).find();
//    }
//
//    /**
//     * @param
//     * @param part
//     * @return boolean
//     * @author FeianLing
//     * @date 2019/8/20
//     * @desc 判断邮件是否包含附件，如果没有包含附件，返回false 反之返回true
//     */
//    private boolean isContainAttach(Part part) throws Exception {
//        boolean attachFlag = false;
//        // String contentType = part.getContentType();
//        if (part.isMimeType("multipart/*")) {
//            Multipart mp = (Multipart) part.getContent();
//            for (int i = 0; i < mp.getCount(); i++) {
//                BodyPart mPart = mp.getBodyPart(i);
//                String disposition = mPart.getDisposition();
//                if ((disposition != null)
//                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
//                        .equals(Part.INLINE)))) {
//                    attachFlag = true;
//                } else if (mPart.isMimeType("multipart/*")) {
//                    attachFlag = isContainAttach((Part) mPart);
//                } else {
//                    String conType = mPart.getContentType();
//
//                    if (conType.toLowerCase().indexOf("application") != -1) {
//                        attachFlag = true;
//                    }
//                    if (conType.toLowerCase().indexOf("name") != -1) {
//                        attachFlag = true;
//                    }
//                }
//            }
//        } else if (part.isMimeType("message/rfc822")) {
//            attachFlag = isContainAttach((Part) part.getContent());
//        }
//        return attachFlag;
//    }
//
//    /**
//     * @param
//     * @param msg
//     * @return java.lang.String
//     * @author FeianLing
//     * @date 2019/8/20
//     * @desc 获取发送地址
//     */
//    private String getFrom(Message msg) throws MessagingException {
//        String from = "";
//        InternetAddress[] addresses = (InternetAddress[]) msg.getFrom();
//        if (null == addresses || addresses.length == 0) {
//            Logs.error("无法获取发送人地址信息！");
//            return from;
//        }
//        Address address = addresses[0];
//        Logs.info("发件人地址json：" + JSONObject.toJSONString(address));
//        String form = ((InternetAddress) address).getAddress();
//        return form;
//    }
//}
