/**
 *
 */
package net.emphased.vkclient;

/**
 * Base class for API responses.
 */
public class VkApiResponse
{
    public VkApiError getError()
    {
        return _error;
    }

    void setError(VkApiError error)
    {
        _error = error;
    }

    public boolean isSuccess()
    {
        return _error == null;
    }

    private VkApiError _error;
}
