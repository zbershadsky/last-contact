/**
 *
 */
package net.emphased.vkclient;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class VkLoginResult
{
    VkLoginResult() { }

    @JsonProperty("error")
    public String getErrorMessage()
    {
        return _errorMessage;
    }

    public String getId()
    {
        return _id;
    }

    public String getEmail()
    {
        return _email;
    }

    @JsonProperty("pass")
    public String getPasswordHash()
    {
        return _passwordHash;
    }

    @JsonProperty("sid")
    public String getSessionId()
    {
        return _sessionId;
    }

    @JsonProperty("error")
    void setErrorMessage(String errorMessage)
    {
        _errorMessage = errorMessage;
    }

    void setId(String id)
    {
        _id = id;
    }

    void setEmail(String email)
    {
        _email = email;
    }

    @JsonProperty("pass")
    void setPasswordHash(String passwordHash)
    {
        _passwordHash = passwordHash;
    }

    @JsonProperty("sid")
    void setSessionId(String sessionId)
    {
        _sessionId = sessionId;
    }

    private String _errorMessage;
    private String _id;
    private String _email;
    private String _passwordHash;
    private String _sessionId;
}
