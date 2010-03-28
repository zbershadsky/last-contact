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

import net.emphased.vkclient.VkApiResponse;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link Audio#search} result.
 */
public class SearchResult extends VkApiResponse
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
