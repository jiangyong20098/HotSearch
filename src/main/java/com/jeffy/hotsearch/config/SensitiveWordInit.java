package com.jeffy.hotsearch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 屏蔽敏感词初始化
 */
@Configuration
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class SensitiveWordInit {
    // 字符编码
    private String ENCODING = "UTF-8";

    // 初始化敏感字库
    public Map initKeyWord() throws IOException {
        // 读取敏感词库 ,存入Set中
        Set<String> wordSet = readSensitiveWordFile();
        log.info("wordSet: {}", wordSet);

        // 将敏感词库加入到HashMap中
        return addSensitiveWordToHashMap(wordSet);
    }

    private Map addSensitiveWordToHashMap(Set<String> wordSet) {
        // 初始化敏感词容器，减少扩容操作
        Map wordMap = new HashMap(wordSet.size());
        for (String word : wordSet) {
            Map nowMap = wordMap;
            for (int i = 0; i < word.length(); i++) {
                // 转换成char型
                char keyChar = word.charAt(i);

                // 获取
                Object tempMap = nowMap.get(keyChar);

                // 如果存在该key，直接赋值
                if (tempMap != null) {
                    nowMap = (Map) tempMap;
                }
                // 不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                else {
                    // 设置标志位
                    Map<String, String> newMap = new HashMap<String, String>();
                    newMap.put("isEnd", "0");

                    // 添加到集合
                    nowMap.put(keyChar, newMap);
                    nowMap = newMap;
                }
                // 最后一个
                if (i == word.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
        return wordMap;
    }

    // 读取敏感词库 ,存入HashMap中
    private Set<String> readSensitiveWordFile() throws IOException {
        Set<String> wordSet = null;
        ClassPathResource classPathResource = new ClassPathResource("static/censorword.txt");
        try (InputStream inputStream = classPathResource.getInputStream();
             InputStreamReader read = new InputStreamReader(inputStream, ENCODING);
             BufferedReader br = new BufferedReader(read)) {
            wordSet = new HashSet<String>();
            String txt = null;
            while ((txt = br.readLine()) != null) {
                wordSet.add(txt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wordSet;
    }

    private Map addToHashMap(Set<String> wordSet) {
        // 初始化敏感词容器
        Map wordMap = new HashMap(wordSet.size());
        for (String word : wordSet) {
            Map nowMap = wordMap;
            for (int i = 0; i < word.length(); i++) {
                char keyChar = word.charAt(i);
                Object tempMap = nowMap.get(keyChar);
                if (tempMap != null) {
                    nowMap = (Map) tempMap;
                }
                // 不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                else {
                    // 设置标志位
                    Map<String, String> newMap = new HashMap<String, String>();
                    newMap.put("isEnd", "0");
                    nowMap.put(keyChar, newMap);
                    nowMap = newMap;
                }
                // 最后一个
                if (i == word.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
        return wordMap;
    }
}