package com.ququ.ofdserver.controller;

import com.ququ.common.result.ResultJson;
import com.ququ.ofdserver.service.PDFConvertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "OFD生成")
@RestController
public class ConvertController {
    @Autowired
    private PDFConvertService pdfConvertService;

    @ApiOperation(value = "pdf转换ofd文件", notes = "用pdf文件转换生成ofd文件", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pdfUrl", value = "原始pdf文件下载地址", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "ofdFileName", value = "指定ofd文件的文件名，不指定时与pdf文件同名，只改扩展名", required = false,paramType = "query", dataType = "String")
    })
    @RequestMapping("/convert/pdf")
    public ResultJson ConvertFromPdf(@RequestParam String pdfUrl, @RequestParam(required = false) String ofdFileName){
        return pdfConvertService.ConvertFromPdf(pdfUrl, ofdFileName);
    }

    @ApiOperation(value = "docx转换ofd文件", notes = "用docx文件转换生成ofd文件", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "docxUrl", value = "原始docx文件下载地址", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "ofdFileName", value = "指定ofd文件的文件名，不指定时与docx文件同名，只改扩展名", required = false,paramType = "query", dataType = "String")
    })
    @RequestMapping("/convert/word")
    public ResultJson ConvertFromWord(@RequestParam String docxUrl, @RequestParam(required = false) String ofdFileName){
        return pdfConvertService.ConvertFromWord(docxUrl, ofdFileName);
    }

}
