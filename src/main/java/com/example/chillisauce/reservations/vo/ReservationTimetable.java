package com.example.chillisauce.reservations.vo;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class ReservationTimetable {
    public static final Integer OPEN_HOUR = 7;
    public static final Integer CLOSE_HOUR = 22;
    public static final Set<TimeUnit> TIME_SET =
            IntStream.range(OPEN_HOUR, CLOSE_HOUR + 1)
                    .mapToObj(x->new TimeUnit(LocalTime.of(x, 0), LocalTime.of(x, 59)))
                    .collect(Collectors.toSet());
}
