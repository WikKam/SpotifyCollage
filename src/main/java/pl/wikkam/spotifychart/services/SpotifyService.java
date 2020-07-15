package pl.wikkam.spotifychart.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
public class SpotifyService {

    private HttpEntity createHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new HttpEntity(headers);
    }

    public JsonNode getAlbumsByAuthor(OAuth2Authentication details, String name){

        final String template = "https://api.spotify.com/v1/search?q=%s&type=%s";

        final String url = String.format(template, name, "album");

        return getResponse(details, url);
    }

    public JsonNode getUserPlaylists(OAuth2Authentication details){
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

    public JsonNode getUserPlaylist(OAuth2Authentication details, String name){
        return new ObjectMapper()
                .valueToTree(
                       new ObjectMapper()
                               .convertValue(getUserPlaylists(details), ArrayList.class)
                               .stream()
                .filter(playlist -> {
            LinkedHashMap object = (LinkedHashMap)playlist;
            return object.get("name").equals(name);
        }).findAny().orElse(null));
    }

    public JsonNode getTracksFromPlaylist(OAuth2Authentication details, String name){
        JsonNode playlist = getUserPlaylist(details, name);
        if(playlist != null){
            String url = playlist.get("tracks").get("href").textValue();
            return getResponse(details,url);
        }
        return null;
    }

    public JsonNode getRecentlyPlayedTracks(OAuth2Authentication details){
        final String url = "https://api.spotify.com/v1/me/player/recently-played";
        return getResponse(details, url);
    }
    public  JsonNode getUsersTopArtists(OAuth2Authentication details){
        final String url = "https://api.spotify.com/v1/me/top/artists?time_range=long_term&limit=9";
        return getResponse(details,url);
    }
}
