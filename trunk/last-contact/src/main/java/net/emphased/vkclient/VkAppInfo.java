/**
 *
 */
package net.emphased.vkclient;

/**
 * Holds information about Vkontakte App.
 */
public class VkAppInfo
{
    public VkAppInfo(String apiId, String secret)
    {
        _apiId = apiId;
        _secret = secret;
    }

    public String getApiId()
    {
        return _apiId;
    }

    public String getSecret()
    {
        return _secret;
    }

    private final String _apiId;
    private final String _secret;
}
