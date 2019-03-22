package com.http.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kaka.notice.Message;

/**
 *
 * @author zhoukai
 */
public class JsonMessage extends Message {

    public final HttpJsonRespWriter out;

    public JsonMessage(Object what, ObjectNode data, HttpJsonRespWriter out) {
        super(what, data);
        this.out = out;
    }

}
