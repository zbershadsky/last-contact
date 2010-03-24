/**
 *
 */
package net.emphased.vkclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * Vkontakte API.
 */
public class VkApi
{
    public static String VERSION = "2";
    public static final String DEFAULT_URL_PATTERN = "http://api.%s/api.php";
    public static final String DEFAULT_URL = String.format(DEFAULT_URL_PATTERN, VkClient.DEFAULT_SITE);
    public static final String DEFAULT_URL_PREFIX = DEFAULT_URL + '?';
    public static final String API_ID_PARAM = "api_id";
    public static final String SIGNATURE_PARAM = "sig";
    public static final String METHOD_PARAM = "method";
    public static final String FORMAT_PARAM = "format";
    public static final String VERSION_PARAM = "v";

    VkApi(VkClient client, HttpContext httpContext,
            VkLoginResult loginResult)
    {
        _client = client;
        _httpContext = httpContext;
        _loginResult = loginResult;
    }

    public void logout() throws IOException, VkException
    {
        getCookieStore().clear();
    }

    public<T> T makeApiRequest(
            VkAppInfo appInfo,
            String method,
            List<NameValuePair> params,
            Class<T> responseClass,
            ContentFilter contentFilter)
                throws IOException, VkException
    {
        params.add(new BasicNameValuePair(METHOD_PARAM, method));
        params.add(new BasicNameValuePair(API_ID_PARAM, appInfo.getApiId()));
        params.add(new BasicNameValuePair(FORMAT_PARAM, "JSON"));
        params.add(new BasicNameValuePair(VERSION_PARAM, VERSION));
        String signature = makeSignature(_loginResult.getId(), appInfo.getSecret(), params);
        params.add(new BasicNameValuePair(SIGNATURE_PARAM, signature));

        String url = DEFAULT_URL_PREFIX + URLEncodedUtils.format(params, "UTF-8");

        HttpGet get = new HttpGet(url);
        HttpResponse resp = _client.getHttpClient().execute(get, _httpContext);
        String respContent = EntityUtils.toString(resp.getEntity());

        if (contentFilter != null) {
            respContent = contentFilter.filter(respContent);
        }

        T result = VkClient.getObjectMapper().readValue(respContent, responseClass);

        return result;
    }

    public<T> T makeApiRequest(
            VkAppInfo appInfo,
            String method,
            List<NameValuePair> params,
            Class<T> responseClass)
                throws IOException, VkException
    {
        return makeApiRequest(appInfo, method, params, responseClass, null);
    }

    public static String makeSignature(String viewerId,
            String appSecret, List<NameValuePair> params)
    {
        SortedMap<String, String> map = new TreeMap<String, String>();
        for (NameValuePair pair: params) {
            map.put(pair.getName(), pair.getValue());
        }
        return makeSignature(viewerId, appSecret, map);
    }

    public static String makeSignature(String viewerId,
            String appSecret, SortedMap<String, String> params)
    {
        StringBuilder str = new StringBuilder();
        str.append(viewerId);
        for (Entry<String, String> param: params.entrySet()) {
            str.append(param.getKey()).append('=').append(param.getValue());
        }
        str.append(appSecret);
        return DigestUtils.md5Hex(str.toString());
    }

    public VkApiUserInfoEx getUserInfoEx(VkAppInfo appInfo)
                    throws IOException, VkException
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        return makeApiRequest(appInfo, "getUserInfoEx", params, VkApiUserInfoEx.class);
    }

    /**
     * Audio related API methods.
     * @return audio API instance.
     */
    public VkApiAudio audio()
    {
        return _audio;
    }

    private CookieStore getCookieStore()
    {
        return (CookieStore)_httpContext.getAttribute(ClientContext.COOKIE_STORE);
    }

    private final VkClient _client;
    private final HttpContext _httpContext;
    private final VkLoginResult _loginResult;
    private final VkApiAudio _audio = new VkApiAudio(this);
}
