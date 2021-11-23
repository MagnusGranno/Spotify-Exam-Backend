package facades;

import com.google.gson.Gson;
import entities.Playlist;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class PlaylistFacade {

    private static PlaylistFacade instance;
    private static EntityManagerFactory emf;
    private Gson gson = new Gson();

    //Private Constructor to ensure Singleton
    private PlaylistFacade() {}

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PlaylistFacade getPlaylistFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PlaylistFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }


}
