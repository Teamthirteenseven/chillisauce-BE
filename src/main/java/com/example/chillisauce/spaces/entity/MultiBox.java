package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.MultiBoxRequestDto;
import com.example.chillisauce.users.entity.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
@Entity
@Getter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Setter
public class MultiBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotEmpty
    private String multiBoxName;
    @Column(nullable = false)
    @NotEmpty
    private String x;
    @Column(nullable = false)
    @NotEmpty
    private String y;

    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    public MultiBox(MultiBoxRequestDto multiBoxRequestDto) {
        this.multiBoxName = multiBoxRequestDto.getMultiBoxName();
        this.x = multiBoxRequestDto.getX();
        this.y = multiBoxRequestDto.getY();
    }

    public void updateMultiBox(MultiBoxRequestDto multiBoxRequestDto) {
        this.multiBoxName = multiBoxRequestDto.getMultiBoxName();
        this.x = multiBoxRequestDto.getX();
        this.y = multiBoxRequestDto.getY();

    }

    public void linkSpace(Space space) {
        this.space = space;
        space.getMultiboxes().add(this);
    }

    public void updateMultiBox(MultiBoxRequestDto multiBoxRequestDto, User user) {
        this.multiBoxName = multiBoxRequestDto.getMultiBoxName();
        this.x = multiBoxRequestDto.getX();
        this.y = multiBoxRequestDto.getY();
        this.user = user;
        this.username = user.getUsername();
    }
}
