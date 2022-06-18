package com.jeffy.hotsearch;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.Locale;

@RestController
@Slf4j
public class SensitiveWordController {

    @GetMapping("/check/{userId}")
    public String checkSensitiveWord(@PathVariable("userId") String userId, @PathParam("searchKey") String searchKey) throws IOException {
        log.info("userId: {}, searchKey: {}", userId, searchKey);
        if (StringUtils.isEmpty(searchKey)) {
            return "searchKey is empty";
        }

        //非法敏感词汇判断
        SensitiveFilter filter = SensitiveFilter.getInstance();
        int n = filter.checkSensitiveWord(searchKey, 0, 1);
        if (n > 0) { //存在非法字符
            String msg = String.format(Locale.ENGLISH, "这个人输入了非法字符--> %s,不知道他到底要查什么~ userid--> %s", searchKey, userId);
            log.info(msg);
            return msg;
        }
        log.info("你很文明");
        return "你很文明";
    }

    /**
     * 替换敏感信息
     * <p>
     * http://localhost:8080/replace/user1?text=fuck%20you
     * 输出：**** you
     *
     * @param userId 用户ID
     * @param text   待处理文本
     * @throws IOException
     */
    @GetMapping("/replace/{userId}")
    public String replaceSensitiveWord(@PathVariable("userId") String userId, @PathParam("text") String text) throws IOException {
        log.info("text: {}", text);
        SensitiveFilter filter = SensitiveFilter.getInstance();
        String result = filter.replaceSensitiveWord(text, 1, "*");
        log.info("result: {}", result);
        return text + " -> " + result;
    }
}
