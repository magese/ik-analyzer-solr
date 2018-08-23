/*
 * IK 中文分词  版本 7.4
 * IK Analyzer release 7.4
 * update by Magese(magese@live.cn)
 */
package org.wltea.analyzer.lucene;

import java.io.IOException;
import java.util.Vector;

/**
 * 更新扩展词典子线程类
 */
public class UpdateKeeper implements Runnable {
    private static final long INTERVAL = 30000L;                            // 循环等待时间
    private Vector<UpdateJob> filterFactorys;                               // 更新任务集合

    /**
     * 私有化构造器，阻止外部进行实例化
     */
    private UpdateKeeper() {
        this.filterFactorys = new Vector<>();
        Thread worker = new Thread(this);
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * 静态内部类，实现线程安全单例模式
     */
    private static class Builder {
        private static UpdateKeeper singleton = new UpdateKeeper();
    }

    /**
     * 获取本类的实例
     * 线程安全单例模式
     *
     * @return 本类的实例
     */
    static UpdateKeeper getInstance() {
        return UpdateKeeper.Builder.singleton;
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
