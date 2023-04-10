package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.reservations.entity.Reservation;
import com.example.chillisauce.spaces.dto.MrRequestDto;
import com.example.chillisauce.users.entity.User;
import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Entity
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Mr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty
    private String mrName;
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

    @OneToMany(mappedBy = "meetingRoom")
    List<Reservation> reservations;
    public Mr(MrRequestDto mrRequestDto) {
        this.mrName = mrRequestDto.getMrName();
        this.x = mrRequestDto.getX();
        this.y = mrRequestDto.getY();
        this.username = getUsername();
    }

    public void updateMr(MrRequestDto mrRequestDto) {
        this.mrName = mrRequestDto.getMrName();
        this.x = mrRequestDto.getX();
        this.y = mrRequestDto.getY();
    }
    public void linkSpace(Space space) {
        this.space = space;
        space.getMrs().add(this);
    }
}
