package com.github.yanghyu.isid.app.service.online.service;

import com.github.yanghyu.isid.app.service.online.model.result.SysDateTime;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class SysInfoService {

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
