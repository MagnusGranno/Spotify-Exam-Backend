package DTO.MyPlaylistsDTOS;

import java.util.List;

public class MyPlaylistDTO {

    private String description;
    private String name;
    private String id;
    private List<MyPlaylistImage> images;
    private String imageUrl;
    private int userFollowers;

    public void setUserFollowers(int userFollowers) {
        this.userFollowers = userFollowers;
    }

    public int getUserFollowers() {
        return userFollowers;
    }

    public void moveImageUrl() {
        if (!images.isEmpty()) {
            this.imageUrl = images.get(0).getUrl();
            this.images = null;
        }
    }

}
