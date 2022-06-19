package com.github.eoniz.nexus.core.utils;

public class RoomUtils {
    private static final Integer MAX_ROOM_CHARS = 6;
    private static final String[] ROOM_ALLOWED_CHARS = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z"
    };

    public static String generateRoomId() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < MAX_ROOM_CHARS; i++) {
            int charIndex = (int) Math.round(Math.random() * (ROOM_ALLOWED_CHARS.length - 1));
            stringBuilder.append(ROOM_ALLOWED_CHARS[charIndex]);
        }

        return stringBuilder.toString();
    }
}
