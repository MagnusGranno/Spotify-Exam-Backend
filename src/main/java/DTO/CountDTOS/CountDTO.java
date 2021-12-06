package DTO.CountDTOS;

public class CountDTO {

    private Long userCount;

    public CountDTO(Long count){
        this.userCount = count;
    }

    public Long getUserCount() {
        return userCount;
    }
}
