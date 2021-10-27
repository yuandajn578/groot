package com.choice.cloud.architect.groot.remote.milkyway;

import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.remote.*;
import com.choice.cloud.architect.groot.response.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "milky-way",fallbackFactory = DefaultClientFallbackFactory.class)
public interface MilkywayClient {

    /**
     * 获取应用列表
     * @param paramWebPage
     * @return
     */
    @PostMapping("/application/list")
    ResponseData<WebPage<List<AppListResponse>>> appList(@RequestBody WebPage<AppListRequest> paramWebPage);

    @GetMapping("/open/api/application/info")
    ResponseData<AppListResponse> getApplicationInfo(@RequestParam("appCode") String appCode);

    /**
     * 根据人员查询应用
     */
    @PostMapping("/application/listByUser")
    ResponseData<List<AppListResponse>> listAppByUser(@RequestBody ApplicationListByUserParam param);

    @PostMapping("/application/listByAppCode")
    ResponseData<List<AppListResponse>> listByAppCode(@RequestBody ApplicationListByAppCodeParam param);

    @GetMapping("/application/countByRank")
    ResponseData<Integer> countByRank(@RequestParam("rank") String rank);


    @PostMapping("/architect/info")
    ResponseData<DomainArchitectureDTO> getDomainArchitectureInfo(@RequestBody SingleOidParam param);

    /**
     * 根据appCode获取架构域信息
     * 因为是消费消息触发的feign调用，所以拦截器中获取不到token，这里就显示进行传过去
     * @param token
     * @param param
     * @return
     */
    @PostMapping("/architect/getInfoByAppCode")
    ResponseData<GetInfoByAppCodeResponseDTO> getDomainArchitectureInfoByAppCode(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token, @RequestBody GetInfoByAppCodeRequestDTO param);

    /**
     * 根据appCode查询应用配置信息
     * @param appCode
     * @return
     */
    @GetMapping("/appconfig/getInfoByAppCode")
    ResponseData<AppConfigInfoResponseDTO> getAppConfigInfoByAppCode(@RequestParam("appCode") String appCode);

    /**
     * 根据架构域oid查询架构域信息
     * @param token
     * @param param
     * @return
     */
    @PostMapping("/architect/info")
    ResponseData<DomainArchitectureDTO> getDomainArchitectureInfoByOid(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token, @RequestBody DomainArchitectureParamId param);

    /**
     * 根据应用code查询应用详情
     * @param token
     * @param param
     * @return
     */
    @PostMapping("/application/info")
    ResponseData<ApplicationDTO> getApplicationInfoByAppCode(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token, @RequestBody ApplicationParamAppCode param);
}
