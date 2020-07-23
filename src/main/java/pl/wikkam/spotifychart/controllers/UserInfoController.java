package pl.wikkam.spotifychart.controllers;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import pl.wikkam.spotifychart.services.SpotifyService;
import javax.naming.AuthenticationException;
import java.security.Principal;

@RestController
public class UserInfoController {
    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/user")
    public Principal user(Principal principal) throws AuthenticationException {
        if(principal == null){
            throw new AuthenticationException("No auth token provided! go to /login first");
        }
        return principal;
    }

    @GetMapping("/")
    public Principal home(Principal principal) throws AuthenticationException {
        if(principal == null){
            throw new AuthenticationException("No auth token provided! go to /login first");
        }
        return principal;
    }

    @GetMapping("/login")
    public Principal login(Principal principal){
        return principal;
    }

    @GetMapping("/user/playlists")
    public JsonNode getUserPlaylists(OAuth2Authentication details) throws AuthenticationException {
        return this.spotifyService.getUserPlaylists(details);
    }

    @GetMapping("/user/playlists/{name}/tracks")
    public JsonNode getTracksFromPlaylist(OAuth2Authentication details, @PathVariable String name) throws AuthenticationException {
        return this.spotifyService.getTracksFromPlaylist(details, name).get("items");
    }

    @GetMapping("/user/recently-played")
    public JsonNode getRecentlyPlayedTracks(OAuth2Authentication details) throws AuthenticationException {
        return this.spotifyService.getRecentlyPlayedTracks(details);
    }

    @GetMapping("/user/top-artists")
    public JsonNode getTopArtists(OAuth2Authentication details, @RequestParam(name = "time_range") String timeRange) throws AuthenticationException {
        return this.spotifyService.getUsersTopArtists(details, timeRange);
    }

    @GetMapping("/user/top-tracks")
    public JsonNode getTopTracks(OAuth2Authentication details, @RequestParam(name = "time_range") String timeRange) throws AuthenticationException {
        return this.spotifyService.getUsersTopTracks(details, timeRange);
    }
}
