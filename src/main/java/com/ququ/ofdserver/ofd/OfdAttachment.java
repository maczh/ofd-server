package com.ququ.ofdserver.ofd;

import com.ququ.common.result.ResultJson;
import org.ofdrw.layout.OFDDoc;
import org.ofdrw.layout.edit.Attachment;
import org.ofdrw.reader.OFDReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class OfdAttachment {
    private Logger logger = LoggerFactory.getLogger(OfdAttachment.class);

    public ResultJson attachFileIntoOFD(String ofdFilePath, String attachFilepath, String attachmentName) {
        Path ofdIn = Paths.get(ofdFilePath);
        Path ofdout = Paths.get(ofdFilePath + ".tmp");
        try {
            //添加附件，另存为临时文件
            OFDReader reader = new OFDReader(ofdIn);
            OFDDoc ofdDoc = new OFDDoc(reader, ofdout);
            ofdDoc.addAttachment(new Attachment(attachmentName, Paths.get(attachFilepath)));
            ofdDoc.close();
            reader.close();
            //删除原文件
            File file = new File(ofdFilePath);
            file.delete();
            file = new File(attachFilepath);
            file.delete();
            //将临时文件改名成原文件名
            file = new File(ofdFilePath + ".tmp");
            file.renameTo(new File(ofdFilePath));
            return new ResultJson();
        } catch (Exception e) {
            logger.error("OFD文件添加附件异常:", e);
            return new ResultJson(1005, "OFD文件添加附件异常::" + e.getMessage());
        }
    }


    public ResultJson removeAttachFromOFD(String ofdFilePath, String attachmentName) {
        Path ofdIn = Paths.get(ofdFilePath);
        Path ofdout = Paths.get(ofdFilePath + ".tmp");
        try {
            //添加附件，另存为临时文件
            OFDReader reader = new OFDReader(ofdIn);
            OFDDoc ofdDoc = new OFDDoc(reader, ofdout);
            ofdDoc.removeAttachment(attachmentName);
            ofdDoc.close();
            reader.close();
            //删除原文件
            File file = new File(ofdFilePath);
            file.delete();
            //将临时文件改名成原文件名
            file = new File(ofdFilePath + ".tmp");
            file.renameTo(new File(ofdFilePath));
            return new ResultJson();
        } catch (Exception e) {
            logger.error("OFD文件删除附件异常:", e);
            return new ResultJson(1005, "OFD文件删除附件异常::" + e.getMessage());
        }
    }


}
