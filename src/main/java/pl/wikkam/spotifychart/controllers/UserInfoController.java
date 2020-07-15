package pl.wikkam.spotifychart.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import pl.wikkam.spotifychart.services.ImageService;
import pl.wikkam.spotifychart.services.SpotifyService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserInfoController {
    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private ImageService imageService;


    @GetMapping("/user")
    public Principal user(Principal principal){
        return principal;
    }

    @GetMapping("/login")
    public Principal login(Principal principal){
        return principal;
    }

    @GetMapping("/user/playlists")
    public JsonNode getUserPlaylists(OAuth2Authentication details){
        return this.spotifyService.getUserPlaylists(details);
    }

    @GetMapping("/user/playlists/{name}")
    public JsonNode getUserPlaylist(OAuth2Authentication details, @PathVariable String name){
        return this.spotifyService.getUserPlaylist(details,name);
    }

    @GetMapping("/user/playlists/{name}/tracks")
    public JsonNode getTracksFromPlaylist(OAuth2Authentication details, @PathVariable String name){
        return this.spotifyService.getTracksFromPlaylist(details, name);
    }

    @GetMapping("/user/recently-played")
    public JsonNode getRecentlyPlayedTracks(OAuth2Authentication details){
        return this.spotifyService.getRecentlyPlayedTracks(details);
    }

    @RequestMapping(value = "/user/recently-played/images", method = RequestMethod.GET, produces = "image/jpg")
    public ResponseEntity<byte[]> getRecentlyPlayedTracksImages(OAuth2Authentication details) throws IOException {
        JsonNode tracks = this.spotifyService.getRecentlyPlayedTracks(details).get("items");
        List tracksList = new ObjectMapper().convertValue(tracks, ArrayList.class);
        tracksList = (List) tracksList.stream().map(obj -> {
            JsonNode track = new ObjectMapper().valueToTree(obj);
            return this.imageService.getImagesFromTrack(track);
        }).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ArrayNode track1images = new ObjectMapper().valueToTree(tracksList.get(0));
        ArrayNode track2images = new ObjectMapper().valueToTree(tracksList.get(1));

        String url1 = track1images.get(1).get("url").textValue();
        String url2 = track2images.get(1).get("url").textValue();
        BufferedImage resultImg = this.imageService.joinTwo(url1,url2);
        System.out.println(resultImg);
        ImageIO.write(resultImg, "png", bao);
        byte[] media = bao.toByteArray();
        ResponseEntity<byte[]> ret = new ResponseEntity<>(media,headers, HttpStatus.OK);
        return ret;
    }

    @GetMapping("/user/top-artists")
    public JsonNode getTopArtists(OAuth2Authentication details){
        return this.spotifyService.getUsersTopArtists(details);
    }

    @RequestMapping(value = "/user/top-artists/images", method = RequestMethod.GET, produces = "image/jpg")
    public ResponseEntity<byte[]> getTopArtistsImages(OAuth2Authentication details) throws IOException {
        JsonNode artists = this.spotifyService.getUsersTopArtists(details).get("items");

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        this.imageService.initBuilding(3,300);
        BufferedImage resultImg = this.imageService.buildArtistsChart(artists);
        System.out.println(resultImg);
        ImageIO.write(resultImg, "png", bao);
        byte[] media = bao.toByteArray();
        ResponseEntity<byte[]> ret = new ResponseEntity<>(media,headers, HttpStatus.OK);
        return ret;
    }


}
