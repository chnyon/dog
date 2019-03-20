package org.dog.core.aop;

import org.dog.core.annotation.DogTccAnnotation;
import org.dog.core.ApplicationAutoConfig;
import org.dog.core.entry.DogTcc;
import org.dog.core.tccserver.ITccServer;
import org.dog.core.util.ThreadManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Aspect
@PropertySource(value = {"classpath:application.properties"})
@ConfigurationProperties(prefix = "application")
public class DogTccAop {

    private static Logger logger = Logger.getLogger(DogTccAop.class);

    @Autowired
    ITccServer server;

    @Autowired
    private ApplicationAutoConfig applicationAutoConfig;

    @Around("@annotation(org.dog.core.annotation.DogTccAnnotation)  && @annotation(ad) ")
    public Object doAroundtransaction(ProceedingJoinPoint pjp, DogTccAnnotation ad) throws Throwable {


        if(ThreadManager.exsit()){

            return  pjp.proceed();


        }else{

            DogTcc transaction = new DogTcc(applicationAutoConfig.getApplicationname(),ad.name());

            logger.info("createTransaction:"+transaction.toString());

            ThreadManager.setTransaction(transaction);

            return  server.tccTry(transaction,pjp);

        }
    }

}

