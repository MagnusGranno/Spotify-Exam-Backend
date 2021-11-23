package rest;

import DTO.ItemsDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import facades.FacadeExample;
import facades.SpotifyFacade;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
}
