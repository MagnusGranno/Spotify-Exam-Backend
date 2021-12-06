package DTO.UserDTOS;

import entities.User;

public class UserDTO {

    private final String userName;
    private final Long playlistCount;

    public UserDTO(User user, Long playlistCount) {
        this.userName = user.getUserName();
        this.playlistCount = playlistCount;
    }

    public String getUserName() {
        return userName;
    }
}
