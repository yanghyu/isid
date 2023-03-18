package com.github.yanghyu.isid.app.service.online.controller.ui.v1;

import com.github.yanghyu.isid.app.service.online.model.result.SysDateTime;
import com.github.yanghyu.isid.app.service.online.service.SysInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UiV1SysInfoController {

    @Resource
    private SysInfoService sysInfoService;

    @PostMapping("/ui/v1/sys-info/sys-date-time")
    public SysDateTime sysDateTime() {
        return sysInfoService.sysDateTime();
    }

}
