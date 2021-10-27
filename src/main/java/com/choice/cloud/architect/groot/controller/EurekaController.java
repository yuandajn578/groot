package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.enums.EnvOnOffEnum;
import com.choice.cloud.architect.groot.enums.EurekaStatusEnum;
import com.choice.cloud.architect.groot.eureka.EurekaService;
import com.choice.cloud.architect.groot.eureka.ShutdownEurekaService;
import com.choice.cloud.architect.groot.eureka.dto.InstanceDTO;
import com.choice.cloud.architect.groot.eureka.dto.ServiceGroupInstanceDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.response.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName EurekaController
 * @Description eureka管理
 * @Author Guangshan Wang
 * @Date 2020/3/17/017 10:24
 */
@Api(value = "eureka管理", tags = "eureka管理")
@RestController
@RequestMapping("/api/eureka")
@Validated
@Slf4j
public class EurekaController {

    @Autowired
    private EurekaService eurekaService;
    @Autowired
    private ShutdownEurekaService shutdownEurekaService;

    @ApiOperation(value = "获取所有eureka的服务实例信息", notes = "获取所有eureka的服务实例信息")
    @GetMapping("/list")
    public ResponseData<List<ServiceGroupInstanceDTO>> allEureka(@RequestParam EnvOnOffEnum envOnOffEnum) {
        try {
            List<ServiceGroupInstanceDTO> ret = eurekaService.getAllService(envOnOffEnum);
            return ResponseData.createBySuccess(ret);
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("EurekaController allEureka 查询所有eureka信息出错 error:{}", e);
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "按服务分组名称查询", notes = "按服务分组名称查询")
    @GetMapping("/listByGroup")
    public ResponseData<List<ServiceGroupInstanceDTO>> findServiceByGroup(@RequestParam(required=false) String serverGroup, @RequestParam EnvOnOffEnum envOnOffEnum) {
        try {
            List<ServiceGroupInstanceDTO> ret = eurekaService.findServiceByGroup(serverGroup, envOnOffEnum);
            return ResponseData.createBySuccess(ret);
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("EurekaController allEureka 查询所有eureka信息出错 error:{}", e);
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "下线所有 没有服务分组的实例", notes = "下线所有 没有服务分组的实例")
    @GetMapping("/shutdownAllInstanceNoServiceGroup")
    public ResponseData shutdownAllInstanceNoServiceGroup() {
        try {
            shutdownEurekaService.shutdownAllInstanceNoServiceGroup();
            return ResponseData.createBySuccess();
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("EurekaController allEureka 查询所有eureka信息出错 error:{}", e);
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "下线实例", notes = "下线实例")
    @GetMapping("/shutdownInstance")
    public ResponseData shutdownInstance(@RequestParam String serverId, @RequestParam String instanceId) {
        try {
            boolean ret = shutdownEurekaService.updateInstance(serverId, instanceId, EurekaStatusEnum.OUT_OFF_SERVICE);
            return ResponseData.createBySuccess(ret);
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("EurekaController allEureka 查询所有eureka信息出错 error:{}", e);
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "上线实例", notes = "上线实例")
    @GetMapping("/upInstance")
    public ResponseData upInstance(@RequestParam String serverId, @RequestParam String instanceId) {
        try {
            boolean ret = shutdownEurekaService.updateInstance(serverId, instanceId, EurekaStatusEnum.UP);
            return ResponseData.createBySuccess(ret);
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("EurekaController allEureka 查询所有eureka信息出错 error:{}", e);
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "统计eureka数量", notes = "统计eureka数量")
    @GetMapping("/serverCount")
    public ResponseData serverCount() {
        System.out.println("-------------- offline--------------------");
        eurekaService.serverCount(EnvOnOffEnum.offline);
        System.out.println("-------------- online--------------------");
        eurekaService.serverCount(EnvOnOffEnum.online);
        return ResponseData.createBySuccess();

    }

}
