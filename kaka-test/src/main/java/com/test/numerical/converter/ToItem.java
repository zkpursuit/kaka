package com.test.numerical.converter;

import com.kaka.numerical.annotation.NumericField;
import com.test.numerical.beans.Item;

/**
 * @author zhoukai
 */
public class ToItem implements NumericField.Converter<Item> {

    @Override
    public Item transform(String data) {
        return toItem(data);
    }

    public final static Item toItem(String data) {
        if (data == null) {
            return null;
        }
        if ("".equals(data)) {
            return null;
        }
        if ("0".equals(data)) {
            return null;
        }
        if (data.length() == 0) {
            return null;
        }
        String[] strs = data.split("#");
        int cid = Integer.parseInt(strs[0]);
        int num = Integer.parseInt(strs[1]);
        return new Item(cid, num);
    }
}
