package com.ququ.ofdserver.service;

import com.alibaba.fastjson.JSONObject;
import com.ququ.common.result.ResultJson;
import com.ququ.common.utils.HttpFileDownloadUtil;
import com.ququ.ofdserver.ofd.OfdAttachment;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AttactmentService {
    private Logger logger = LoggerFactory.getLogger(AttactmentService.class);

    @Value("${filepath.local}")
    private String filePath;

    @Value("${filepath.temp}")
    private String filePathTemp;

    @Value("${filepath.url-base}")
    private String fileUrlBase;

    @Autowired
    private OfdAttachment ofdAttachment;

    public ResultJson addOfdAttachment(String ofdUrl, String attachmentFileUrl, String attachmentName, String ext) {
        if (StringUtils.isBlank(ofdUrl))
            return new ResultJson(1001, "传入的ofd文件URL地址为空");
        if (!ofdUrl.toLowerCase().startsWith("http://") && ofdUrl.toLowerCase().startsWith("https://")) {
            return new ResultJson(1002, "传入的ofd文件URL格式错误");
        }
        String ofdFileName = ofdUrl.substring(ofdUrl.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(attachmentFileUrl))
            return new ResultJson(1001, "传入的附件文件URL地址为空");
        if (!attachmentFileUrl.toLowerCase().startsWith("http://") && !attachmentFileUrl.toLowerCase().startsWith("https://")) {
            return new ResultJson(1002, "传入的附件文件URL格式错误");
        }
        String attachmentFileName = attachmentFileUrl.substring(attachmentFileUrl.lastIndexOf("/") + 1);
        if (StringUtils.isNotBlank(ext)) {
            if (attachmentFileName.lastIndexOf(".") > 0)
                attachmentFileName = attachmentName.substring(0, attachmentFileName.lastIndexOf(".") - 1) + ext;
            else
                attachmentFileName = attachmentName + ext;
        }
        if (StringUtils.isBlank(attachmentName)) {
            attachmentName = attachmentFileName;
        }
        try {
            HttpFileDownloadUtil.downLoadFromUrl(ofdUrl, ofdFileName, filePathTemp);
            HttpFileDownloadUtil.downLoadFromUrl(attachmentFileUrl, attachmentFileName, filePathTemp);
            ResultJson resultJson = ofdAttachment.attachFileIntoOFD(filePathTemp + ofdFileName, filePathTemp + attachmentFileName, attachmentName);
            if (resultJson.getStatus() == 1) {
                File file = new File(filePathTemp + ofdFileName);
                file.renameTo(new File(filePath + ofdFileName));
                logger.debug("文件添加附件成功，准备返回结果");
                JSONObject result = new JSONObject();
                result.put("ofd_file_url", fileUrlBase + ofdFileName);  //返回可下载的ofd文件地址
                return new ResultJson(result);
            } else {
                return resultJson;
            }
        } catch (Exception e) {
            logger.error("添加ofd附件异常:", e);
            return new ResultJson(1004, "系统异常:" + e.getMessage());
        }
    }


    public ResultJson removeOfdAttachment(String ofdUrl, String attachmentName) {
        if (StringUtils.isBlank(ofdUrl))
            return new ResultJson(1001, "传入的ofd文件URL地址为空");
        if (!ofdUrl.toLowerCase().startsWith("http://") && ofdUrl.toLowerCase().startsWith("https://")) {
            return new ResultJson(1002, "传入的ofd文件URL格式错误");
        }
        String ofdFileName = ofdUrl.substring(ofdUrl.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(attachmentName)) {
            return new ResultJson(1006, "要删除的附件名称不可为空");
        }
        try {
            HttpFileDownloadUtil.downLoadFromUrl(ofdUrl, ofdFileName, filePathTemp);
            ResultJson resultJson = ofdAttachment.removeAttachFromOFD(filePathTemp + ofdFileName, attachmentName);
            if (resultJson.getStatus() == 1) {
                File file = new File(filePathTemp + ofdFileName);
                file.renameTo(new File(filePath + ofdFileName));
                logger.debug("文件删除附件成功，准备返回结果");
                JSONObject result = new JSONObject();
                result.put("ofd_file_url", fileUrlBase + ofdFileName);  //返回可下载的ofd文件地址
                return new ResultJson(result);
            } else {
                return resultJson;
            }
        } catch (Exception e) {
            logger.error("删除ofd附件异常:", e);
            return new ResultJson(1004, "系统异常:" + e.getMessage());
        }
    }


}
