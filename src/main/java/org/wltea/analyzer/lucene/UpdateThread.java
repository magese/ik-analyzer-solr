/*
 * IK 中文分词  版本 8.3.0
 * IK Analyzer release 8.3.0
 *
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
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 *
 * 8.3.0版本 由 Magese (magese@live.cn) 更新
 * release 8.3.0 update by Magese(magese@live.cn)
 *
 */
package org.wltea.analyzer.lucene;

import java.io.IOException;
import java.util.Vector;

/**
 * 更新扩展词典子线程类
 */
public class UpdateThread implements Runnable {
    private static final long INTERVAL = 30000L;                            // 循环等待时间
    private Vector<UpdateJob> filterFactorys;                               // 更新任务集合

    /**
     * 私有化构造器，阻止外部进行实例化
     */
    private UpdateThread() {
        this.filterFactorys = new Vector<>();
        Thread worker = new Thread(this);
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * 静态内部类，实现线程安全单例模式
     */
    private static class Builder {
        private static UpdateThread singleton = new UpdateThread();
    }

    /**
     * 获取本类的实例
     * 线程安全单例模式
     *
     * @return 本类的实例
     */
    static UpdateThread getInstance() {
        return UpdateThread.Builder.singleton;
    }

    /**
     * 将运行中的IK分词工厂实例注册到本类定时任务中
     *
     * @param filterFactory 运行中的IK分词器
     */
    void register(UpdateJob filterFactory) {
        this.filterFactorys.add(filterFactory);
    }

    /**
     * 子线程执行任务
     */
    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 如果更新字典任务集合不为空
            if (!this.filterFactorys.isEmpty()) {
                // 进行循环并执行更新
                for (UpdateJob factory : this.filterFactorys) {
                    try {
                        factory.update();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public interface UpdateJob {
        void update() throws IOException;
    }
}
