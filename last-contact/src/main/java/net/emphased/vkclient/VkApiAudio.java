/**
 *
 */
package net.emphased.vkclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 */
public class VkApiAudio extends VkSubApi
{
    public static String NAME = "audio";

    public enum Sort {
        BY_DATE,
        BY_DURATION,
    }

    public static class SearchResult extends VkApiResponse
    {
        public static class FileInfo
        {
            @JsonCreator
            FileInfo(@JsonProperty("aid") String aid,
                     @JsonProperty("owner_id") String ownerId,
                     @JsonProperty("artist") String artist,
                     @JsonProperty("title") String title,
                     @JsonProperty("duration") int duration,
                     @JsonProperty("url") String url)
            {
                _aid = aid;
                _ownerId = ownerId;
                _artist = artist;
                _title = title;
                _duration = duration;
                _url = url;
            }

            public String getAid()
            {
                return _aid;
            }

            @JsonProperty("owner_id")
            public String getOwnerId()
            {
                return _ownerId;
            }

            public String getArtist()
            {
                return _artist;
            }

            public String getTitle()
            {
                return _title;
            }

            public int getDuration()
            {
                return _duration;
            }

            public String getUrl()
            {
                return _url;
            }

            private final String _aid;
            private final String _ownerId;
            private final String _artist;
            private final String _title;
            private final int _duration;
            private final String _url;
        }

        SearchResult() { }

        public FileInfo[] getResponse()
        {
            return _response;
        }

        void setResponse(FileInfo[] response)
        {
            _response = response;
        }

        public int getTotalCount()
        {
            return _totalCount;
        }

        void setTotalCount(int totalCount)
        {
            _totalCount = totalCount;
        }

        private FileInfo[] _response;
        private int _totalCount;
    }

    public VkApiAudio(VkApi api)
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
        static final Pattern REGEX = Pattern.compile("\\s*(\\{\\s*\"response\"\\s*:\\s*\\[)\\s*\"(\\d+)\"\\s*,\\s*(.+)", Pattern.DOTALL);

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
