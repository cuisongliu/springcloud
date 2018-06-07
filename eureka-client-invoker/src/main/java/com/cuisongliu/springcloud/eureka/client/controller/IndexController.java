package com.cuisongliu.springcloud.eureka.client.controller;
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 cuisongliu@qq.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 调用方controller
 *
 * @author cuisongliu [cuisongliu@qq.com]
 * @since 2018-06-06 下午10:15
 */
@Configuration
@RestController
public class IndexController {
    @Autowired
    public IndexController(DiscoveryClient client) {
        this.client = client;
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @RequestMapping(value = "/router",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String router(){

        RestTemplate restTpl = restTemplate();
        String json = restTpl.getForObject("http://service-provider/index/23",String.class);
        return json;
    }

    private final DiscoveryClient client;

    @RequestMapping(value = "/index",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String index(){
        //获取服务信息
        List<ServiceInstance> instances = instances();
        //输出服务信息以及状态
        for (ServiceInstance instance:instances){
            EurekaDiscoveryClient.EurekaServiceInstance esi = (EurekaDiscoveryClient.EurekaServiceInstance)instance;
            InstanceInfo info = esi.getInstanceInfo();
            System.out.println(info.getAppName() + "---" + info.getInstanceId() + "---" + info.getStatus());
        }
        List<ServiceInstance> ins = client.getInstances("eureka-server");
        for (ServiceInstance service:ins){
            System.out.println(service.getMetadata().get("asMap"));
        }

        return "";
    }

    private List<ServiceInstance> instances(){
        List<String> ids = client.getServices();
        List<ServiceInstance> result = new ArrayList<>();
        for (String id : ids) {
            result.addAll(client.getInstances(id));
        }
        return result;
    }

}
