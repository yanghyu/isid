package com.github.yanghyu.isid.app.service.online.controller.external.v1;

import com.github.yanghyu.isid.app.service.online.model.result.SysDateTime;
import com.github.yanghyu.isid.app.service.online.service.SysInfoService;
import com.github.yanghyu.isid.common.core.generator.IdGenerator;
import com.github.yanghyu.isid.common.core.message.base.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ExternalV1SysInfoController {

    @Resource
    private SysInfoService sysInfoService;

    @Resource
    private IdGenerator idGenerator;

    @PostMapping("/external/v1/sys-info/sys-date-time")
    public SysDateTime sysDateTime() {
        return sysInfoService.sysDateTime();
    }

    @PostMapping("/external/v1/sys-info/generate-id")
    public Result<String> generateId() {
        return idGenerator.generateId("external");
    }

}
