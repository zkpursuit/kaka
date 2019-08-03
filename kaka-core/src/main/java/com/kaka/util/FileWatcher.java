package com.kaka.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 文件变动监控
 *
 * @author zhoukai
 */
public class FileWatcher implements Runnable {

    private final WatchService watcher = FileSystems.getDefault().newWatchService();
    private boolean watchFlag;
    private final WatchCallBack callback;

    /**
     * 构造方法
     *
     * @param watchDir 监控目录
     * @param descendants 是否监控目录下的子孙目录
     * @param callback 文件监控回调
     * @throws IOException IO流异常
     */
    public FileWatcher(String watchDir, boolean descendants, WatchCallBack callback) throws IOException {
        this.callback = callback;
        if (descendants) {
            File dir = new File(watchDir);
            List<File> dirs = FileUtils.getDirectories(dir, true);
            int size = dirs.size();
            for (int i = 0; i < size; i++) {
                String absPath = dirs.get(i).getAbsolutePath();
                Path path = Paths.get(absPath);
                path.register(watcher, ENTRY_CREATE, OVERFLOW, ENTRY_MODIFY, ENTRY_DELETE);
            }
        } else {
            Path path = Paths.get(watchDir);
            path.register(watcher, ENTRY_CREATE, OVERFLOW, ENTRY_MODIFY, ENTRY_DELETE);
        }
        watchFlag = true;
    }

    /**
     * 停止文件监控
     */
    public void stop() {
        watchFlag = false;
    }

    public void close() throws IOException {
        stop();
        watcher.close();
    }

    @Override
    public void run() {
        while (watchFlag) {
            if (watcher == null) {
                return;
            }
            WatchKey signal;
            try {
                signal = watcher.take();
                Path watchDirPath = (Path) signal.watchable();
                signal.pollEvents().stream().forEach((WatchEvent<?> event) -> {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path fileNamePath = (Path) event.context();
                    if (callback != null) {
                        callback.call(kind, watchDirPath, fileNamePath);
                    }
                });
                signal.reset();
            } catch (InterruptedException ex) {
                Logger.getLogger(FileWatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * 文件变动回调
     */
    public interface WatchCallBack {

        /**
         * 监控文件变动的回调
         *
         * @param kind 文件变动的类型
         * @param watchDirPath 变化的目录
         * @param fileNamePath 变化的文件
         */
        void call(WatchEvent.Kind<?> kind, Path watchDirPath, Path fileNamePath);

    }

}
