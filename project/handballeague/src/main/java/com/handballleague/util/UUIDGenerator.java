package com.handballleague.util;

import java.util.UUID;

public class UUIDGenerator {
    public static long generateRandomLongUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.getMostSignificantBits() & Long.MAX_VALUE;
    }
}
