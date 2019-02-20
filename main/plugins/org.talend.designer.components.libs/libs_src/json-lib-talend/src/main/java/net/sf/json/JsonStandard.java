package net.sf.json;

public enum JsonStandard {
    /**
     * Default standard used to exist before
     */
    DEFAULT,

    /**
     * Updated standard due to RFC 7159 to not unwrap "null" strings (keep quotations)
     */
    WRAP_NULL_STRINGS

}
