package pl.wikkam.spotifychart.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class Album implements Comparable {
    @Getter
    @Setter
    private String albumName;
    @Getter
    @Setter
    private String artistName;
    @Getter
    @Setter
    private JsonNode images;

    @Getter
    private List<Track> tracksList = new ArrayList<>();

    public BigInteger getAlbumLength(){
        return this.tracksList.stream().map(Track::getLength).reduce(BigInteger.valueOf(0), BigInteger::add);
    }

    public void addTrack(Track track){
        tracksList.add(track);
    }


    @Override
    public int compareTo(Object o) {
        if(o instanceof Album){
            Album album = (Album) o;
            return this.getAlbumLength().subtract(album.getAlbumLength()).intValue();
        }
        return 0;
    }
}
