package pl.wikkam.spotifychart.model;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class Track {
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private BigInteger length;

}
