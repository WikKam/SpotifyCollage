package pl.wikkam.spotifychart.services;

import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.Iterator;

@Service
public class SpotifyService {

    private HttpEntity<Object> createHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(headers);
    }

    public void checkIfAuthenticated(OAuth2Authentication details) throws AuthenticationException {
        if(details==null)
            throw new AuthenticationException("No auth token provided! go to /login first");
    }

    public JsonNode getUserPlaylists(OAuth2Authentication details) throws AuthenticationException {
        checkIfAuthenticated(details);
        final String url = "https://api.spotify.com/v1/me/playlists";
        return getResponse(details, url).get("items");
    }
    private JsonNode getResponse(OAuth2Authentication details, String url) {
        RestTemplate restTemplate = new RestTemplate();
        String token = ((OAuth2AuthenticationDetails)details.getDetails()).getTokenValue();
        HttpEntity<Object> entity = createHeaders(token);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

    public JsonNode getRawUserPlaylist(OAuth2Authentication details, String name) throws AuthenticationException {
        checkIfAuthenticated(details);
        Iterator<JsonNode> playlistIterator = getUserPlaylists(details).elements();

        while (playlistIterator.hasNext()){
            JsonNode current = playlistIterator.next();
            if(current.get("name").textValue().equals(name)) {
                return current;
            }
        }
        return null;
    }

    public JsonNode getRawTracksFromPlaylist(OAuth2Authentication details, String name) throws AuthenticationException {
        checkIfAuthenticated(details);
        JsonNode playlist = getRawUserPlaylist(details, name);
        if(playlist != null){
            String url = playlist.get("tracks").get("href").textValue();
            ObjectNode result = (ObjectNode) getResponse(details,url);
            while(result.get("next").textValue() != null){
                String nextUrl = result.get("next").textValue();
                JsonNode nextResult = getResponse(details, nextUrl);
                ArrayNode items = (ArrayNode)nextResult.get("items");
                ((ArrayNode)result.get("items")).addAll(items);
                result.set("next", nextResult.get("next"));
            }
            return result;
        }
        return null;
    }

    public JsonNode getRecentlyPlayedRawTracks(OAuth2Authentication details) throws AuthenticationException {
        checkIfAuthenticated(details);
        final String url = "https://api.spotify.com/v1/me/player/recently-played?limit=50";
        return getResponse(details, url);
    }
    public  JsonNode getUsersRawTopArtists(OAuth2Authentication details, String timeRange) throws AuthenticationException {
        checkIfAuthenticated(details);
        final String url = "https://api.spotify.com/v1/me/top/artists?time_range=" + timeRange +"&limit=50";
        return getResponse(details,url);
    }

    public JsonNode getUsersRawTopTracks(OAuth2Authentication details, String timeRange) throws AuthenticationException {
        checkIfAuthenticated(details);
        final String url = "https://api.spotify.com/v1/me/top/tracks?time_range=" + timeRange +"&limit=50";
        return getResponse(details, url);
    }
}
