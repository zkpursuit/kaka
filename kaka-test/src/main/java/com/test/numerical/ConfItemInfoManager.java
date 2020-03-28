package com.test.numerical;

import com.kaka.numerical.TextNumericConfig;
import com.kaka.numerical.annotation.Numeric;
import com.test.numerical.beans.ConfItemInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 道具配置数据管理器
 *
 * @author zkpursuit
 */
@Numeric(src = "conf_item.txt")
public class ConfItemInfoManager extends TextNumericConfig<ConfItemInfo> {

    private final Map<Integer, ConfItemInfo> map = new ConcurrentHashMap<>();
    private final List<ConfItemInfo> list = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected String initDelimiter() {
        return "\t";
    }

    @Override
    protected void cacheObject(ConfItemInfo info) {
        map.put(info.getId(), info);
        list.add(info);
    }

    @Override
    protected void parseBefore() {
        map.clear();
        list.clear();
    }

    @Override
    protected void parseAfter() {
    }

    public ConfItemInfo getConfItemInfo(int id) {
        if (id <= 0) id = 1;
        return this.map.get(id);
    }

    public List<ConfItemInfo> getList() {
        return list;
    }

}
