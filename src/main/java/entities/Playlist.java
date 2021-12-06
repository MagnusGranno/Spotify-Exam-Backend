package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "playlist")
@NamedQuery(name = "Playlist.deleteAllRows", query = "delete from Playlist")
@NamedNativeQuery(name = "Playlist.resetAutoIncrement", query = "ALTER TABLE playlist AUTO_INCREMENT = 1;")
public class Playlist implements Serializable {

    private static final long serialVersionUID = 1L;
//    @Id
//    @Basic(optional = false)
//    @NotNull
//    @Column(name = "playlist_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "spotify_id")
    private String spotifyId;

    @ManyToMany(mappedBy = "playlistList")
    private List<User> userList;

    private int followers;


    public Playlist() {
    }

    public Playlist(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public int getFollowers() {
        return followers;
    }

    public void addFollower() {
        this.followers++;
    }

    public void removeFollower() {
        if (followers == 0) {
            return;
        }
        this.followers--;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}