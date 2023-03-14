package com.github.yanghyu.isid.app.service.online.model.result;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class SysDateTime {

    private Instant sysInstant;

    private ZoneId sysZoneId;

    private LocalDateTime sysLocalDateTime;

}
