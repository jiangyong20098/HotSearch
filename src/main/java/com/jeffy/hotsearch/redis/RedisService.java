package com.jeffy.hotsearch.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * @Description 获取String类型的value
     * @param name
     * @return
     */
    String findName(String name);

    /**
     * @Description 添加String类型的key-value
     * @param name
     * @param value
     * @return
     */
    String setNameValue(String name, String value);

    /**
     * @Description 根据key删除redis的数据
     * @param name
     * @return
     */
    String delNameValue(String name);

    /**
     * @Description 根据key获取list类型的value(范围)
     * @param key
     * @return
     */
    List<String> findList(String key,int start,int end);

    /**
     * @Description 插入多条数据
     * @param key
     * @param value
     * @return
     */
    long setList(String key, List<String> value);

    /**
     * @Description 获取list最新记录（右侧）
     * @param key
     * @return
     */
    String findLatest(String key);

    /**
     * @Description 查询hash
     * @param key
     * @return
     */
    Map<Object, Object> findHash(String key);

    /**
     * @Description 查询hash中所有的key
     * @param key
     * @return
     */
    Set<Object> findHashKeys(String key);

    /**
     * @Description 查询hash中所有的value
     * @param key
     * @return
     */
    List<Object> findHashValues(String key);

    /**
     * @param key
     * @param map
     * @return
     * @Desscription 插入hash数据
     */
    long insertHash(String key, Map<String, Object> map);
}
