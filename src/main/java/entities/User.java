package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name = "users")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "user_name")
  @NotNull
  @Basic(optional = false)
  private String userName;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "user_pass")
  private String userPass;

  @JoinTable(name = "user_roles", joinColumns = {
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")}, inverseJoinColumns = {
    @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
  @ManyToMany
  private List<Role> roleList = new ArrayList<>();

  @JoinTable(name = "playlist_users", joinColumns = {
          @JoinColumn(name = "user_id", referencedColumnName = "user_id")}, inverseJoinColumns = {
          @JoinColumn(name = "playlist_id", referencedColumnName = "playlist_id")})
  @ManyToMany
  private List<Playlist> playlistList = new ArrayList<>();


  public List<String> getRolesAsStrings() {
    if (roleList.isEmpty()) {
      return null;
    }
    List<String> rolesAsStrings = new ArrayList<>();
    roleList.forEach((role) -> {
        rolesAsStrings.add(role.getRoleName());
      });
    return rolesAsStrings;
  }

  public User() {}

  //TODO Change when password is hashed
   public boolean verifyPassword(String pw, String hashedPw){
        return BCrypt.checkpw(pw, hashedPw);
    }

  public User(String userName, String userPass) {
    this.userName = userName;
    String salt =BCrypt.gensalt();
    this.userPass = BCrypt.hashpw(userPass, salt);
  }


  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserPass() {
    return this.userPass;
  }

  public void setUserPass(String userPass) {
    this.userPass = userPass;
  }
  public List<Playlist> getPlaylistList() {
    return playlistList;
  }
  public void setPlaylistList(List<Playlist> playlistList) {
    this.playlistList = playlistList;
  }
  public void addPlaylist(Playlist myPlaylist) {
    playlistList.add(myPlaylist);
  }

  public long getId() {
    return id;
  }

  public List<Role> getRoleList() {
    return roleList;
  }

  public void setRoleList(List<Role> roleList) {
    this.roleList = roleList;
  }

  public void addRole(Role userRole) {
    roleList.add(userRole);
  }

}
