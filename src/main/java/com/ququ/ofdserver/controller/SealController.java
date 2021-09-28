package com.ququ.ofdserver.controller;

import com.ququ.common.result.ResultJson;
import com.ququ.ofdserver.service.SealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "电子签章")
@RestController
public class SealController {

    @Autowired
    private SealService sealService;

    @ApiOperation(value = "添加附件", notes = "向ofd文件中添加附件", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ofdUrl", value = "原始ofd文件下载地址", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "page", value = "要盖章的页号，当只盖一个章时", required = false,paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "x", value = "电子盖章在页面左上角的x坐标，当只盖一个章时", required = false,paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "y", value = "电子盖章在页面左上角的x坐标，当只盖一个章时", required = false,paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "pages", value = "要盖章的页号数组，当盖多个章时", required = false,paramType = "query", dataType = "Integer[]"),
            @ApiImplicitParam(name = "xx", value = "电子盖章在页面左上角的x坐标数组，当盖多个章时", required = false,paramType = "query", dataType = "Double[]"),
            @ApiImplicitParam(name = "yy", value = "电子盖章在页面左上角的x坐标数组，当盖多个章时", required = false,paramType = "query", dataType = "Double[]")
    })
    @RequestMapping("/seal")
    public ResultJson sealOfd(@RequestParam String ofdUrl,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Double x,
                              @RequestParam(required = false) Double y,
                              @RequestParam(required = false) Integer[] pages,
                              @RequestParam(required = false) Double[] xx,
                              @RequestParam(required = false) Double[] yy) {
        if (page == null && pages == null)
            return new ResultJson(1001, "缺少盖章在页号参数");
        if (page != null) {
            if (x == null || y == null)
                return new ResultJson(1001, "缺少盖章位置x或y参数");
        } else if (pages.length > 0) {
            if (xx == null || xx.length == 0 || yy == null || yy.length == 0)
                return new ResultJson(1001, "多个签章缺少对应x或y参数数组");
            if (pages.length != xx.length || pages.length != yy.length)
                return new ResultJson(1009, "多个签单页数组数量与x或y数组个数不符");
        }
        return sealService.sealOfd(ofdUrl, page, x, y, pages, xx, yy);
    }

}
