package com.akshansh.timecapsulebackend.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SlugUtil {
    public static String generateSlug(String title) {
        Random random = new Random();

        char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o',
        'p','q','r','s','t','u','v','w','x','y','z','1','2','3','4','5','6','7','8','9','0'};

        int size = 6;

        String halfSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();

        return halfSlug + "-" + NanoIdUtils.randomNanoId(random, alphabet, size);
    }
}
