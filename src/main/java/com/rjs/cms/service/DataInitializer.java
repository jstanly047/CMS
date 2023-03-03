package com.rjs.cms.service;

import com.rjs.cms.service.db.RoleInfoCache;
import com.rjs.cms.service.db.TableMetaDataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner, Runnable{
    @Autowired
    private ThreadPoolTaskExecutor dbThreadPoolTaskExecutor;
    @Autowired
    private TableMetaDataCache tableMetaDataCache;

    @Autowired
    private RoleInfoCache roleInfoCache;

    private int threadInitCount = 0;

    @Override
    public void run(String... args) throws Exception{
        threadInitCount = dbThreadPoolTaskExecutor.getPoolSize();

        for (int i = 0; i < dbThreadPoolTaskExecutor.getPoolSize(); i++)
        {
            dbThreadPoolTaskExecutor.submit(this);
        }

        checkAllThreadAreDone();
    }


    @Override
    public void run(){
        tableMetaDataCache.populateCache();
        roleInfoCache.populateCache();
        checkAllThreadAreDone();
    }

    public synchronized void checkAllThreadAreDone(){
        threadInitCount--;

        while(threadInitCount > 0)
        {
            try {
                wait();
            }
            catch (InterruptedException e)
            {
                //CMSLogger.get().warn("Waiting failed!!!");
            }
        };

        notifyAll();
    }

}
