package com.wf.ew;

import com.yuanjing.framework.common.utils.LicenseUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.wf.jwtp.configuration.EnableJwtPermission;

@EnableJwtPermission
@SpringBootApplication
@EnableAsync
public class EasyWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyWebApplication.class, args);
        if(!LicenseUtil.checkLicense()){
            System.err.println("license不合法");
            System.exit(-1);
        }
    }
}
