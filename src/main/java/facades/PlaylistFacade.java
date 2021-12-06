package facades;

import DTO.CountDTOS.CountDTO;
import DTO.MyPlaylistsDTOS.MyPlaylistDTO;
import DTO.UserDTOS.UserDTO;
import callables.Parallel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Playlist;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlaylistFacade {

    private static PlaylistFacade instance;
    private static EntityManagerFactory emf;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //Private Constructor to ensure Singleton
    private PlaylistFacade() {
    }

    /**
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

    public void savePlaylistOnUser(String spotifyId, String userName) {
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Playlist playlist = null;

            if (em.find(Playlist.class, spotifyId) != null) {
                playlist = em.find(Playlist.class, spotifyId);
            } else {
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

    public List<MyPlaylistDTO> getMostPopularPlaylists() throws IOException, ExecutionException, InterruptedException {
        SpotifyFacade sf = SpotifyFacade.getSpotifyFacade();
        sf.getTokenIfNeeded();
        String accessToken = sf.getAccessToken();
        String browseUrl = "https://api.spotify.com/v1/playlists/";
        EntityManager em = getEntityManager();
        List<Playlist> playlists;
        List<MyPlaylistDTO> myPlaylistDTOList = new ArrayList<>();

        try {
            TypedQuery<Playlist> tq = em.createQuery(
                    "select p from Playlist p order by p.followers desc", Playlist.class);
            tq.setMaxResults(10);
            playlists = tq.getResultList();
        } finally {
            em.close();
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<String>> futures = new ArrayList<>();

        for (Playlist playlist : playlists) {
            Future future = executor.submit(
                    new Parallel(browseUrl + playlist.getSpotifyId(), accessToken));
            futures.add(future);
        }


        for (int i = 0; i < futures.size(); i++) {
            String response = futures.get(i).get();

            MyPlaylistDTO myPlaylistDTO = gson.fromJson(response, MyPlaylistDTO.class);
            myPlaylistDTO.moveImageUrl();
            myPlaylistDTO.setUserFollowers(playlists.get(i).getFollowers());
            myPlaylistDTOList.add(myPlaylistDTO);
        }
        return myPlaylistDTOList;
    }

    public CountDTO getCountOfUsers() {
        EntityManager em = getEntityManager();
        CountDTO countDTO;
        try {
            TypedQuery<Long> q = em.createQuery("select count(u) from User u", Long.class);
            countDTO = new CountDTO(q.getSingleResult());
        } finally {
            em.close();
        }
        return countDTO;
    }

    public List<UserDTO> getAllUsersFromDatabase() {
        EntityManager em = getEntityManager();
        List<UserDTO> userDTOS = new ArrayList<>();
        try {
            TypedQuery<User> tq = em.createQuery("select u from User u", User.class);
            List<User> users = tq.getResultList();
            for (User user : users) {
                Query q = em.createQuery("select count(user.playlistList) from User user where user.userName = :userName");
                q.setParameter("userName", user.getUserName());
                userDTOS.add(new UserDTO(user, (Long) q.getSingleResult()));
            }
        } finally {
            em.close();
        }
        return userDTOS;
    }

    public List<MyPlaylistDTO> getFollowedPlaylists(String username) throws IOException {
        SpotifyFacade sf = SpotifyFacade.getSpotifyFacade();
        sf.getTokenIfNeeded();
        String accessToken = sf.getAccessToken();

        String browseUrl = "https://api.spotify.com/v1/playlists/";
        EntityManager em = getEntityManager();
        User user = em.find(User.class, username);

        List<MyPlaylistDTO> myPlaylistDTOList = new ArrayList<>();

        for (Playlist pl : user.getPlaylistList()) {
            URL url = new URL(browseUrl + pl.getSpotifyId());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("content-type", "application/json");
            http.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String currentLine = Lines.readLine();
            String response = "";
            while (currentLine != null) {
                response += currentLine;
                currentLine = Lines.readLine();
            }
            MyPlaylistDTO myPlaylistDTO = gson.fromJson(response, MyPlaylistDTO.class);
            myPlaylistDTO.moveImageUrl();
            myPlaylistDTOList.add(myPlaylistDTO);
        }
        return myPlaylistDTOList;
    }

}
