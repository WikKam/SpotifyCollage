package pl.wikkam.spotifychart.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private BufferedImage image = null;
    private int sideSize = 0; // 2, 3, 4 ,5
    private int nodeSize = 0; // 64, 300, 640

    private BufferedImage resize(BufferedImage inputImage, int scaledWidth, int scaledHeight)
            throws IOException {
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

    public List parseArtistData(JsonNode data){
        List artistsList = new ObjectMapper().convertValue(data, ArrayList.class);
        return (List) artistsList.stream().map(obj -> {
            JsonNode track = new ObjectMapper().valueToTree(obj);
            return this.getImagesFromArtist(track);
        }).collect(Collectors.toList());
    }

    private Graphics2D initDrawing(){
        Graphics2D g2 = this.image.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, nodeSize * sideSize, nodeSize * sideSize);
        g2.setColor(oldColor);
        return g2;
    }

    private BufferedImage drawAll(Graphics2D g2, List list) throws IOException {
        for(int i = 0; i < sideSize * sideSize; i++) {
            ArrayNode trackImages = new ObjectMapper().valueToTree(list.get(i));
            String url = trackImages.get(mapNodeSizeToIndex()).get("url").textValue();
            BufferedImage img1 = this.readImage(url);
            if(img1.getWidth() != this.nodeSize){
                img1 = this.resize(img1, this.nodeSize, this.nodeSize);
            }
            g2.drawImage(img1, null, ((i%sideSize)*nodeSize),(i/sideSize)*nodeSize);

        }
        g2.dispose();
        return this.image;
    }

    public BufferedImage buildArtistsChart(JsonNode data) throws IOException {
        List artistsList = this.parseArtistData(data);

        Graphics2D g2 = this.initDrawing();

        return this.drawAll(g2, artistsList);
    }


    public JsonNode getImagesFromTrack(JsonNode track){
        return track.get("track").get("album").get("images");
    }

    public JsonNode getImagesFromArtist(JsonNode artist){
        return artist.get("images");
    }

    public BufferedImage readImage(String url) throws IOException {
        URL imageUrl = new URL(url);
        return ImageIO.read(imageUrl);
    }

    public BufferedImage joinTwo(String url1, String url2) throws IOException {
        BufferedImage img1 = readImage(url1);
        BufferedImage img2 = readImage(url2);
        return joinTwo(img1,img2);
    }

    public BufferedImage joinTwo(BufferedImage img1, BufferedImage img2){
        int offset  = 0;
        int wid = img1.getWidth()+img2.getWidth()+offset;
        int height = Math.max(img1.getHeight(),img2.getHeight())+offset;
        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth()+offset, 0);
        g2.dispose();
        return newImage;
    }
}
