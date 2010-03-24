/**
 *
 */
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

    public boolean isLoggedIn(String email)
    {
        CookieStore cookieStore = getCookieStore(email);
        return cookieStore != null &&
            CookieUtils.findCookie(cookieStore, SESSION_COOKIE) != null;
    }

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

        return new VkApi(this, httpContext, loginResult);
    }

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

        return new VkApi(this, httpContext, loginResult);
    }

    private CookieStore getCookieStore(String email)
    {
        CookieStore result = _cookieStoreFactory.getCookieStore(email);
        clearExpiredCookies(result);
        return result;
    }

    private HttpContext getHttpContext(CookieStore cookieStore)
    {
        HttpContext result = _httpContextFactory.create();
        result.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        return result;
    }

    private void clearExpiredCookies(CookieStore cookieStore)
    {
        cookieStore.clearExpired(new Date());
    }

    private static ObjectMapper _objectMapper = new ObjectMapper();
    private final HttpClient _httpClient;
}
