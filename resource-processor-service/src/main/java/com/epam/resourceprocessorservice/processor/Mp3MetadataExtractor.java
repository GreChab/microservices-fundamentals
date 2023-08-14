package com.epam.resourceprocessorservice.processor;

import ch.qos.logback.core.util.Duration;
import com.epam.resourceprocessorservice.model.SongMetadata;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.web.multipart.MultipartFile;

public class Mp3MetadataExtractor {
    @SneakyThrows
    public SongMetadata extractMetadata(MultipartFile file) {
        Metadata metadata = new Metadata();
        new Mp3Parser().parse(file.getInputStream(), new BodyContentHandler(), metadata, new ParseContext());

        return SongMetadata.builder()
                .withName(metadata.get("dc:title"))
                .withArtist(metadata.get("xmpDM:artist"))
                .withAlbum(metadata.get("xmpDM:album"))
                .withYear(Integer.valueOf(metadata.get("xmpDM:releaseDate")))
                .withLength(formatSecondsToMinutesAndSeconds(metadata.get("xmpDM:duration")))
                .build();
    }

    private static String formatSecondsToMinutesAndSeconds(String s) {
        return DurationFormatUtils.formatDuration(
                Duration.buildBySeconds(Double.parseDouble(s)).getMilliseconds(),
                "mm:ss");
    }
}
