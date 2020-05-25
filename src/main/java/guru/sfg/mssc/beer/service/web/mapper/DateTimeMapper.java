//: guru.sfg.mssc.beer.service.web.mapper.DateTimeMapper.java


package guru.sfg.mssc.beer.service.web.mapper;


import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;


@Component
public class DateTimeMapper {

    public OffsetDateTime asOffsetDateTime(Timestamp ts) {

        if (Objects.isNull(ts)) {
            return null;
        }

        return OffsetDateTime.of(ts.toLocalDateTime().getYear(),
                ts.toLocalDateTime().getMonthValue(),
                ts.toLocalDateTime().getDayOfMonth(),
                ts.toLocalDateTime().getHour(),
                ts.toLocalDateTime().getMinute(),
                ts.toLocalDateTime().getSecond(),
                ts.toLocalDateTime().getNano(),
                ZoneOffset.UTC);
    }

    public Timestamp asTimestamp(OffsetDateTime offsetDateTime) {

        if (Objects.isNull(offsetDateTime)) {
            return null;
        }

        return Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime());
    }

}///:~