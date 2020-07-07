package com.sdy.dataexchange.biz.core.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

/**
 * nacos实例服务
 * @author zhouziqiang 
 */
@Service
public class NacosService {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;
    private NamingService namingService;
    
    @PostConstruct
    public void init() throws NacosException {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.setProperty(PropertyKeyConst.NAMESPACE, namespace);
        namingService = NamingFactory.createNamingService(properties);
    }

    /**
     * 获取nacos实例列表
     */
    public List<Instance> listInstance() throws NacosException {
        return namingService.getAllInstances(appName, Constants.DEFAULT_GROUP);
    }
}
