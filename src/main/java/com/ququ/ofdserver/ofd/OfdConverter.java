package com.ququ.ofdserver.ofd;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.ququ.common.result.ResultJson;
import org.ofd.render.OFDRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class OfdConverter {

    private Logger logger = LoggerFactory.getLogger(OfdConverter.class);
    /* 转PDF格式值 */
    private static final int wdFormatPDF = 17;

    public ResultJson convertPDFToOFD(String pdfFilePath,String ofdFilePath){
        Path pdfIn = Paths.get(pdfFilePath);
        Path ofdout = Paths.get(ofdFilePath);
        try {
            logger.debug("开始转换PDF文件{}到{}",pdfFilePath,ofdFilePath);
            OFDRender.convertPdfToOfd(Files.newInputStream(pdfIn), Files.newOutputStream(ofdout));
            return new ResultJson();
        }catch (Exception e){
            logger.error("PDF文件转OFD异常:",e);
            return new ResultJson(1003,"PDF文件转OFD异常:"+e.getMessage());
        }
    }

    public ResultJson convertWordToOFD(String wordFilePath,String ofdFilePath) {
        String pdfFilePath = wordFilePath+".pdf";
        ResultJson toPdf = convertWordToPdf(wordFilePath,pdfFilePath);
        if (toPdf.getStatus() != 1)
            return toPdf;
        ResultJson resultJson = convertPDFToOFD(pdfFilePath,ofdFilePath);
//        File file = new File(pdfFilePath);
//        file.delete();
        return resultJson;
    }

    public ResultJson convertWordToPdf(String wordFilePath,String pdfFilePath){
        try {
            InputStream is = OfdConverter.class.getClassLoader().getResourceAsStream("license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            Document docxFile = new Document(wordFilePath);
            docxFile.save(pdfFilePath, SaveFormat.PDF);
            return new ResultJson();
        }catch (Exception e){
            logger.error("word转pdf异常:",e);
            return new ResultJson(1010,"Word文件转PDF异常");
        }
    }

}
