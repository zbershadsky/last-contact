/* Copyright (c) 2010 Dmitry Lisay <pingw33n@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */
package net.emphased.vkclient;

import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link VkApiThrottler} implementation based on
 * limit of requests per {@link VkDefaultApiThrottler#UNIT}.
 */
public class VkDefaultApiThrottler implements VkApiThrottler
{
    /**
     * Interval in milliseconds on which requests is limited.
     */
    public static long UNIT = 1000L; // 1 second.
    public static final long DEFAULT_WAIT_BIAS = 250;

    /**
     * Returns number of request maximum allowed per {@link UNIT}.
     * @return limit value.
     */
    public int getLimit()
    {
        return _limit;
    }

    /**
     * Sets limit.
     * @param limit limit value.
     */
    public void setLimit(int limit)
    {
        _limit = limit;
    }

    /**
     * Returns number of milliseconds that are added to value returned by {@link #throttle}.
     * @return number of milliseconds.
     */
    public long getWaitBias()
    {
        return _waitBias;
    }

    /**
     * Sets number of milliseconds that are added to value returned by {@link #throttle}.
     * @param waitBias value to set.
     */
    public void setWaitBias(long waitBias)
    {
        _waitBias = waitBias;
    }

    /**
     * Returns number of milliseconds to wait before request can be made.
     * @param api API instance.
     * @param appInfo app info.
     * @return number of milliseconds to wait (0 when no wait is needed).
     */
    @Override
    public long throttle(VkApi api, VkAppInfo appInfo)
    {
        Info info = getInfo(api, appInfo);
        return info.getTimeToWait(_limit) + _waitBias;
    }

    /**
     * Invokes {@link Thread#sleep(long)} with a result of {@link #throttle}.
     * @param api API instance.
     * @param appInfo app info.
     */
    @Override
    public void throttleWait(VkApi api, VkAppInfo appInfo) throws InterruptedException
    {
        Thread.sleep(throttle(api, appInfo));
    }

    private synchronized Info getInfo(VkApi api, VkAppInfo appInfo)
    {
        Info info = _info.get(getKey(api, appInfo));
        if (info == null) {
            info = new Info();
        }
        return info;
    }

    private String getKey(VkApi api, VkAppInfo appInfo)
    {
        return api.getLoginResult().getId() + appInfo.getApiId();
    }

    private static class Info
    {
        synchronized long getTimeToWait(int limit)
        {
            long cur = System.currentTimeMillis();
            long elapsed = cur - time;
            if (elapsed < UNIT) {
                if (count < limit) {
                    count++;
                    return 0;
                } else {
                    return UNIT - elapsed;
                }
            } else {
                count = 0;
                time = cur;
                return 0;
            }
        }
        long time = System.currentTimeMillis();
        int count;
    }

    private volatile int _limit;
    private volatile long _waitBias = DEFAULT_WAIT_BIAS;
    private final Map<String, Info> _info = new HashMap<String, Info>();
}
