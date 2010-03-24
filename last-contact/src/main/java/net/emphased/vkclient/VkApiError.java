/**
 *
 */
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
