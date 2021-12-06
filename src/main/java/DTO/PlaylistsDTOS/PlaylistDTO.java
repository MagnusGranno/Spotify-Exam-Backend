package DTO.PlaylistsDTOS;

import java.util.List;

public class PlaylistDTO {

    private String description;
    private String name;
    private String id;
    private List<PlaylistImage> images;
    private String imageUrl;


    public void moveImageUrl() {
        if (!images.isEmpty()) {
            this.imageUrl = images.get(0).getUrl();
        }
    }

    public void setImages(List<PlaylistImage> images) {
        this.images = images;
    }
}
