package com.kaka.util;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 时间线
 *
 * @author zkpursuit
 */
public class Timeline implements Runnable {

    public interface FrameTask {

        void onEnterFrame();
    }

    private final List<FrameTask> tasks = new CopyOnWriteArrayList<>();

    /**
     * 每帧运行的事件，纳秒
     */
    private final long frameDelayTime;
    private final TimeUnit unit = TimeUnit.NANOSECONDS;

    /**
     * @param fp 帧频
     */
    public Timeline(int fp) {
        this.frameDelayTime = (1000L / fp) * 1000000L;
    }

    public void addFrameTask(FrameTask task) {
        tasks.add(task);
    }

    public void removeFrameTask(FrameTask task) {
        tasks.remove(task);
    }

    @Override
    public void run() {
        while (true) {
            if (tasks.isEmpty()) {
                continue;
            }
            int size = tasks.size();
            Iterator<FrameTask> iter = tasks.iterator();
            while (iter.hasNext()) {
                long nano = System.nanoTime();
                //执行业务
                long offset = System.nanoTime() - nano;
                long avage = frameDelayTime / size; // 1帧所需要的时间/任务个数
                if (offset < avage) {
                    try {
                        unit.sleep(avage);
                    } catch (InterruptedException ex) {
                    }
                    FrameTask task = iter.next();
                    task.onEnterFrame();
                }
            }
        }
    }

}
