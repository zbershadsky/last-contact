/**
 *
 */
package net.emphased.vkclient;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * {@link HttpContextFactory} implementation creating {@link BasicHttpContext} instances.
 */
public class BasicHttpContextFactory implements HttpContextFactory
{
    @Override
    public HttpContext create()
    {
        return new BasicHttpContext();
    }
}
