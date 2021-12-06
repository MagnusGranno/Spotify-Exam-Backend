package DTO.CountDTOS;

public class CountDTO {

    private final Long userCount;

    public CountDTO(Long count) {
        this.userCount = count;
    }

    public Long getUserCount() {
        return userCount;
    }
}
