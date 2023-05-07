package com.example.chillisauce.spaces.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Optional;

@Data
@Getter
@NoArgsConstructor
public class SpaceRequestDto {
    private String spaceName;

    private Optional<Long> floorId = Optional.empty();

    public SpaceRequestDto(String spaceName) {
        this.spaceName = spaceName;
    }
}
