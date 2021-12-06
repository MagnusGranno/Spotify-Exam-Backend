package DTO.PlaylistsDTOS;

import java.util.List;

public class PlaylistsDTO {

    private List<PlaylistDTO> items;

    public List<PlaylistDTO> getPlaylistDTO() {
        return items;
    }

    public void moveImageUrlForEachItem() {
        if (!items.isEmpty()) {
            for (PlaylistDTO p : items) {
                p.moveImageUrl();
                p.setImages(null);
            }
        }
    }
}
