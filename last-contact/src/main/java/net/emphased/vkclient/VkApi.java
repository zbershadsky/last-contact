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
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.emphased.vkclient.audio.Audio;
import net.emphased.vkclient.deprecated.Deprecated;

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

    public VkLoginResult getLoginResult()
    {
        return _loginResult;
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
        try {
            return DigestUtils.md5Hex(str.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public Deprecated deprecated()
    {
        return _deprecated;
    }

    public Audio audio()
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
    private final Deprecated _deprecated = new Deprecated(this);
    private final Audio _audio = new Audio(this);
}
