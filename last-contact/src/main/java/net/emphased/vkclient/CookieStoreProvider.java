/**
 *
 */
package net.emphased.vkclient;

import org.apache.http.client.CookieStore;

/**
 * {@link CookieStore} factory.
 */
public interface CookieStoreProvider
{
    CookieStore getCookieStore(String userEmail);
}
