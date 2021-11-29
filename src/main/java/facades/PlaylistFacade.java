package facades;

import com.google.gson.Gson;
import entities.Playlist;
import entities.User;

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

    public void savePlaylistOnUser(String spotifyId, String userName){
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Playlist playlist = null;

            if(em.find(Playlist.class,spotifyId) != null){
                playlist = em.find(Playlist.class, spotifyId);
            } else{
                playlist = new Playlist(spotifyId);
                em.persist(playlist);
            }

            user.addPlaylist(playlist);
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void unSavePlaylistFromUser(String spotifyId, String username) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, username);
            Playlist playlist = em.find(Playlist.class, spotifyId);
            user.removePlaylist(playlist);
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }




}
