package facades;

import DTO.MyPlaylistsDTOS.MyPlaylistDTO;
import entities.Playlist;
import entities.Role;
import entities.User;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlaylistFacadeTest {

    private static EntityManagerFactory emf;
    private static PlaylistFacade facade;

    public PlaylistFacadeTest() {}

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PlaylistFacade.getPlaylistFacade(emf);
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {


            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.resetAutoIncrement").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.resetAutoIncrement").executeUpdate();
            em.createNamedQuery("Playlist.deleteAllRows").executeUpdate();
            em.createNamedQuery("Playlist.resetAutoIncrement").executeUpdate();
            em.getTransaction().commit();



            User user1 = new User("user1", "kode123");
            User user2 = new User("user2", "kode123");
            User admin = new User("admin", "kode123");
            User both = new User("user_admin", "kode123");
            Playlist playlistOne = new Playlist("37i9dQZF1DX9uKNf5jGX6m");
            Playlist playlistTwo = new Playlist("37i9dQZF1DXdejmG21jbny");

            em.getTransaction().begin();
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user1.addRole(userRole);
            user1.addPlaylist(playlistOne);
            user1.addPlaylist(playlistTwo);
            em.persist(playlistTwo);
            em.persist(playlistOne);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user1);
            em.getTransaction().commit();

            em.getTransaction().begin();
            user2.addRole(userRole);
            em.persist(user2);
            em.getTransaction().commit();

            em.getTransaction().begin();
            admin.addRole(adminRole);
            both.addRole(userRole);
            both.addRole(adminRole);
            em.persist(admin);
            em.persist(both);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @Test
    public void testSavePlaylistOnUserNewPlaylist() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            facade.savePlaylistOnUser("37i9dQZF1DWZqd5JICZI0u", "user2");
            User user = em.find(User.class, "user2");

            Assertions.assertEquals("37i9dQZF1DWZqd5JICZI0u", user.getPlaylistList().get(0).getSpotifyId());

        } finally {
            em.close();
        }
    }

    @Test
    public void testUnSavePlaylist() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            facade.savePlaylistOnUser("37i9dQZF1DWZqd5JICZI0u", "user2");
            facade.unSavePlaylistFromUser("37i9dQZF1DWZqd5JICZI0u", "user2");
            Playlist playlist = em.find(Playlist.class, "37i9dQZF1DWZqd5JICZI0u");

            Assertions.assertEquals(0, playlist.getFollowers());

        } finally {
            em.close();
        }
    }

    @Test
    public void testSavePlaylistOnUserExistingPlaylist() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            facade.savePlaylistOnUser("37i9dQZF1DX9uKNf5jGX6m", "user2");
            User user = em.find(User.class, "user2");

            Assertions.assertEquals("37i9dQZF1DX9uKNf5jGX6m", user.getPlaylistList().get(0).getSpotifyId());

        } finally {
            em.close();
        }
    }

    @Test
    public void testGetMostPopularPlaylists() {
        EntityManager em = emf.createEntityManager();
        List<MyPlaylistDTO> myPlaylistDTO = new ArrayList<>();
        try {
            myPlaylistDTO = facade.getMostPopularPlaylists();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        Assertions.assertEquals(1,myPlaylistDTO.get(0).getUserFollowers());
        Assertions.assertEquals(1,myPlaylistDTO.get(1).getUserFollowers());

    }
}
