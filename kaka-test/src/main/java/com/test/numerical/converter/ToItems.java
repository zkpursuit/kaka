package com.test.numerical.converter;

import com.kaka.numerical.annotation.NumericField;
import com.test.numerical.beans.Item;

/**
 * @author zhoukai
 */
public class ToItems implements NumericField.Converter<Item[]> {

    @Override
    public Item[] transform(String data) {
        if (data == null) {
            return null;
        }
        if ("".equals(data)) {
            return null;
        }
        int index = data.indexOf(";");
        data = data.substring(index + 1);
        String[] strs = data.split("[;,，]");
        Item[] array = new Item[strs.length];
        for (int i = 0; i < array.length; i++) {
            String str = strs[i];
            array[i] = ToItem.toItem(str);
        }
        return array;
    }

}
