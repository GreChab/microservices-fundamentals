package com.epam.songservice.service.unit

import com.epam.songservice.model.Song
import com.epam.songservice.repository.SongRepository
import com.epam.songservice.service.SongService
import spock.lang.Specification
import spock.lang.Subject

class SongServiceTest extends Specification{
    private static final String EMPTY_STRING = ''
    private static final String ANY = 'any'
    private final SongRepository repository = Mock(SongRepository)

    @Subject
    private final SongService service = new SongService(repository)

    def 'metadata should be validated with success'() {
        given:
        def song = songWithCorrectMetadata()

        when:
        def metadataValidatedCorrect = service.isSongMetadataCorrect(song)

        then:
        metadataValidatedCorrect
    }

    def 'metadata should not be validated with success'() {
        given:
        def song = songWithCustomMetadata(name, artist, album, length, year, resourceId)

        when:
        def metadataValidatedCorrect = service.isSongMetadataCorrect(song)

        then:
        !metadataValidatedCorrect

        where:
        name         | artist | album | length | year | resourceId
        ''           | ANY    | ANY   | ANY    | 1    | 1
        EMPTY_STRING | ANY    | ANY   | ANY    | 1    | 1
        null         | ANY    | ANY   | ANY    | 1    | 1
    }

    def songWithCorrectMetadata() {
        return Song.builder()
                .withName("sample_name")
                .withArtist("sample_artist")
                .withAlbum("sample_album")
                .withLength("sample_length")
                .withYear(2000)
                .withResourceId(1)
                .build()
    }

    def songWithCustomMetadata(String name, String artist, String album, String length, Long year, Long resourceId) {
        return Song.builder()
                .withName(name)
                .withArtist(artist)
                .withAlbum(album)
                .withLength(length)
                .withYear(year)
                .withResourceId(resourceId)
                .build()
    }
}
