package com.akshansh.timecapsulebackend.util;

import org.springframework.stereotype.Component;

@Component
public class SlugUtil {
    public static String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
