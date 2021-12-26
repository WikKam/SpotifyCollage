package pl.wikkam.spotifychart.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;
import pl.wikkam.spotifychart.model.Album;
import pl.wikkam.spotifychart.model.Image;
import pl.wikkam.spotifychart.model.Track;

import java.math.BigInteger;
import java.util.*;

@Service
public class PlaylistService {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Album> parseRawTracksToAlbumList(JsonNode rawTrackList){
        HashMap<String, Album> albumsMap = new HashMap<>();
        Iterator<JsonNode> trackIterator = rawTrackList.elements();
        while(trackIterator.hasNext()){
            JsonNode trackData = trackIterator.next();
            trackData = trackData.get("track") ==null ? trackData : trackData.get("track");
            Track track = new Track();
            track.setLength(BigInteger.valueOf(trackData.get("duration_ms").asLong()));
            track.setName(trackData.get("name").textValue());
            String albumName = trackData.get("album").get("name").textValue();
            String artist = trackData.get("album").get("artists").get(0).get("name").textValue();
            Album album;
            if(!albumsMap.containsKey(albumName)){
                album = new Album();
                album.setAlbumName(albumName);
                album.setArtistName(artist);
                album.setImages(
                        mapper.convertValue(
                                trackData.get("album").get("images"),
                                mapper.getTypeFactory().constructCollectionType(List.class, Image.class)
                        )
                );
                albumsMap.put(albumName, album);
            }
            else{
                album = albumsMap.get(albumName);
            }
            album.addTrack(track);
        }
        ArrayList<Album> ret = new ArrayList<>(albumsMap.values());
        ret.sort(Collections.reverseOrder());
        return ret;
    }
}
