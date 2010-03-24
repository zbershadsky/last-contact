/**
 *
 */
package net.emphased.vkclient;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Request parameters returned in {@link VkApiError}.
 */
public class VkApiRequestParam
{
    @JsonCreator
    VkApiRequestParam(@JsonProperty("key") String key,
                      @JsonProperty("value") String value)
    {
        _key = key;
        _value = value;
    }

    public String getKey()
    {
        return _key;
    }

    public String getValue()
    {
        return _value;
    }

    private final String _key;
    private final String _value;
}
