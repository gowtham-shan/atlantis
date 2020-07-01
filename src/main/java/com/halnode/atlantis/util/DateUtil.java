package com.halnode.atlantis.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DateUtil {

    public LocalDateTime getCurrentDateAsLocalDateTime() {
        return LocalDateTime.now();
    }

    public Date getCurrentDateAsDate() {
        LocalDateTime localDateTime = this.getCurrentDateAsLocalDateTime();
        return convertToDate(localDateTime);
    }

    public Date convertToDate(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    public long getCurrentDateInMills() {
        LocalDateTime localDateTime = this.getCurrentDateAsLocalDateTime();
        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    public Date getDateFromMills(long milliseconds) {
        return new Date(milliseconds);
    }
}
