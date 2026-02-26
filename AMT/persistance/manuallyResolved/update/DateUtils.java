import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

private static final DateTimeFormatter ORACLE_TS =
    DateTimeFormatter.ofPattern("dd-MMM-yy hh.mm.ss.nnnnnnnnn a", Locale.ENGLISH);

private static final DateTimeFormatter ISO_LOCAL =
    DateTimeFormatter.ISO_LOCAL_DATE_TIME;

private static final DateTimeFormatter SPACE_FMT =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

private static final DateTimeFormatter ORACLE_TS =
        DateTimeFormatter.ofPattern("dd-MMM-yy hh.mm.ss.nnnnnnnnn a", Locale.ENGLISH);

private Timestamp parseToTimestamp(String raw) {
    try {
        if (raw == null) {
            throw new IllegalArgumentException("cdIntegrationStatusDate is null");
        }

        raw = raw.trim();

        if (raw.isEmpty()) {
            throw new IllegalArgumentException("cdIntegrationStatusDate is blank");
        }

        // 1) ISO instant (UTC): 2026-02-24T18:30:00Z
        if (raw.endsWith("Z")) {
            return Timestamp.from(Instant.parse(raw));
        }

        // 2) Oracle-style: 13-FEB-26 02.22.51.302000000 PM
        //    (normalize to uppercase to ensure FEB/PM matches)
        try {
            LocalDateTime ldt = LocalDateTime.parse(raw.toUpperCase(Locale.ENGLISH), ORACLE_TS);
            return Timestamp.valueOf(ldt);
        } catch (Exception ignored) {
            // continue
        }

        // 3) ISO local: 2026-02-24T18:30:00
        try {
            return Timestamp.valueOf(LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (Exception ignored) {
            // continue
        }

        // 4) Fallback: yyyy-MM-dd HH:mm:ss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Timestamp.valueOf(LocalDateTime.parse(raw, formatter));

    } catch (Exception e) {
        throw new IllegalArgumentException("Invalid cdIntegrationStatusDate format: " + raw, e);
    }
}
