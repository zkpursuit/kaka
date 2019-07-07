package com.kaka.notice;

import com.kaka.util.ObjectPool;

/**
 * {@link com.kaka.notice.Command}对象池
 * <br> 此类在类包外不可访问
 *
 * @author zkpursuit
 */
class CommandPool extends ObjectPool<Command> {

    private final Facade context;
    final Class<? extends Command> cls;

    CommandPool(Facade context, int maxSize, Class<? extends Command> cls) {
        super(maxSize);
        this.context = context;
        this.cls = cls;
    }

    @Override
    protected Command newObject() {
        return (Command) this.context.createObject(cls);
    }
}
