package com.ququ.ofdserver.controller;

import com.ququ.common.result.ResultJson;
import com.ququ.ofdserver.service.AttactmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "附件管理")
@RestController
public class AttachmentController {
    @Autowired
    private AttactmentService attactmentService;

    @ApiOperation(value = "添加附件", notes = "向ofd文件中添加附件", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ofdUrl", value = "原始ofd文件下载地址", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "attachmentFileUrl", value = "要添加的附件文件下载地址", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "attachmentName", value = "要添加的附件文件显示名称", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "ext", value = "要添加的附件文件扩展名，当url中没有文件扩展名时需要,如.jpg", required = false,paramType = "query", dataType = "String")
    })
    @RequestMapping("/attachment/add")
    public ResultJson addOfdAttachment(@RequestParam String ofdUrl,
                                       @RequestParam String attachmentFileUrl,
                                       @RequestParam String attachmentName,
                                       @RequestParam(required = false) String ext) {
        return attactmentService.addOfdAttachment(ofdUrl, attachmentFileUrl, attachmentName, ext);
    }

    @ApiOperation(value = "删除附件", notes = "从ofd文件中删除指定名称的附件", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ofdUrl", value = "原始ofd文件下载地址", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "attachmentName", value = "要删除的附件文件显示名称", required = true,paramType = "query", dataType = "String")
    })
    @RequestMapping("/attachment/delete")
    public ResultJson removeAttachFromOFD(@RequestParam String ofdUrl,
                                          @RequestParam String attachmentName) {
        return attactmentService.removeOfdAttachment(ofdUrl, attachmentName);
    }

}
