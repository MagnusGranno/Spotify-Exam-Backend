package DTO.UserDTOS;

import entities.User;

public class UserDTO {

    private String userName;

    public UserDTO(User user){
        this.userName = user.getUserName();
    }
}
