package com.epam.songservice.service.integration

import com.epam.songservice.model.Song
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.context.jdbc.Sql
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationSongServiceTest extends Specification {
    @LocalServerPort
    private int serverPort

    @Autowired
    private TestRestTemplate restTemplate

    @Sql(value = "/insert_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    def 'should return song data'() {
        when:
        RequestEntity<Void> request = RequestEntity
                .get(new URI("http://localhost:" + serverPort + "/songs/1"))
                .build()
        ResponseEntity response = restTemplate.exchange(request, Song.class)
        Song receivedSong = response.getBody()

        then:
        response.getStatusCode().is2xxSuccessful()
        receivedSong.name == "sample name"
    }

    def 'should return 404 for not existing song'() {
        when:
        RequestEntity<Void> request = RequestEntity
                .get(new URI("http://localhost:" + serverPort + "/songs/999"))
                .build()
        ResponseEntity response = restTemplate.exchange(request, Song.class)

        then:
        response.getStatusCode().is4xxClientError()
    }
}
