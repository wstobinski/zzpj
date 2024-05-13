package com.handballleague.util;

import java.util.UUID;

public class UUIDGenerator {
    public static long generateRandomIntegerUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.getMostSignificantBits() & Integer.MAX_VALUE;
    }
}
