package pl.wikkam.spotifychart.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.wikkam.spotifychart.model.Album;
import pl.wikkam.spotifychart.model.Artist;
import pl.wikkam.spotifychart.model.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private BufferedImage image = null;
    private int sideSize = 0; // 2, 3, 4 ,5
    private int nodeSize = 0; // 64, 300, 640
    private final ObjectMapper mapper = new ObjectMapper();

    private BufferedImage resize(BufferedImage inputImage, int scaledWidth, int scaledHeight) {
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return outputImage;
    }


    private int mapNodeSizeToIndex(){
        switch (nodeSize){
            case 64:
                return 0;
            case 300:
                return 1;
            case 640:
                return 2;
            default:
                return -1;
        }
    }

    public void initBuilding(int sideSize, int nodeSize){
        this.sideSize = sideSize;
        this.nodeSize = nodeSize;
        this.image =
                new BufferedImage
                        (sideSize * nodeSize, sideSize * nodeSize, BufferedImage.TYPE_INT_ARGB);
    }

    public List<Artist> parseArtistData(JsonNode data){
        return mapper.convertValue(
                data,
                mapper.getTypeFactory().constructCollectionType(List.class, Artist.class)
        );
    }

    private Graphics2D initDrawing(){
        Graphics2D g2 = this.image.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, nodeSize * sideSize, nodeSize * sideSize);
        g2.setColor(oldColor);
        return g2;
    }

    private BufferedImage drawAll(Graphics2D g2, List<List<Image>> list) {
        for(int i = 0; i < Math.min(sideSize * sideSize, list.size()); i++) {
            String url = list.get(i).get(mapNodeSizeToIndex()).getUrl();
            try {
                BufferedImage img1 = this.readImage(url);
                if(img1.getWidth() != this.nodeSize){
                    img1 = this.resize(img1, this.nodeSize, this.nodeSize);
                }
                g2.drawImage(img1, null, ((i % sideSize) * nodeSize),(i / sideSize) * nodeSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        g2.dispose();
        return this.image;
    }

    public BufferedImage buildArtistsChart(JsonNode data) {
        List<Artist> artistsList = this.parseArtistData(data);

        Graphics2D g2 = this.initDrawing();

        return this.drawAll(g2, artistsList
                .stream()
                .map(Artist::getImages)
                .collect(Collectors.toList()));
    }

    public BufferedImage buildAlbumsChart(List<Album> albums) {
        List<List<Image>> imagesList = albums
                .stream()
                .map(Album::getImages)
                .collect(Collectors.toList());

        Graphics2D g2 = this.initDrawing();
        return this.drawAll(g2, imagesList);
    }

    public BufferedImage readImage(String url) throws IOException {
        URL imageUrl = new URL(url);
        return ImageIO.read(imageUrl);
    }
}
