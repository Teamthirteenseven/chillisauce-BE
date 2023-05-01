//package com.example.chillisauce.spaces.repository;
//
//import com.example.chillisauce.spaces.dto.BoxResponseDto;
//import com.example.chillisauce.spaces.dto.MultiBoxResponseDto;
//import com.example.chillisauce.spaces.entity.Location;
//import com.example.chillisauce.spaces.entity.MultiBox;
//import com.example.chillisauce.spaces.entity.QUserLocation;
//import com.example.chillisauce.spaces.entity.UserLocation;
//import com.querydsl.jpa.impl.JPAQuery;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
//
//import javax.persistence.EntityManager;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import static com.example.chillisauce.spaces.entity.QBox.box;
//import static com.example.chillisauce.spaces.entity.QMultiBox.multiBox;
//
//
//public class LocationRepositoryImpl extends QuerydslRepositorySupport implements LocationRepositorySupport{
//    private final EntityManager em;
//
//    public LocationRepositoryImpl(JPAQueryFactory queryFactory, EntityManager em) {
//        super(Location.class);
//        this.em = em;
//    }
//
//
//    public List<BoxResponseDto> getBoxResponseDtos(Long spaceId) {
//        QUserLocation userLocation = QUserLocation.userLocation;
//        return new JPAQuery<>(em)
//                .select(box, userLocation.username)
//                .from(box)
//                .leftJoin(box.userLocations, userLocation)
//                .where(box.space.id.eq(spaceId))
//                .fetch()
//                .stream()
//                .map(tuple -> new BoxResponseDto(tuple.get(box), tuple.get(userLocation)))
//                .collect(Collectors.toList());
//    }
//
//    public List<MultiBoxResponseDto> getMultiBoxResponseDtos(Long spaceId) {
//        QUserLocation userLocation = QUserLocation.userLocation;
//
//        Map<MultiBox, List<UserLocation>> multiBoxUserLocationsMap = new JPAQuery<>(em)
//                .select(multiBox, userLocation)
//                .from(multiBox)
//                .leftJoin(multiBox.userLocations, userLocation).fetchJoin()
//                .where(multiBox.space.id.eq(spaceId))
//                .fetch()
//                .stream()
//                .collect(Collectors.groupingBy(
//                        tuple -> tuple.get(multiBox),
//                        Collectors.mapping(tuple -> tuple.get(userLocation), Collectors.toList())
//                ));
//
//        return multiBoxUserLocationsMap.entrySet().stream()
//                .map(entry -> new MultiBoxResponseDto(entry.getKey(), entry.getValue()))
//                .collect(Collectors.toList());
//    }
//
//
//}
