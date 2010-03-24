/**
 *
 */
package net.emphased.vkclient;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

/**
 * Helper functions for working with {@link Cookie} and {@link CookieStore}.
 */
public class CookieUtils
{
    /**
     * Search for cookie in <code>cookieStore</code> with <code>name</code>.
     * @param cookieStore <code>CookieStore</code> to search in.
     * @param name cookie name to search for.
     * @return cookie if found, <code>null</code> otherwise.
     */
    public static Cookie findCookie(CookieStore cookieStore, String name)
    {
        for (Cookie cookie: cookieStore.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }
}
