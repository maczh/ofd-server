package com.ququ.ofdserver.ofd;

import com.ququ.common.result.ResultJson;
import org.ofd.render.OFDRender;
import org.ofdrw.layout.OFDDoc;
import org.ofdrw.layout.edit.Attachment;
import org.ofdrw.reader.OFDReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class OfdContentReplace {

    private Logger logger = LoggerFactory.getLogger(OfdContentReplace.class);

    public ResultJson replaceOfdContentByPdf(String ofdFilePath,String pdfFilePath){
        Path pdfIn = Paths.get(pdfFilePath);
        Path ofdFromPdf = Paths.get(pdfFilePath+".tmp");
        Path ofdOld = Paths.get(ofdFilePath);
        Path ofdout = Paths.get(ofdFilePath+".tmp");
        try {
            logger.debug("开始转换PDF文件{}到{}",pdfFilePath,ofdFilePath);
            OFDRender.convertPdfToOfd(Files.newInputStream(pdfIn), Files.newOutputStream(ofdFromPdf));
            OFDReader readerOld = new OFDReader(ofdOld);
            OFDReader reader = new OFDReader(ofdFromPdf);
            OFDDoc ofdDoc = new OFDDoc(reader, ofdout);
            List<String> attachNames = readerOld.getAttachmentNames();
            //开始复制附件
            if (attachNames.size() > 0){
                logger.debug("所有附件:{}",attachNames);
                for (String attachmentName: attachNames){
                    logger.debug("正在复制附件:{}",attachmentName);
                    Path attactFile = readerOld.getAttachmentFile(attachmentName);
                    //添加到新文件中
                    ofdDoc.addAttachment(new Attachment(attachmentName,attactFile));
                }
            }
            ofdDoc.close();
            reader.close();
            readerOld.close();
            //删除临时文件
            File file = new File(pdfFilePath+".tmp");
            file.delete();
            file = new File(pdfFilePath);
            file.delete();
            file = new File(ofdFilePath);
            file.delete();
            //生成的文件改名
            file = new File(ofdFilePath+".tmp");
            file.renameTo(new File(ofdFilePath));
            return new ResultJson();
        }catch (Exception e){
            logger.error("OFD文件替换pdf页面内容异常:",e);
            return new ResultJson(1003,"OFD文件替换pdf页面内容异常:"+e.getMessage());
        }
    }

}
