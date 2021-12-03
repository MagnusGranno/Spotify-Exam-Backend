package rest;

import DTO.CategoryDTOS.ItemsDTO;
import DTO.MyPlaylistsDTOS.MyPlaylistDTO;
import DTO.PlaylistsDTOS.PlaylistDTO;
import DTO.TracksDTOS.TrackItemsDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import errorhandling.API_Exception;
import facades.PlaylistFacade;
import facades.SpotifyFacade;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("spotify")
public class SpotifyResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
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


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("follow")
    @RolesAllowed({"user","admin"})
    public void followPlaylist(String jsonString) throws API_Exception, AuthenticationException {


        PlaylistFacade pf = PlaylistFacade.getPlaylistFacade(EMF);

        String username;
        String spotifyId;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
            spotifyId = json.get("spotifyId").getAsString();

        } catch(Exception e) {
            throw new API_Exception("Malformed JSON Suplied 1",400,e);
        }

        try {
            pf.savePlaylistOnUser(spotifyId, username);
        } catch (Exception e) {
            throw new API_Exception("Malformed Json Suplied 2", 400, e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("unfollow")
    @RolesAllowed({"user","admin"})
    public void unFollowPlaylist(String jsonString) throws API_Exception, AuthenticationException {


        PlaylistFacade pf = PlaylistFacade.getPlaylistFacade(EMF);

        String username;
        String spotifyId;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
            spotifyId = json.get("spotifyId").getAsString();

        } catch(Exception e) {
            throw new API_Exception("Malformed JSON Suplied 1",400,e);
        }

        try {
            pf.unSavePlaylistFromUser(spotifyId, username);
        } catch (Exception e) {
            throw new API_Exception("Malformed Json Suplied 2", 400, e);
        }
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("myplaylists/{username}")
    @RolesAllowed({"user","admin"})
    public String getFollowedPlaylists(@PathParam("username") String username) throws IOException {

        SpotifyFacade sf = SpotifyFacade.getSpotifyFacade();
        List<MyPlaylistDTO> myPlaylistDTOS = sf.getFollowedPlaylists(username);

        return gson.toJson(myPlaylistDTOS);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("popular")
    public String getMostPopularPlaylists() throws API_Exception {
        PlaylistFacade pf = PlaylistFacade.getPlaylistFacade(EMF);
        List<MyPlaylistDTO> response;

        try{
            response = pf.getMostPopularPlaylists();
        } catch (Exception e) {
            throw new API_Exception("Failed to fetch most popular playlists", 400, e);
        }
        return gson.toJson(response);
    }

}
