package com.jeffy.hotsearch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@Transactional
@Service("redisService")
public class RedisServiceImpl implements RedisService {
    //导入数据源
//    @Resource(name = "redisSearchTemplate")
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增一条userid用户在搜索栏的历史记录
     *
     * @param userId    用户ID
     * @param searchKey 输入的关键词
     */
    @Override
    public int insertSearchHistoryByUserId(String userId, String searchKey) {
        String historyKey = RedisKeyUtils.getSearchHistoryKey(userId);
        boolean b = stringRedisTemplate.hasKey(historyKey);
        if (b) {
            Object hk = stringRedisTemplate.opsForHash().get(historyKey, searchKey);
            if (hk != null) {
                return 1;
            } else {
                stringRedisTemplate.opsForHash().put(historyKey, searchKey, "1");
            }
        } else {
            stringRedisTemplate.opsForHash().put(historyKey, searchKey, "1");
        }
        return 1;
    }

    /**
     * 删除个人历史数据
     *
     * @param userId 用户ID
     * @param searchKey 输入的关键词
     */
    @Override
    public Long delSearchHistoryByUserId(String userId, String searchKey) {
        String shistory = RedisKeyUtils.getSearchHistoryKey(userId);
        return stringRedisTemplate.opsForHash().delete(shistory, searchKey);
    }

    /**
     * 获取个人历史数据列表
     *
     * @param userId 用戶ID
     */
    @Override
    public List<String> getSearchHistoryByUserId(String userId) {
        List<String> stringList = null;
        String shistory = RedisKeyUtils.getSearchHistoryKey(userId);
        boolean b = stringRedisTemplate.hasKey(shistory);
        if (b) {
            Cursor<Map.Entry<Object, Object>> cursor = stringRedisTemplate.opsForHash().scan(shistory, ScanOptions.NONE);
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> map = cursor.next();
                String key = map.getKey().toString();
                if (CollectionUtils.isEmpty(stringList)) {
                    stringList = new ArrayList<>();
                }
                stringList.add(key);
            }
            return stringList;
        }
        return null;
    }

    /**
     * 新增热词搜索记录，将用户输入的热词存储
     *
     * @param searchKey 输入的关键词
     */
    @Override
    public int insertScore(String searchKey) {
        Long now = System.currentTimeMillis();
        ZSetOperations zSetOperations = stringRedisTemplate.opsForZSet();
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        List<String> title = new ArrayList<>();
        title.add(searchKey);
        for (int i = 0, lengh = title.size(); i < lengh; i++) {
            String tle = title.get(i);
            try {
                if (zSetOperations.score("title", tle) <= 0) {
                    zSetOperations.add("title", tle, 0);
                    valueOperations.set(tle, String.valueOf(now));
                }
            } catch (Exception e) {
                zSetOperations.add("title", tle, 0);
                valueOperations.set(tle, String.valueOf(now));
            }
        }
        return 1;
    }

    /**
     * 根据searchkey搜索其相关最热的前十名
     * (如果searchkey为null空，则返回redis存储的前十最热词条)
     *
     * @param searchKey 输入的关键词
     */
    @Override
    public List<String> getHotList(String searchKey) {
        String key = searchKey;
        Long now = System.currentTimeMillis();
        List<String> result = new ArrayList<>();
        ZSetOperations zSetOperations = stringRedisTemplate.opsForZSet();
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Set<String> value = zSetOperations.reverseRangeByScore("title", 0, Double.MAX_VALUE);
        //key不为空的时候 推荐相关的最热前十名
        if (StringUtils.isNotEmpty(searchKey)) {
            for (String val : value) {
                if (StringUtils.containsIgnoreCase(val, key)) {
                    if (result.size() > 9) {//只返回最热的前十名
                        break;
                    }
                    Long time = Long.valueOf(valueOperations.get(val));
                    if ((now - time) < 2592000000L) {//返回最近一个月的数据
                        result.add(val);
                    } else {//时间超过一个月没搜索就把这个词热度归0
                        zSetOperations.add("title", val, 0);
                    }
                }
            }
        } else {
            for (String val : value) {
                if (result.size() > 9) {//只返回最热的前十名
                    break;
                }
                Long time = Long.valueOf(valueOperations.get(val));
                if ((now - time) < 2592000000L) {//返回最近一个月的数据
                    result.add(val);
                } else {//时间超过一个月没搜索就把这个词热度归0
                    zSetOperations.add("title", val, 0);
                }
            }
        }
        return result;
    }

    /**
     * 每次点击给相关词searchkey热度 +1
     *
     * @param searchKey 输入的关键词
     */
    @Override
    public int incrementScore(String searchKey) {
        String key = searchKey;
        Long now = System.currentTimeMillis();
        ZSetOperations zSetOperations = stringRedisTemplate.opsForZSet();
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        zSetOperations.incrementScore("title", key, 1);
        valueOperations.getAndSet(key, String.valueOf(now));
        return 1;
    }
}