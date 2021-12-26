package pl.wikkam.spotifychart.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Album implements Comparable<Album> {
    @Getter
    @Setter
    private String albumName;
    @Getter
    @Setter
    private String artistName;
    @Getter
    @Setter
    private List<Image> images;

    @Getter
    private List<Track> tracksList = new ArrayList<>();

    public BigInteger getAlbumLength(){
        return this.tracksList.stream().map(Track::getLength).reduce(BigInteger.valueOf(0), BigInteger::add);
    }

    public void addTrack(Track track){
        tracksList.add(track);
    }


    @Override
    public int compareTo(Album o) {
        if(o != null){
            return this.getAlbumLength().subtract(o.getAlbumLength()).intValue();
        }
        return 0;
    }
}
