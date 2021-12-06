package DTO.TracksDTOS;

import java.util.List;

public class TrackItemsDTO {

    private TrackDTO track;

    private long duration_ms;
    private String id;
    private String name;
    private String preview_url;
    private List<ArtistDTO> artists;

    public void moveDataFromTrack() {
        this.duration_ms = track.getDuration_ms();
        this.id = track.getId();
        this.name = track.getName();
        this.preview_url = track.getPreview_url();
        this.artists = track.getArtists();
        this.track = null;

        if (!this.artists.isEmpty()) {
            for (ArtistDTO artist : this.artists) {
                artist.moveExternalUrls();
            }
        }
    }
}
