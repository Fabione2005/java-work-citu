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

private Timestamp parseToTimestamp(String raw) {
    String v = raw == null ? null : raw.trim();
    if (v == null || v.isEmpty()) return null;

    try {
        // 1) ISO instant: 2026-02-13T14:22:51.302Z
        if (v.endsWith("Z")) {
            return Timestamp.from(Instant.parse(v));
        }

        // 2) Oracle style: 13-FEB-26 02.22.51.302000000 PM
        // NOTE: uses 2-digit year; Java resolves it relative to a pivot.
        try {
            LocalDateTime ldt = LocalDateTime.parse(v.toUpperCase(Locale.ENGLISH), ORACLE_TS);
            return Timestamp.valueOf(ldt);
        } catch (Exception ignored) {}

        // 3) ISO local: 2026-02-13T14:22:51.302
        try {
            return Timestamp.valueOf(LocalDateTime.parse(v, ISO_LOCAL));
        } catch (Exception ignored) {}

        // 4) Space format: 2026-02-13 14:22:51
        return Timestamp.valueOf(LocalDateTime.parse(v, SPACE_FMT));

    } catch (Exception e) {
        throw new IllegalArgumentException("Invalid cdIntegrationStatusDate format: " + raw, e);
    }
}
