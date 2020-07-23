package pl.wikkam.spotifychart.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;
import pl.wikkam.spotifychart.model.Album;
import pl.wikkam.spotifychart.model.Track;

import java.math.BigInteger;
import java.util.*;

@Service
public class PlaylistService {

    public List<Album> parseTracksToAlbumList(JsonNode trackList){
        HashMap<String, Album> albumsMap = new HashMap<>();
        Iterator<JsonNode> trackIterator = trackList.elements();
        while(trackIterator.hasNext()){
            JsonNode trackData = trackIterator.next();
            trackData = trackData.get("track") ==null ? trackData : trackData.get("track");
            Track track = new Track();
            track.setLength(BigInteger.valueOf(trackData.get("duration_ms").asLong()));
            track.setName(trackData.get("name").textValue());
            String albumName = trackData.get("album").get("name").textValue();
            String artist = trackData.get("album").get("artists").get(0).get("name").textValue();
            System.out.println(artist);
            if(!albumsMap.containsKey(albumName)){
                Album album = new Album();
                album.setAlbumName(albumName);
                album.setArtistName(artist);
                album.setImages(trackData.get("album").get("images"));
                albumsMap.put(albumName, album);
                album.addTrack(track);
            }
            else{
                Album album = albumsMap.get(albumName);
                album.addTrack(track);
            }
        }
        ArrayList<Album> ret = new ArrayList<>(albumsMap.values());
        ret.sort(Collections.reverseOrder());
        return ret;
    }
}
