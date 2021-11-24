package rest;

import DTO.CategoryDTOS.ItemsDTO;
import DTO.PlaylistsDTOS.PlaylistDTO;
import DTO.TracksDTOS.TrackItemsDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import facades.SpotifyFacade;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("spotify")
public class SpotifyResource {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @GET
    @Produces
    @Path("token")
    public String getTokenFromSpotify() throws IOException {

        SpotifyFacade sf = SpotifyFacade.getSpotifyFacade();

        String result = sf.getTokenFromSpotify();

        return result;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("browse")
    public String getCategoriesFromSpotify() throws IOException {

        SpotifyFacade sf = SpotifyFacade.getSpotifyFacade();
        List<ItemsDTO> categories = sf.getCategories();

        return gson.toJson(categories);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("browse/{genre}")
    public String getPlaylistsFromSpotify(@PathParam("genre") String genre) throws IOException {

        SpotifyFacade sf = SpotifyFacade.getSpotifyFacade();
        List<PlaylistDTO> playlists = sf.getPlaylists(genre);

        return gson.toJson(playlists);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("playlist/{playlistId}")
    public String getTracksFromSpotify(@PathParam("playlistId") String playlistId) throws IOException {

        SpotifyFacade sf = SpotifyFacade.getSpotifyFacade();
        List<TrackItemsDTO> tracks = sf.getTracks(playlistId);

        return gson.toJson(tracks);
    }
}
