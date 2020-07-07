package com.sdy.dataexchange.web.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzq on 2019/1/11.
 */
@Configuration
public class TransactionConfig {
    @Autowired
    private DataSource dataSource;

    /**
     * 事务管理器
     *
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        transactionManager.setNestedTransactionAllowed(true);
        return transactionManager;
    }

    /**
     * 自定义事务拦截
     * @return
     */
    @Bean
    public TransactionInterceptor customTransactionInterceptor() {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

        // 只读事务
        DefaultTransactionAttribute readOnlyAttribute = new DefaultTransactionAttribute();
        readOnlyAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        readOnlyAttribute.setReadOnly(true);

        // 一般事务
        RuleBasedTransactionAttribute commonAttribute = new RuleBasedTransactionAttribute();
        commonAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        commonAttribute.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));

        Map<String, TransactionAttribute> m = new HashMap<>();
        m.put("select*", readOnlyAttribute);
        m.put("find*", readOnlyAttribute);
        m.put("get*", readOnlyAttribute);
        m.put("query*", readOnlyAttribute);
        m.put("*", commonAttribute);
        source.setNameMap(m);
        return new TransactionInterceptor(transactionManager(), source);
    }

    /**
     * 这个Bean会引发很多 Bean is not eligible for getting processed by all BeanPostProcessors 的Info
     * 可能是因为这个Advisor在一些BeanPostProcessor之前初始化
     * @return
     */
    @Bean
    public Advisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.sdy.dataexchange.biz.service.*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, customTransactionInterceptor());
    }

//    @Bean
//    public DefaultAdvisorAutoProxyCreator txAdvisorCreator() {
//        DefaultAdvisorAutoProxyCreator txAdvisorCreator = new DefaultAdvisorAutoProxyCreator();
////        txAdvisorCreator.settra
//    }

//    @Bean
//    public TransactionAttributeSourceAdvisor taSourceAdvisor() {
//        TransactionAttributeSourceAdvisor taSourceAdvisor = new TransactionAttributeSourceAdvisor();
//        taSourceAdvisor.setTransactionInterceptor(customTransactionInterceptor());
//        taSourceAdvisor.setClassFilter(new AnnotationClassFilter(Service.class));
//        return taSourceAdvisor;
//    }

//    @Bean
//    public BeanNameAutoProxyCreator transactionAutoProxy() {
//        BeanNameAutoProxyCreator transactionAutoProxy = new BeanNameAutoProxyCreator();
//        transactionAutoProxy.setProxyTargetClass(true);
//        transactionAutoProxy.setBeanNames("com.sdy.dataexchange.auth.service.*ServiceImpl");
//        transactionAutoProxy.setInterceptorNames("customTransactionInterceptor");
//        return transactionAutoProxy;
//    }
//
//    @Bean
//    public AspectJExpressionPointcutAdvisor pointcutAdvisor(){
//        AspectJExpressionPointcutAdvisor pointcutAdvisor = new AspectJExpressionPointcutAdvisor();
//        pointcutAdvisor.setAdvice(customTransactionInterceptor());
//        pointcutAdvisor.setExpression("execution(* com.sdy.dataexchange.auth.service.*.*(..))");
//        return pointcutAdvisor;
//    }
}
