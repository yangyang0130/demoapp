package com.yangyang.unmanneddrone.helper;

public final class Constants{

    public Constants() {
    }

    private static class SingletonHolder {
        private static final Constants INSTANCE = new Constants();
    }

    public static Constants getInstance() {
        return Constants.SingletonHolder.INSTANCE;
    }

    /**
     *  轮盘的点击方向
     */
    public final int Click_LEFT_ARROW = 0x0001;
    public final int Click_UP_ARROW = 0x0002;
    public final int Click_RIGHT_ARROW = 0x0003;
    public final int Click_DOWN_ARROW = 0x0004;
    public final int Click_CENTER_ARROW = 0x0005;

}
