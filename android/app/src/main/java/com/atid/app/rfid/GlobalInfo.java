package com.atid.app.rfid;

import com.atid.lib.dev.rfid.type.TagType;

public class GlobalInfo {

    private static final int READER_LOG_LEVEL = 1;

    private static TagType smTagType = TagType.Tag6C;
    private static boolean smIsDisplayPc = true;
    private static boolean smIsContinuousMode = true;
    private static int smLogLevel = 0;

    public static TagType getTagType() {
        return smTagType;
    }

    public static void setTagType(TagType type) {
        smTagType = type;
    }

    public static boolean isDisplayPc() {
        return smIsDisplayPc;
    }

    public static void setDisplayPc(boolean enabled) {
        smIsDisplayPc = enabled;
    }

    public static boolean isContinuousMode() {
        return smIsContinuousMode;
    }

    public static void setContinuousMode(boolean enabled) {
        smIsContinuousMode = enabled;
    }

    public static void setLogLevel(int level) {
        smLogLevel = level;
    }

    public static int getLogLevel() {
        return smLogLevel;
    }

    public static boolean isLog(int level) {
        return smLogLevel >= level;
    }

    public static int getReaderLogLevel() {
        return smLogLevel > READER_LOG_LEVEL ? smLogLevel - READER_LOG_LEVEL : 0;
    }
}
