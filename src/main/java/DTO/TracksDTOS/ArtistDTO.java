package DTO.TracksDTOS;

public class ArtistDTO {

    private ExternalUrlDTO external_urls;
    private String name;
    private String url;

    public void moveExternalUrls() {
        this.url = external_urls.getSpotify();
        external_urls = null;
    }
}