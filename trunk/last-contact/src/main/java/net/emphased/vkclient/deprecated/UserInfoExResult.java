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
package net.emphased.vkclient.deprecated;

import net.emphased.vkclient.VkApiResponse;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link Deprecated#getUserInfoEx} result.
 */
public class UserInfoExResult extends VkApiResponse
{
    public static class Response
    {
        @JsonCreator
        Response(@JsonProperty("user_id") String userId,
                 @JsonProperty("user_name") String userName,
                 @JsonProperty("user_sex") String userSex,
                 @JsonProperty("user_city") String userCity,
                 @JsonProperty("user_photo") String userPhoto)
        {
            _userId = userId;
            _userName = userName;
            _userSex = userSex;
            _userCity = userCity;
            _userPhoto = userPhoto;
        }

        public String getUserId()
        {
            return _userId;
        }

        public String getUserName()
        {
            return _userName;
        }

        public String getUserSex()
        {
            return _userSex;
        }

        public String getUserCity()
        {
            return _userCity;
        }

        public String getUserPhoto()
        {
            return _userPhoto;
        }

        private final String _userId;
        private final String _userName;
        private final String _userSex;
        private final String _userCity;
        private final String _userPhoto;
    }

    public Response getResponse()
    {
        return _response;
    }

    void setResponse(Response response)
    {
        _response = response;
    }

    private Response _response;
}
