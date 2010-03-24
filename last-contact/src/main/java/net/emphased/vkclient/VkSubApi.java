/**
 *
 */
package net.emphased.vkclient;

/**
 * Base class for sub-APIs.
 */
public class VkSubApi
{
    protected VkSubApi(VkApi api, String name)
    {
        _api = api;
        _name = name;
    }

    public String getName()
    {
        return _name;
    }

    protected String getMethodName(String methodShortName)
    {
        return _name + '.' + methodShortName;
    }

    protected final VkApi _api;
    private final String _name;
}
