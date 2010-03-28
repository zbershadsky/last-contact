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

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * API error description.
 */
public class VkApiError
{
    public static int UNKNOWN = 1;
    public static int APP_DISABLED = 2;
    public static int UNKOWN_METHOD = 3;
    public static int BAD_SIGNATURE = 4;
    public static int USER_AUTH_FAILED = 5;
    public static int REQUESTS_TOO_FAST = 6;

    VkApiError() { }

    @JsonProperty("error_code")
    public int getErrorCode()
    {
        return _errorCode;
    }

    @JsonProperty("error_code")
    void setErrorCode(int errorCode)
    {
        _errorCode = errorCode;
    }

    @JsonProperty("error_msg")
    public String getErrorMessage()
    {
        return _errorMessage;
    }

    @JsonProperty("error_msg")
    void setErrorMessage(String errorMessage)
    {
        _errorMessage = errorMessage;
    }

    @JsonProperty("request_params")
    public VkApiRequestParam[] getRequestParams()
    {
        return _requestParams;
    }

    @JsonProperty("request_params")
    void setRequestParams(VkApiRequestParam[] requestParams)
    {
        _requestParams = requestParams;
    }

    private int _errorCode;
    private String _errorMessage;
    private VkApiRequestParam[] _requestParams;
}
