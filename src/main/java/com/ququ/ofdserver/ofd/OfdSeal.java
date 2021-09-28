package com.ququ.ofdserver.ofd;

import com.ququ.common.result.ResultJson;
import com.ququ.ofdserver.service.SealService;
import org.ofdrw.gm.cert.PKCS12Tools;
import org.ofdrw.gm.ses.v4.SESeal;
import org.ofdrw.reader.OFDReader;
import org.ofdrw.sign.NumberFormatAtomicSignID;
import org.ofdrw.sign.OFDSigner;
import org.ofdrw.sign.SignMode;
import org.ofdrw.sign.signContainer.SESV4Container;
import org.ofdrw.sign.stamppos.NormalStampPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.Certificate;

@Service
@Configuration
public class OfdSeal {

    private Logger logger = LoggerFactory.getLogger(SealService.class);

    @Value("${seal.p12}")
    private String p12File;

    @Value("${seal.esl}")
    private String eslFile;

    @Value("${seal.password}")
    private String keyPassword;

    public ResultJson sealOfd(String ofdFilePath, Integer page, Double x, Double y, Integer[] pages, Double[] xx, Double[] yy) {
        Path p12Path = Paths.get(p12File);
        Path eslPath = Paths.get(eslFile);
        try {
            PrivateKey prvKey = PKCS12Tools.ReadPrvKey(p12Path, "private", keyPassword);
            Certificate signCert = PKCS12Tools.ReadUserCert(p12Path, "private", keyPassword);
            SESeal seal = SESeal.getInstance(Files.readAllBytes(eslPath));

            Path src = Paths.get(ofdFilePath);
            Path out = Paths.get(ofdFilePath + ".tmp");

            // 1. 构造签名引擎
            OFDReader reader = new OFDReader(src);
            OFDSigner signer = new OFDSigner(reader, out, new NumberFormatAtomicSignID());
            SESV4Container signContainer = new SESV4Container(prvKey, seal, signCert);
            // 2. 设置签名模式
            signer.setSignMode(SignMode.WholeProtected);
            // 3. 设置签名使用的扩展签名容器
            signer.setSignContainer(signContainer);
            // 4. 设置显示位置
            if (page != null)
                signer.addApPos(new NormalStampPos(page, x, y, 40, 40));
            else if (pages != null) {
                for (int i = 0; i < pages.length; i++)
                    signer.addApPos(new NormalStampPos(pages[i], xx[i], yy[i], 40, 40));
            }
            // 5. 执行签名
            signer.exeSign();
            // 6. 关闭签名引擎，生成文档。
            signer.close();
            reader.close();
            return new ResultJson();
        } catch (Exception e) {
            logger.error("盖章异常:", e);
            return new ResultJson(1008, "盖章异常:" + e.getMessage());
        }

    }

}
