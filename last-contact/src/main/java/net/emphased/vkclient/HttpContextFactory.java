/**
 *
 */
package net.emphased.vkclient;

import org.apache.http.protocol.HttpContext;

/**
 * {@link HttpContext} factory.
 */
public interface HttpContextFactory
{
    HttpContext create();
}
