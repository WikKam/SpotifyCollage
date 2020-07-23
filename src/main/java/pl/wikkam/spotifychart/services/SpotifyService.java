package pl.wikkam.spotifychart.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
public class SpotifyService {

    private ObjectMapper mapper = new ObjectMapper();

    private HttpEntity createHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new HttpEntity(headers);
    }

    public void checkIfAuthenticated(OAuth2Authentication details) throws AuthenticationException {
        if(details==null)
            throw new AuthenticationException("No auth token provided! go to /login first");
    }

    public JsonNode getAlbumsByAuthor(OAuth2Authentication details, String name) throws AuthenticationException {
        checkIfAuthenticated(details);

        final String template = "https://api.spotify.com/v1/search?q=%s&type=%s";

        final String url = String.format(template, name, "album");

        return getResponse(details, url);
    }

    public JsonNode getUserPlaylists(OAuth2Authentication details) throws AuthenticationException {
        checkIfAuthenticated(details);
        final String url = "https://api.spotify.com/v1/me/playlists";
        return getResponse(details, url).get("items");
    }
    private JsonNode getResponse(OAuth2Authentication details, String url) {
        RestTemplate restTemplate = new RestTemplate();
        String token = ((OAuth2AuthenticationDetails)details.getDetails()).getTokenValue();
        HttpEntity entity = createHeaders(token);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

    public JsonNode getUserPlaylist(OAuth2Authentication details, String name) throws AuthenticationException {
        checkIfAuthenticated(details);
        return mapper
                .valueToTree(
                       mapper
                               .convertValue(getUserPlaylists(details), ArrayList.class)
                               .stream()
                .filter(playlist -> {
            LinkedHashMap object = (LinkedHashMap)playlist;
            return object.get("name").equals(name);
        }).findAny().orElse(null));
    }

    public JsonNode getTracksFromPlaylist(OAuth2Authentication details, String name) throws AuthenticationException {
        checkIfAuthenticated(details);
        JsonNode playlist = getUserPlaylist(details, name);
        if(playlist != null){
            String url = playlist.get("tracks").get("href").textValue();
            JsonNode result =  getResponse(details,url);
            while(result.get("next").textValue() != null){
                String nextUrl = result.get("next").textValue();
                System.out.println(nextUrl);
                JsonNode nextResult = getResponse(details, nextUrl);
                ArrayNode items = (ArrayNode) nextResult.get("items");
                ((ArrayNode)result.get("items")).addAll(items);
                ((ObjectNode) result).set("next", nextResult.get("next"));
            }
            return result;
        }
        return null;
    }

    public JsonNode getRecentlyPlayedTracks(OAuth2Authentication details) throws AuthenticationException {
        checkIfAuthenticated(details);
        final String url = "https://api.spotify.com/v1/me/player/recently-played?limit=50";
        return getResponse(details, url);
    }
    public  JsonNode getUsersTopArtists(OAuth2Authentication details, String timeRange) throws AuthenticationException {
        checkIfAuthenticated(details);
        final String url = "https://api.spotify.com/v1/me/top/artists?time_range=" + timeRange +"&limit=50";
        return getResponse(details,url);
    }

    public JsonNode getUsersTopTracks(OAuth2Authentication details, String timeRange) throws AuthenticationException {
        checkIfAuthenticated(details);
        final String url = "https://api.spotify.com/v1/me/top/tracks?time_range=" + timeRange +"&limit=50";
        return getResponse(details, url);
    }
}
