package com.ecnu.trivia.web.common.controller;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.utils.Resp;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/template")
public class TestController {

    /**
     * GET 方法示例
     *
     * @author Jack Chen
     */
    @RequestMapping(value = "/get/", method = RequestMethod.GET)
    public Resp getTest() {
        return new Resp(HttpRespCode.USER_NOT_LOGIN);
    }

    /**
     * POST 方法示例
     *
     * @author Jack Chen
     */
    @RequestMapping(value = "/post/", method = RequestMethod.POST)
    public Resp postTest() {
        return new Resp(HttpRespCode.USER_NOT_LOGIN);
    }

    /**
     * DELETE 方法示例
     *
     * @author Jack Chen
     */
    @RequestMapping(value = "/delete/", method = RequestMethod.DELETE)
    public Resp deleteTest() {
        return new Resp(HttpRespCode.USER_NOT_LOGIN);
    }
}

