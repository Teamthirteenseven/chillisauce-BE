package com.example.chillisauce.spaces.entity;

import com.example.chillisauce.users.entity.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "locationType")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Column(nullable = false)
    private String locationName;
    @NotEmpty
    @Column(nullable = false)
    private String x;
    @NotEmpty
    @Column(nullable = false)
    private String y;

    private String username;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    public Location(Location location, String username) {
        this.locationName = location.getLocationName();
        this.x = location.getX();
        this.y = location.getY();
        this.username = username;
    }


    public void linkSpace(Space space) {
        this.space = space;
        space.getLocations().add(this);
    }

    public Location(String locationName, String x, String y) {
        this.locationName = locationName;
        this.x = x;
        this.y = y;
    }


}
