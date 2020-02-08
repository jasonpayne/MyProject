//package com.xinchao.schedule;
//
//import com.alibaba.fastjson.JSONObject;
//import com.bitmain.ai.ccos.manager.common.bean.strategy.TpuPower;
//import com.bitmain.ai.ccos.manager.common.consts.StrategyConst;
//import com.bitmain.ai.ccos.manager.strategy.component.StateMgr;
//import com.bitmain.ai.ccos.manager.strategy.consts.CommonConst;
//import com.bitmain.ai.ccos.manager.strategy.consts.MessageLogConst;
//import com.bitmain.ai.ccos.manager.strategy.dao.mapper.StrategyMessageLogMapper;
//import com.bitmain.ai.ccos.manager.strategy.enums.DeviceStatus;
//import com.bitmain.ai.ccos.manager.strategy.enums.TpuStatus;
//import com.bitmain.ai.ccos.manager.strategy.service.AlgorithmService;
//import com.bitmain.ai.ccos.manager.strategy.util.Utils;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//
///**
// * 扫描检测识别端核心板
// *
// * @author xinchao.pan@bitmain.com
// */
//@Component
//public class ScanClassScheduler {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ScanCheckTpuScheduler.class);
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Autowired
//    private StateMgr stateMgr;
//
//    @Autowired
//    private StrategyMessageLogMapper strategyMessageLogMapper;
//
//    @Autowired
//    private AlgorithmService algorithmService;
//
//    /**
//     * 检测识别端列表，如果异常放入可恢复异常列表中
//     * 每隔10分钟执行一次
//     */
//    @Scheduled(cron = "0 5,15,25,35,45,55 * * * ?")
//    @Async("asyncScheduleExecutor")
//    public void scanCheckRecognitionTpu() {
//        LOGGER.info("定时检测识别端列表");
//        Set<String> keySet = redisTemplate.keys(CommonConst.REDIS_KEY_STRATEGY_HDS_POWER_PREFIX + "*");
//        for (String powerKey : keySet) {
//            Map<Object, Object> map = redisTemplate.opsForHash().entries(powerKey);
//            for (Map.Entry<Object, Object> entry : map.entrySet()) {
//                TpuPower tpuPower = JSONObject.parseObject((String) entry.getValue(), TpuPower.class);
//                if (!StringUtils.equals(tpuPower.getAlgorithmType(), StrategyConst.ALGORITHM_TYPE_STRUCTURE_RECOGNITION)
//                        && !StringUtils.equals(tpuPower.getAlgorithmType(), StrategyConst.ALGORITHM_TYPE_FACE_RECOGNITION)) {
//                    continue;
//                }
//                // 选取状态空闲或在用的设备
//                int deviceStatus = stateMgr.getDeviceStatus(tpuPower.getHdsSn());
//                if (deviceStatus != DeviceStatus.IDLE.getStatus() && deviceStatus != DeviceStatus.USING.getStatus()) {
//                    LOGGER.error("检测识别端列表，设备：{} 状态非正常", tpuPower.getHdsSn());
//                    continue;
//                }
//                // 选取状态空闲或在用的核心板
//                Integer tpuStatus = stateMgr.getTpuStatus(tpuPower.getHdsSn(), tpuPower.getTpuId());
//                if (!Objects.equals(tpuStatus, TpuStatus.IDLE.getStatus())
//                        && !Objects.equals(tpuStatus, TpuStatus.USING.getStatus())) {
//                    LOGGER.error("检测识别端列表，HDS：{} 核心板ID：{} 未运行", tpuPower.getHdsSn(), tpuPower.getTpuId());
//                    continue;
//                }
//                if (!algorithmService.checkAlgorithmServer(tpuPower)) {
//                    // 设置核心板状态为可自动恢复异常
//                    stateMgr.setTpuStatus(tpuPower.getHdsSn(), tpuPower.getTpuId(),
//                            TpuStatus.RECOVERABLE_ABNORMAL.getStatus());
//                    // 保存异常日志
//                    String reason = "检测识别端不可用，放置可恢复异常列表中";
//                    Utils.saveMessageLog(tpuPower, stateMgr, redisTemplate, strategyMessageLogMapper,
//                            MessageLogConst.SOURCE_STRATEGY, MessageLogConst.MESSAGE_TYPE_ABNORMAL, reason);
//                }
//            }
//        }
//    }
//}
