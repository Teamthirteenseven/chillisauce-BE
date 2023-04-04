//package com.example.chillisauce.spaces.dto;
//
//import com.example.chillisauce.spaces.entity.Box;
//import com.example.chillisauce.spaces.entity.Mr;
//import com.example.chillisauce.spaces.entity.Space;
//import com.example.chillisauce.users.entity.Companies;
//import org.junit.jupiter.api.Test;
//import static org.assertj.core.api.Assertions.assertThat;
//import java.util.Arrays;
//
//public class SpaceResponseDtoTest {
//
//    @Test
//    public void spaceResponseDto_convertsSpaceToDto() {
//        // Given
//        Companies companies = new Companies();
//        Space space = new Space(new SpaceRequestDto("Space Name"), companies);
//        Box box1 = new Box(new BoxRequestDto("Box 1", "100", "100"), space);
//        Box box2 = new Box(new BoxRequestDto("Box 2", "200", "200"), space);
//        Mr mr1 = new Mr(new MrRequestDto("Mr 1", "300", "300"), space);
//        Mr mr2 = new Mr(new MrRequestDto("Mr 2", "400", "400"), space);
//
//        space.setBoxs(Arrays.asList(box1, box2));
//        space.setMrs(Arrays.asList(mr1, mr2));
//
//        // When
//        SpaceResponseDto spaceResponseDto = new SpaceResponseDto(space);
//
//        // Then
//        assertThat(spaceResponseDto.getId()).isEqualTo(space.getId());
//        assertThat(spaceResponseDto.getSpaceName()).isEqualTo(space.getSpaceName());
//        assertThat(spaceResponseDto.getBoxlist()).hasSize(2);
//        assertThat(spaceResponseDto.getBoxlist()).extracting(BoxResponseDto::getBoxName).containsExactly("Box 1", "Box 2");
//        assertThat(spaceResponseDto.getMrlist()).hasSize(2);
//        assertThat(spaceResponseDto.getMrlist()).extracting(MrResponseDto::getMrName).containsExactly("Mr 1", "Mr 2");
//    }
//
//    @Test
//    public void spaceResponseDto_handlesNullFields() {
//        // Given
//        Space space = new Space(new SpaceRequestDto("Space Name"), null);
//
//        // When
//        SpaceResponseDto spaceResponseDto = new SpaceResponseDto(space);
//
//        // Then
//        assertThat(spaceResponseDto.getId()).isEqualTo(space.getId());
//        assertThat(spaceResponseDto.getSpaceName()).isEqualTo(space.getSpaceName());
//        assertThat(spaceResponseDto.getBoxlist()).isEmpty();
//        assertThat(spaceResponseDto.getMrlist()).isEmpty();
//    }
//
//}