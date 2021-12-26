package pl.wikkam.spotifychart.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import pl.wikkam.spotifychart.model.Album;
import pl.wikkam.spotifychart.services.ImageService;
import pl.wikkam.spotifychart.services.PlaylistService;
import pl.wikkam.spotifychart.services.SpotifyService;

import javax.imageio.ImageIO;
import javax.naming.AuthenticationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class ImagesController {

    private final SpotifyService spotifyService;

    private final ImageService imageService;

    private final PlaylistService playlistService;

    @Autowired
    public ImagesController(
            SpotifyService spotifyService,
            ImageService imageService,
            PlaylistService playlistService
    ){
        this.spotifyService = spotifyService;
        this.imageService = imageService;
        this.playlistService = playlistService;
    }

    private ResponseEntity<byte[]> sendImage(BufferedImage bufferedImage) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        this.imageService.initBuilding(3,300);
        ImageIO.write(bufferedImage, "png", bao);
        byte[] media = bao.toByteArray();
        return new ResponseEntity<>(media,headers, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/user/playlists/{name}/images", method = RequestMethod.GET, produces = "image/jpg")
    public ResponseEntity<byte[]> getPlaylistImages(OAuth2Authentication details,
                                                    @PathVariable String name,
                                                    @RequestParam(name = "size") String size) throws IOException, AuthenticationException {

        JsonNode trackData = this.spotifyService.getRawTracksFromPlaylist(details, name).get("items");
        List<Album> albums = this.playlistService.parseRawTracksToAlbumList(trackData);
        this.imageService.initBuilding(Integer.parseInt(size), 300);
        return sendImage(this.imageService.buildAlbumsChart(albums));
    }

    @CrossOrigin
    @RequestMapping(value = "/user/recently-played/images", method = RequestMethod.GET, produces = "image/jpg")
    public ResponseEntity<byte[]> getRecentlyPlayedTracksImages(OAuth2Authentication details,
                                                                @RequestParam(name="size") String size) throws IOException, AuthenticationException {
        JsonNode rawTracksData = this.spotifyService.getRecentlyPlayedRawTracks(details).get("items");
        List<Album> albums = this.playlistService.parseRawTracksToAlbumList(rawTracksData);
        int sum = albums.stream().map(album -> album.getTracksList().size()).reduce(0, Integer::sum);
        System.out.println(sum);
        this.imageService.initBuilding(Integer.parseInt(size), 300);
        return sendImage(this.imageService.buildAlbumsChart(albums));
    }

    @CrossOrigin
    @RequestMapping(value = "/user/top-artists/images", method = RequestMethod.GET, produces = "image/jpg")
    public ResponseEntity<byte[]> getTopArtistsImages(OAuth2Authentication details,
                                                      @RequestParam(name = "time_range") String timeRange,
                                                      @RequestParam(name="size") String size) throws IOException, AuthenticationException {
        JsonNode artists = this.spotifyService.getUsersRawTopArtists(details, timeRange).get("items");
        this.imageService.initBuilding(Integer.parseInt(size),300);
        return sendImage(this.imageService.buildArtistsChart(artists));
    }

    @CrossOrigin
    @RequestMapping(value = "/user/top-tracks/images", method = RequestMethod.GET, produces = "image/jpg")
    public ResponseEntity<byte[]> getTopTracksImages(OAuth2Authentication details,
                                                     @RequestParam(name = "time_range") String timeRange,
                                                     @RequestParam(name = "size") String size) throws IOException, AuthenticationException {
        JsonNode trackData = this.spotifyService.getUsersRawTopTracks(details, timeRange).get("items");
        List<Album> albums = this.playlistService.parseRawTracksToAlbumList(trackData);
        albums.forEach(album -> {
            System.out.println(album.getAlbumName());
            System.out.println(album.getAlbumLength());
        });
        this.imageService.initBuilding(Integer.parseInt(size), 300);
        return sendImage(this.imageService.buildAlbumsChart(albums));
    }

}
