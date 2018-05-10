/*
 * IK 中文分词  版本 7.0
 * IK Analyzer release 7.0
 * update by 高志成(magese@live.cn)
 */
package org.wltea.analyzer.lucene;

import java.io.IOException;
import java.util.Vector;

public class UpdateKeeper implements Runnable {
    private static final long INTERVAL = 60000L;
    private static UpdateKeeper singleton;
    private Vector<UpdateJob> filterFactorys;

    private UpdateKeeper() {
        this.filterFactorys = new Vector<>();

        Thread worker = new Thread(this);
        worker.setDaemon(true);
        worker.start();
    }

    static UpdateKeeper getInstance() {
        if (singleton == null) {
            synchronized (UpdateKeeper.class) {
                if (singleton == null) {
                    singleton = new UpdateKeeper();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    void register(UpdateJob filterFactory) {
        this.filterFactorys.add(filterFactory);
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!this.filterFactorys.isEmpty()) {
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
