package com.github.yanghyu.isid.app.service.online.controller.external.v1;

import com.github.yanghyu.isid.app.service.online.model.result.SysDateTime;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
public class ExternalV1SysInfoController {

    @PostMapping("/external/v1/sys-info/sys-date-time")
    public SysDateTime sysDateTime() {
        SysDateTime sysDateTime = new SysDateTime();
        Instant sysInstant = Instant.now();
        ZoneId sysZoneId = ZoneId.systemDefault();
        sysDateTime.setSysInstant(sysInstant);
        sysDateTime.setSysZoneId(sysZoneId);
        sysDateTime.setSysLocalDateTime(LocalDateTime.ofInstant(sysInstant, sysZoneId));
        return sysDateTime;
    }

}
