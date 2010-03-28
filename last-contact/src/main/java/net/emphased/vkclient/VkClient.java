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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 */
public class VkClient
{
    public static String DEFAULT_SITE = "vkontakte.ru";
    public static String DEFAULT_LOGIN_URL_PATTERN = "http://%s/login.php";
    public static String DEFAULT_LOGIN_URL = String.format(DEFAULT_LOGIN_URL_PATTERN, DEFAULT_SITE);
    public static final String SESSION_COOKIE = "remixsid";
    public static final String SESSION_INFO_COOKIE = "lastcontactSessionInfo";
    private final CookieStoreProvider _cookieStoreFactory;
    private final HttpContextFactory _httpContextFactory;

    public VkClient(HttpClient httpClient, CookieStore cookieStore)
    {
        this(httpClient, new SingleCookieStoreProvider(cookieStore));
    }

    public VkClient(HttpClient httpClient, CookieStoreProvider cookieStoreProvider)
    {
        this(httpClient, cookieStoreProvider, new BasicHttpContextFactory());
    }

    public VkClient(HttpClient httpClient, CookieStoreProvider cookieStoreProvider,
               HttpContextFactory httpContextFactory)
    {
        _httpClient = httpClient;
        _cookieStoreFactory = cookieStoreProvider;
        _httpContextFactory = httpContextFactory;
    }

    public static ObjectMapper getObjectMapper()
    {
        return _objectMapper;
    }

    public static void setObjectMapper(ObjectMapper objectMapper)
    {
        _objectMapper = objectMapper;
    }

    public HttpClient getHttpClient()
    {
        return _httpClient;
    }

    public VkApiThrottler getThrottler()
    {
        return _throttler;
    }

    public void setThrottler(VkApiThrottler throttler)
    {
        _throttler = throttler;
    }

    public boolean isLoggedIn(String email)
    {
        CookieStore cookieStore = getCookieStore(email);
        return cookieStore != null &&
            CookieUtils.findCookie(cookieStore, SESSION_COOKIE) != null;
    }

    /**
     * Login to Vk site.
     * @param email email for login.
     * @param password password for login.
     * @return API instance.
     * @throws VkException if login failed.
     */
    public VkApi login(String email, String password)
                    throws IOException, VkException
    {
        CookieStore cookieStore = getCookieStore(email);
        HttpContext httpContext = getHttpContext(cookieStore);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("noredirect", "1"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("pass", password));

        HttpPost post = new HttpPost(DEFAULT_LOGIN_URL);
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        post.addHeader("X-Requested-With", "XMLHttpRequest");
        HttpResponse resp = _httpClient.execute(post, httpContext);
        String respContent = EntityUtils.toString(resp.getEntity());
        VkLoginResult loginResult = _objectMapper.readValue(respContent, VkLoginResult.class);

        if (!StringUtils.isEmpty(loginResult.getErrorMessage())) {
            throw new VkException(loginResult.getErrorMessage());
        }

        Cookie sessionCookie = CookieUtils.findCookie(cookieStore, SESSION_COOKIE);
        if (sessionCookie == null) {
            throw new VkException("No session cookie found");
        }
        // Store LoginResult for future cookie-based logins.
        BasicClientCookie sessionInfoCookie =
            new BasicClientCookie(SESSION_INFO_COOKIE, respContent);
        sessionInfoCookie.setExpiryDate(sessionCookie.getExpiryDate());
        cookieStore.addCookie(sessionInfoCookie);

        return createApi(httpContext, loginResult);
    }

    /**
     * Try to login using cookies.
     * @param email email to use for login.
     * @return API instance if successful, <code>null</code> if cookies missing or expired.
     */
    public VkApi login(String email) throws IOException, VkException
    {
        CookieStore cookieStore = getCookieStore(email);
        HttpContext httpContext = getHttpContext(cookieStore);

        Cookie sessionInfoCookie = CookieUtils.findCookie(cookieStore, SESSION_INFO_COOKIE);
        Cookie sessionCookie = CookieUtils.findCookie(cookieStore, SESSION_COOKIE);
        if (sessionInfoCookie == null || sessionCookie == null) {
            return null;
        }

        VkLoginResult loginResult = _objectMapper.readValue(
                sessionInfoCookie.getValue(), VkLoginResult.class);

        if (!sessionCookie.getValue().equals(loginResult.getSessionId())) {
            return null;
        }

        return createApi(httpContext, loginResult);
    }

    protected VkApi createApi(HttpContext httpContext, VkLoginResult loginResult)
    {
        if (_throttler != null) {
            return new VkThrottledApi(this, httpContext, loginResult, _throttler);
        } else {
            return new VkApi(this, httpContext, loginResult);
        }
    }

    protected CookieStore getCookieStore(String email)
    {
        CookieStore result = _cookieStoreFactory.getCookieStore(email);
        clearExpiredCookies(result);
        return result;
    }

    protected HttpContext getHttpContext(CookieStore cookieStore)
    {
        HttpContext result = _httpContextFactory.create();
        result.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        return result;
    }

    protected void clearExpiredCookies(CookieStore cookieStore)
    {
        cookieStore.clearExpired(new Date());
    }

    private static ObjectMapper _objectMapper = new ObjectMapper();
    private final HttpClient _httpClient;
    private VkApiThrottler _throttler;
}
