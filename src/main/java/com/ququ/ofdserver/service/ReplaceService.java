package com.ququ.ofdserver.service;

import com.alibaba.fastjson.JSONObject;
import com.ququ.common.result.ResultJson;
import com.ququ.common.utils.HttpFileDownloadUtil;
import com.ququ.ofdserver.ofd.OfdContentReplace;
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
public class ReplaceService {

    private Logger logger = LoggerFactory.getLogger(ReplaceService.class);

    @Value("${filepath.local}")
    private String filePath;

    @Value("${filepath.temp}")
    private String filePathTemp;

    @Value("${filepath.url-base}")
    private String fileUrlBase;

    @Autowired
    private OfdContentReplace ofdContentReplace;

    public ResultJson replaceOfdContentByPdf(String ofdUrl,String pdfUrl) {
        if (StringUtils.isBlank(pdfUrl))
            return new ResultJson(1001, "传入的pdf文件URL地址为空");
        if (!pdfUrl.toLowerCase().startsWith("http://") && pdfUrl.toLowerCase().startsWith("https://")) {
            return new ResultJson(1002, "传入的pdf文件URL格式错误");
        }
        String pdfName = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(ofdUrl))
            return new ResultJson(1001, "传入的ofd文件URL地址为空");
        if (!ofdUrl.toLowerCase().startsWith("http://") && ofdUrl.toLowerCase().startsWith("https://")) {
            return new ResultJson(1002, "传入的ofd文件URL格式错误");
        }
        String ofdFileName = ofdUrl.substring(ofdUrl.lastIndexOf("/") + 1);
        try {
            //下载PDF文件到临时目录
            HttpFileDownloadUtil.downLoadFromUrl(pdfUrl, pdfName, filePathTemp);
            HttpFileDownloadUtil.downLoadFromUrl(ofdUrl,ofdFileName,filePathTemp);
            logger.debug("文件下载完成");
            //替换文件内容
            ResultJson resultJson = ofdContentReplace.replaceOfdContentByPdf(filePathTemp + ofdFileName, filePathTemp + pdfName);
            logger.debug("文件替换换完成");
            //删除下载的临时PDF文件
            File file = new File(filePathTemp + pdfName);
            file.delete();
            //移动结果文件
            file = new File(filePathTemp+ofdFileName);
            file.renameTo(new File(filePath+ofdFileName));
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
