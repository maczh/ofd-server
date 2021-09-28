package com.ququ.ofdserver.service;

import com.alibaba.fastjson.JSONObject;
import com.ququ.common.result.ResultJson;
import com.ququ.common.utils.HttpFileDownloadUtil;
import com.ququ.ofdserver.ofd.OfdConverter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Configuration
public class PDFConvertService {

    private Logger logger = LoggerFactory.getLogger(PDFConvertService.class);

    @Value("${filepath.local}")
    private String filePath;

    @Value("${filepath.temp}")
    private String filePathTemp;

    @Value("${filepath.url-base}")
    private String fileUrlBase;

    @Autowired
    private OfdConverter ofdConverter;

    public ResultJson ConvertFromPdf(String pdfUrl, String ofdFileName) {
        if (StringUtils.isBlank(pdfUrl))
            return new ResultJson(1001, "传入的pdf文件URL地址为空");
        if (!pdfUrl.toLowerCase().startsWith("http://") && pdfUrl.toLowerCase().startsWith("https://")) {
            return new ResultJson(1002, "传入的pdf文件URL格式错误");
        }
        String pdfName = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(ofdFileName)) {
            String[] fname = pdfName.split("\\.");
            ofdFileName = fname[0] + ".ofd";
        }
        try {
            //下载PDF文件到临时目录
            HttpFileDownloadUtil.downLoadFromUrl(pdfUrl, pdfName, filePathTemp);
            logger.debug("文件下载完成");
            //转换PDF文件成OFD文件
            ResultJson resultJson = ofdConverter.convertPDFToOFD(filePathTemp + pdfName, filePath + ofdFileName);
            logger.debug("文件转换完成");
            //删除下载的临时PDF文件
            File file = new File(filePathTemp + pdfName);
            file.delete();
            if (resultJson.getStatus() == 1) {
                logger.debug("文件转换成功，准备返回结果");
                JSONObject result = new JSONObject();
                result.put("ofd_file_url", fileUrlBase + ofdFileName);  //返回可下载的ofd文件地址
                return new ResultJson(result);
            } else
                return resultJson;
        } catch (Exception e) {
            logger.error("系统异常:",e);
            return new ResultJson(1004,"系统异常:"+e.getMessage());
        }
    }


    public ResultJson ConvertFromWord(String docxUrl, String ofdFileName) {
        if (StringUtils.isBlank(docxUrl))
            return new ResultJson(1001, "传入的Word文件URL地址为空");
        if (!docxUrl.toLowerCase().startsWith("http://") && docxUrl.toLowerCase().startsWith("https://")) {
            return new ResultJson(1002, "传入的Word文件URL格式错误");
        }
        String docxFileName = docxUrl.substring(docxUrl.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(ofdFileName)) {
            String[] fname = docxFileName.split("\\.");
            ofdFileName = fname[0] + ".ofd";
        }
        try {
            //下载PDF文件到临时目录
            HttpFileDownloadUtil.downLoadFromUrl(docxUrl, docxFileName, filePathTemp);
            logger.debug("文件下载完成");
            //转换PDF文件成OFD文件
            ResultJson resultJson = ofdConverter.convertWordToOFD(filePathTemp + docxFileName, filePath + ofdFileName);
            logger.debug("文件转换完成");
            //删除下载的临时PDF文件
            File file = new File(filePathTemp + docxFileName);
            file.delete();
            if (resultJson.getStatus() == 1) {
                logger.debug("文件转换成功，准备返回结果");
                JSONObject result = new JSONObject();
                result.put("ofd_file_url", fileUrlBase + ofdFileName);  //返回可下载的ofd文件地址
                return new ResultJson(result);
            } else
                return resultJson;
        } catch (Exception e) {
            logger.error("系统异常:",e);
            return new ResultJson(1004,"系统异常:"+e.getMessage());
        }
    }


}
