package DTO.TracksDTOS;

import java.util.List;

public class TracksObjectDTO {

    private List<TrackItemsDTO> items;

    public void moveDataToTrackItem() {
        if (!items.isEmpty()) {
            for (TrackItemsDTO item : items) {
                item.moveDataFromTrack();
            }
        }
    }

    public List<TrackItemsDTO> getItems() {
        return items;
    }
}
