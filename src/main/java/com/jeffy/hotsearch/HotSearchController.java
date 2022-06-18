package com.jeffy.hotsearch;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Collections;
import java.util.List;

/**
 * 热词控制器
 */
@RestController
@Slf4j
public class HotSearchController {
    @Autowired
    private RedisService redisService;

    /**
     * 获取热词列表
     *
     * @param userId    用户ID
     * @param searchKey 关键字
     * @return 热词列表
     */
    @GetMapping("/{userId}/hots")
    public List<String> getHotList(@PathVariable("userId") String userId, @PathParam("searchKey") String searchKey) {
        log.info("userId: {}, searchKey: {}", userId, searchKey);
        if (StringUtils.isEmpty(searchKey)) {
            log.error("searchKey is empty");
            return Collections.emptyList();
        }

        List<String> hotList = redisService.getHotList(searchKey);
        log.info("hotList: {}", hotList);
        return hotList;
    }

    /**
     * 每次点击给相关词searchkey热度 +1
     *
     * @param searchKey 关键字
     */
    @GetMapping("/increment-score")
    public String incrementScore(@PathParam("searchKey") String searchKey) {
        log.info("searchKey: {}", searchKey);
        if (StringUtils.isEmpty(searchKey)) {
            return "searchKey is empty";
        }

        int result = redisService.incrementScore(searchKey);
        log.info("incrementScore result: {}", result);
        return "incrementScore: ok";
    }

    /**
     * 新增热词搜索记录，将用户输入的热词存储
     *
     * @param userId    用户ID
     * @param searchKey 待处理文本
     */
    @GetMapping("/insert-score/{userId}")
    public String insertScoreByUserId(@PathVariable("userId") String userId, @PathParam("searchKey") String searchKey) {
        log.info("userId: {}, searchKey: {}", userId, searchKey);
        if (StringUtils.isEmpty(searchKey)) {
            return "searchKey is empty";
        }

        int result = redisService.insertScore(searchKey);
        log.info("insertScoreByUserId result: {}", result);
        return "insertScoreByUserId: ok";
    }

    /**
     * 新增热词搜索记录，将用户输入的热词存储
     *
     * @param userId 用户ID
     */
    @GetMapping("/history/{userId}")
    public List<String> getSearchHistoryByUserId(@PathVariable("userId") String userId) {
        log.info("userId: {}", userId);
        if (StringUtils.isEmpty(userId)) {
            log.error("userId is empty");
        }

        List<String> result = redisService.getSearchHistoryByUserId(userId);
        log.info("getSearchHistoryByUserId result: {}", result);
        return result;
    }

    /**
     * 新增热词搜索记录，将用户输入的热词存储
     *
     * @param searchHistory 用户搜索记录
     */
    @PostMapping("/history")
    public String insertSearchHistoryByUserId(@RequestBody SearchHistory searchHistory) {
        log.info("searchHistory: {}", searchHistory);
        if (StringUtils.isAnyEmpty(searchHistory.getUserId(), searchHistory.getSearchKey())) {
            log.error("userId or searchKey is empty");
            return "params is invalid";
        }

        int result = redisService.insertSearchHistoryByUserId(searchHistory.getUserId(), searchHistory.getSearchKey());
        log.info("insertSearchHistoryByUserId result: {}", result);
        return "insertSearchHistoryByUserId: ok";
    }

    /**
     * 删除个人历史数据
     *
     * @param searchHistory 搜索記錄
     * @return
     */
    @DeleteMapping("/history")
    public String delSearchHistoryByUserId(@RequestBody SearchHistory searchHistory) {
        log.info("searchHistory: {}", searchHistory);
        if (StringUtils.isAnyEmpty(searchHistory.getUserId(), searchHistory.getSearchKey())) {
            log.error("userId or searchKey is empty");
            return "params is invalid";
        }

        Long result = redisService.delSearchHistoryByUserId(searchHistory.getUserId(), searchHistory.getSearchKey());
        log.info("delSearchHistoryByUserId result: {}", result);
        return "delSearchHistoryByUserId: ok";
    }
}
