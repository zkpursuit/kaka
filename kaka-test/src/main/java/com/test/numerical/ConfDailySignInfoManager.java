package com.test.numerical;

import com.http.util.JsonUtils;
import com.kaka.numerical.TextNumericConfig;
import com.kaka.numerical.annotation.Numeric;
import com.test.numerical.beans.ConfDailySignInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 七日签到数据管理器
 *
 * @author zkpursuit
 */
@Numeric(src = "conf_dailySign.txt")
public class ConfDailySignInfoManager extends TextNumericConfig<ConfDailySignInfo> {

    public final Map<Integer, ConfDailySignInfo> map = new ConcurrentHashMap<>();
    public final List<ConfDailySignInfo> list = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected String initDelimiter() {
        return "\t";
    }

    @Override
    protected void cacheObject(ConfDailySignInfo info) {
        list.add(info);
        map.put(info.getId(), info);
        System.out.println(JsonUtils.toJsonString(info));
    }

    @Override
    protected void parseBefore() {
        map.clear();
        list.clear();
    }

    @Override
    protected void parseAfter() {

    }

    public List<ConfDailySignInfo> getList() {
        return list;
    }
}
