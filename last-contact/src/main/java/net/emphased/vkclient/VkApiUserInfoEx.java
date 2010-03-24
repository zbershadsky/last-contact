/**
 *
 */
package net.emphased.vkclient;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * <a href="http://vkontakte.ru/pages.php?o=-1&p=getUserInfoEx">getUserInfoEx</a> API result.
 */
public class VkApiUserInfoEx extends VkApiResponse
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
