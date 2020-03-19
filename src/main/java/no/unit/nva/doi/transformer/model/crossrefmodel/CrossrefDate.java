package no.unit.nva.doi.transformer.model.crossrefmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CrossrefDate {

    @JsonProperty("date-parts")
    private int[][] dateParts; //
    @JsonProperty("date-time")
    private String dateTime;
    @JsonProperty("timestamp")
    private long timestamp;

    public int[][] getDateParts() {
        return dateParts;
    }

    public void setDateParts(int[][] input) {
        this.dateParts = input;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String input) {
        this.dateTime = input;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double input) {
        this.timestamp = input;
    }

    public void extractEarliestYear() {
        Optional<Integer> datetimeYear=extractYearFromDateTime();
        Optional<Integer> timeStampYear= extractYearFromTimeStamp();

    }

    private Optional<Integer> extractYearFromTimeStamp() {
        return Optional.of(timestamp).map(Instant.ofEpochMilli(timestamp))
    }

    private Optional<Integer> extractYearFromDateTime() {

        return Optional.ofNullable(this.dateTime)
                        .map(d -> LocalDateTime.parse(this.dateTime, DateTimeFormatter.ISO_INSTANT))
                        .map(LocalDateTime::getYear);

    }
}
