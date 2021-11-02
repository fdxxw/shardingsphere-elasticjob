/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.elasticjob.lite.internal.setup;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.infra.listener.ElasticJobListener;
import org.apache.shardingsphere.elasticjob.lite.internal.config.ConfigurationService;
import org.apache.shardingsphere.elasticjob.lite.internal.election.LeaderService;
import org.apache.shardingsphere.elasticjob.lite.internal.instance.InstanceService;
import org.apache.shardingsphere.elasticjob.lite.internal.listener.ListenerManager;
import org.apache.shardingsphere.elasticjob.lite.internal.reconcile.ReconcileService;
import org.apache.shardingsphere.elasticjob.lite.internal.server.ServerService;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;

import java.util.Collection;

/**
 * Set up facade.
 */
public final class SetUpFacade {
    
    private final ConfigurationService configService;
    
    private final LeaderService leaderService;
    
    private final ServerService serverService;
    
    private final InstanceService instanceService;
    
    private final ReconcileService reconcileService;
    
    private final ListenerManager listenerManager;
    
    public SetUpFacade(final CoordinatorRegistryCenter regCenter, final String jobName, final Collection<ElasticJobListener> elasticJobListeners) {
        configService = new ConfigurationService(regCenter, jobName);
        leaderService = new LeaderService(regCenter, jobName);
        serverService = new ServerService(regCenter, jobName);
        instanceService = new InstanceService(regCenter, jobName);
        reconcileService = new ReconcileService(regCenter, jobName);
        listenerManager = new ListenerManager(regCenter, jobName, elasticJobListeners);
    }
    
    /**
     * Set up job configuration.
     *
     * @param jobClassName job class name
     * @param jobConfig job configuration to be updated
     * @return accepted job configuration
     */
    public JobConfiguration setUpJobConfiguration(final String jobClassName, final JobConfiguration jobConfig) {
        return configService.setUpJobConfiguration(jobClassName, jobConfig);
    }
    
    /**
     * Register start up info.
     * 
     * @param enabled enable job on startup
     */
    public void registerStartUpInfo(final boolean enabled) {
        //开启所有监听器.  主节点、分片、作业运行、触发等节点状态变更
        /**
         * 监听器主要是用来订阅调度作业写入到Zookeeper上节点状态的变更，
         * 其中包含了主节点，分片信息，作业运行信息，触发信息等节点状态的监听，
         * 在分布式场景下如果有其他机器下的作节点状态发生了变更或者针对作业进行了操作，
         * 当前进行订阅的进程节点可以及时感知到并及时做出合理的操作。
         */
        listenerManager.startAllListeners();
        //选举主节点
        /**
         * 这里主要说下主节点的作用，调度作业的执行是基于逻辑的分片来执行的不依赖于底层机器实例，
         * 而每个机器进程的分片获取是需要主节点来进行分配的，
         * 就像是有一群员工需要进行任务分配了，每个人都不想做任务，
         * 或者都想要做更多的任务，每个人自己来选择执行任务数量，最终是无法形成一致的意见的，
         * 这个时候就需要选出来一个领导，领导分配任务给员工，选主节点就类似于选领导一样。
         */
        leaderService.electLeader();
        //持久化作业服务器上线信息
        /**
         * 这里一共有两个存储机器IP信息的节点在Zookeeper上创建，一个是持久的servers子节点，
         * 一个是临时的instances子节点，持久的servers子节点用来存储作业实例信息的状态比如禁用还是启用，
         * 而临时的instances节点可以用来标示哪个机器的作业进程在运行，在线的进程与Zookeeper保持连接临时节点存在，
         * 下线的进程则临时节点被自动移除。
         */
        serverService.persistOnline(enabled);
        //持久化作业运行实例上线相关信息
        instanceService.persistOnline();
        //调解分布式作业不一致状态服务
        /**
         * 这一步主要做一些补偿操作开启一个定时任务检测，作业节点状态是否正常，来做一些调节操作，保证作业正常运行。
         */
        if (!reconcileService.isRunning()) {
            reconcileService.startAsync();
        }
    }
    
    /**
     * Tear down.
     */
    public void tearDown() {
        if (reconcileService.isRunning()) {
            reconcileService.stopAsync();
        }
    }
}
