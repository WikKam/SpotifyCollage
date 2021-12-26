package pl.wikkam.spotifychart.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private int width;
    @Getter
    @Setter
    private int height;
}
