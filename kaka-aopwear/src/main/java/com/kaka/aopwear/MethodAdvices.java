package com.kaka.aopwear;

import java.util.List;

/**
 * 被切面代理的方法通知集合
 *
 * @author zkpursuit
 */
class MethodAdvices {

    List<MethodWrap> before;
    List<MethodWrap> after;
    List<MethodWrap> afterReturning;
    List<MethodWrap> afterThrowing;
    List<MethodWrap> around;
}
