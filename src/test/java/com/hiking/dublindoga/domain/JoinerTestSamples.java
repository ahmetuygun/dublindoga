package com.hiking.dublindoga.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class JoinerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Joiner getJoinerSample1() {
        return new Joiner().id(1L).fullName("fullName1").email("email1").phone("phone1").point(1);
    }

    public static Joiner getJoinerSample2() {
        return new Joiner().id(2L).fullName("fullName2").email("email2").phone("phone2").point(2);
    }

    public static Joiner getJoinerRandomSampleGenerator() {
        return new Joiner()
            .id(longCount.incrementAndGet())
            .fullName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .point(intCount.incrementAndGet());
    }
}
