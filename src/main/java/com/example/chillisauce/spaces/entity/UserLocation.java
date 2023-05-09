package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.users.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Getter
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Version
    private Long version;

    public UserLocation(Location location, User user) {
        this.location = location;
        this.userId = user.getId();
        this.username = user.getUsername();

    }

    public UserLocation update(Location location, User user) {
        this.userId = user.getId();
        this.username = getUsername();
        this.location = location;
        return this;
    }
}
