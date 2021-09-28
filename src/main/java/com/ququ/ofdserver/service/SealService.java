package com.ququ.ofdserver.service;

import com.alibaba.fastjson.JSONObject;
import com.ququ.common.result.ResultJson;
import com.ququ.common.utils.HttpFileDownloadUtil;
import com.ququ.ofdserver.ofd.OfdSeal;
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
public class SealService {

    private Logger logger = LoggerFactory.getLogger(SealService.class);

    @Autowired
    private OfdSeal ofdSeal;

    @Value("${filepath.local}")
    private String filePath;

    @Value("${filepath.temp}")
    private String filePathTemp;

    @Value("${filepath.url-base}")
    private String fileUrlBase;


    public ResultJson sealOfd(String ofdUrl, Integer page, Double x, Double y, Integer[] pages, Double[] xx, Double[] yy) {
        if (StringUtils.isBlank(ofdUrl))
            return new ResultJson(1001, "传入的ofd文件URL地址为空");
        if (!ofdUrl.toLowerCase().startsWith("http://") && ofdUrl.toLowerCase().startsWith("https://")) {
            return new ResultJson(1002, "传入的ofd文件URL格式错误");
        }
        String ofdFileName = ofdUrl.substring(ofdUrl.lastIndexOf("/") + 1);
        try {
            HttpFileDownloadUtil.downLoadFromUrl(ofdUrl, ofdFileName, filePathTemp);
        } catch (Exception e) {
            logger.error("下载失败:", e);
            return new ResultJson(1007, "ofd文件下载失败" + e.getMessage());
        }
        String ofdFilePath = filePathTemp + ofdFileName;
        logger.debug("文件下载完成");
        ResultJson resultJson = ofdSeal.sealOfd(ofdFilePath, page, x, y, pages, xx, yy);
        if (resultJson.getStatus() == 1) {
            //移动结果文件
            //删除旧文件，新文件改名
            File file = new File(ofdFilePath);
            file.delete();
            file = new File(ofdFilePath + ".tmp");
            file.renameTo(new File(filePath + ofdFileName));
            JSONObject result = new JSONObject();
            result.put("sealed_ofd_url", fileUrlBase + ofdFileName);  //返回可下载的ofd文件地址
            return new ResultJson(result);

        } else
            return resultJson;
    }
}
