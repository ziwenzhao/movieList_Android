package com.example.ziwenzhao.Utils;

import android.support.test.espresso.IdlingResource;

/**
 * Idling Resource container.
 */
public class EspressoIdlingResource {

    private static final String RESOURCE = "GLOBAL";

    private static SimpleCountingIdlingResource mCountingIdlingResource =
            new SimpleCountingIdlingResource(RESOURCE);

    public static void increment() {
        mCountingIdlingResource.increment();
    }

    public static void decrement() {
        mCountingIdlingResource.decrement();
    }

    public static IdlingResource getIdlingResource() {
        return mCountingIdlingResource;
    }

    public static boolean isIdle() {
        return mCountingIdlingResource.isIdleNow();
    }
}
