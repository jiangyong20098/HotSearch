package com.jeffy.hotsearch;

import java.util.List;

public interface RedisService {
    /**
     * 新增一条userid用户在搜索栏的历史记录
     *
     * @param userId
     * @param searchKey 输入的关键词
     */
    int insertSearchHistoryByUserId(String userId, String searchKey);

    /**
     * 删除个人历史数据
     *
     * @param userId
     * @param searchKey 输入的关键词
     */
    Long delSearchHistoryByUserId(String userId, String searchKey);

    /**
     * 获取个人历史数据列表
     *
     * @param userId
     */
    List<String> getSearchHistoryByUserId(String userId);

    /**
     * 新增热词搜索记录，将用户输入的热词存储
     *
     * @param searchKey 输入的关键词
     */
    int insertScore(String searchKey);

    /**
     * 根据searchkey搜索其相关最热的前十名
     * (如果searchkey为null空，则返回redis存储的前十最热词条)
     *
     * @param searchKey 输入的关键词
     */
    List<String> getHotList(String searchKey);

    /**
     * 每次点击给相关词searchkey热度 +1
     *
     * @param searchKey 输入的关键词
     */
    int incrementScore(String searchKey);
}
