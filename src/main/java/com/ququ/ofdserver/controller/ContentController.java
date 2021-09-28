package com.ququ.ofdserver.controller;

import com.ququ.common.result.ResultJson;
import com.ququ.ofdserver.service.ReplaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "页面内容维护")
@RestController
public class ContentController {

    @Autowired
    private ReplaceService replaceService;

    @ApiOperation(value = "替换页面内容", notes = "将ofd文件替换成其他PDF文件内容", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ofdUrl", value = "原始ofd文件下载地址", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pdfUrl", value = "要替换的pdf文件下载地址", required = true,paramType = "query", dataType = "String")
    })
    @RequestMapping("/replace/content/pdf")
    public ResultJson replaceOfdContentByPdf(@RequestParam String ofdUrl,
                                             @RequestParam String pdfUrl) {
        return replaceService.replaceOfdContentByPdf(ofdUrl, pdfUrl);
    }
}
