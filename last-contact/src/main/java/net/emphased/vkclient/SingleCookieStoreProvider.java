/**
 *
 */
package net.emphased.vkclient;

import org.apache.http.client.CookieStore;

/**
 * Single {@link CookieStore} wrapper provider.
 */
public class SingleCookieStoreProvider implements CookieStoreProvider
{
    public SingleCookieStoreProvider(CookieStore cookieStore)
    {
        _cookieStore = cookieStore;
    }

    @Override
    public CookieStore getCookieStore(String userEmail)
    {
        return _cookieStore;
    }

    private final CookieStore _cookieStore;
}
