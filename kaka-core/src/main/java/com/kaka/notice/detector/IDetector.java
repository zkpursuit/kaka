package com.kaka.notice.detector;

/**
 * 类识别器
 *
 * @author zkpursuit
 */
public interface IDetector {

    /**
     * 识别器的名称
     *
     * @return 识别器名称
     */
    String name();

    /**
     * 类识别
     *
     * @param cls 待识别的类
     * @return true表示被正确识别
     */
    boolean discern(Class<?> cls);
}
