package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.spaces.dto.LocationDto;
import com.example.chillisauce.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;


    public UserLocation(Location location, User user) {
        this.location = location;
        this.userId = user.getId();
        this.username = user.getUsername();

    }
}
