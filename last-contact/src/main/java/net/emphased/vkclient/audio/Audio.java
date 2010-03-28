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
package net.emphased.vkclient.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.emphased.vkclient.ContentFilter;
import net.emphased.vkclient.VkApi;
import net.emphased.vkclient.VkApiResponse;
import net.emphased.vkclient.VkAppInfo;
import net.emphased.vkclient.VkException;
import net.emphased.vkclient.VkSubApi;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Audio sub-API.
 */
public class Audio extends VkSubApi
{
    public static String NAME = "audio";

    public enum Sort {
        BY_DATE,
        BY_DURATION,
    }

    public Audio(VkApi api)
    {
        super(api, NAME);
    }

    public SearchResult search(VkAppInfo appInfo, String query, Sort sort,
            boolean withLyricsOnly, int count, int offset)
                throws IOException, VkException
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("q", query));
        params.add(new BasicNameValuePair("sort", String.valueOf(sort.ordinal())));
        params.add(new BasicNameValuePair("lyrics", withLyricsOnly ? "1" : "0"));
        params.add(new BasicNameValuePair("count", String.valueOf(count)));
        params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        SearchContentFilter contentFilter = new SearchContentFilter();
        SearchResult result = _api.makeApiRequest(appInfo, getMethodName("search"),
                                        params, SearchResult.class, contentFilter);
        result.setTotalCount(contentFilter.totalCount);

        return result;
    }

    /**
     * Transforms JSON response into deserializable form.
     * {"response":["totalCount", ...]} -> {"response":[...]}
     *
     * Stores <code>totalCount</code> in a field.
     */
    private static class SearchContentFilter implements ContentFilter
    {
        static final Pattern REGEX = Pattern.compile(
                "\\s*(\\{\\s*\"response\"\\s*:\\s*\\[)\\s*\"(\\d+)\"\\s*,\\s*(.+)",
                Pattern.DOTALL);

        @Override
        public String filter(String str)
        {
            Matcher m = REGEX.matcher(str);
            if (!m.matches()) {
                return str;
            }

            try {
                totalCount = Integer.parseInt(m.group(2));
            } catch (NumberFormatException e) {
                return str;
            }

            return m.group(1) + m.group(3);
        }

        int totalCount;
    }
}
