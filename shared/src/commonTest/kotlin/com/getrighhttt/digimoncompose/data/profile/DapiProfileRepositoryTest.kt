package com.getrighhttt.digimoncompose.data.profile

import com.getrighhttt.digimoncompose.data.browse.source.DapiSource
import com.getrighhttt.digimoncompose.data.profile.repository.DapiProfileRepository
import com.getrighhttt.digimoncompose.domain.profile.model.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class DapiProfileRepositoryTest {
    @Test fun profileMapsClassificationSkillsAndPreferredEnglishDescription() = runTest {
        withRepo("""{
            "id":1,"name":"Agumon","images":[{"href":"https://digi-api.com/a.png"}],
            "levels":[{"id":4,"level":"Child"}],"types":[{"type":"Reptile"}],
            "attributes":[{"attribute":"Vaccine"}],"releaseDate":"1997","xAntibody":false,
            "descriptions":[{"language":"jap","description":"Japanese"},
                {"language":"en_us","origin":"other","description":"Other English"},
                {"language":"en_us","origin":"reference_book","description":"Reference English"}],
            "skills":[{"skill":"Baby Flame","description":"A skill description"}],
            "priorEvolutions":[{"id":2,"digimon":"Previous"}],
            "nextEvolutions":[{"id":3,"digimon":"Next"},{"id":3,"digimon":"Next"}]
        }""") { repo ->
            val profile = assertIs<ProfileResult.Success>(repo.profile(1)).profile
            assertEquals(listOf("Child"), profile.levels)
            assertEquals(listOf("Vaccine"), profile.attributes)
            assertEquals("Reference English", profile.description)
            assertEquals("Baby Flame", profile.skills.single().name)
            assertEquals(1, profile.nextCount)
            assertEquals("1997", profile.debut)
        }
    }

    @Test fun minimalProfileHasExplicitMissingValues() = runTest {
        withRepo("""{"id":1,"name":"Agumon"}""") { repo ->
            val profile = assertIs<ProfileResult.Success>(repo.profile(1)).profile
            assertNull(profile.description); assertNull(profile.image); assertNull(profile.debut)
            assertTrue(profile.skills.isEmpty())
            assertNull(profile.xAntibody); assertNull(profile.nextCount); assertNull(profile.priorCount)
        }
    }

    @Test fun wrongIdentifierAndMalformedPayloadFail() = runTest {
        for (body in listOf("""{"id":2,"name":"Other"}""", "{}", "not json")) {
            withRepo(body) { repo -> assertEquals(ProfileResult.Failure(ProfileFailure.InvalidResponse), repo.profile(1)) }
        }
    }

    @Test fun recognizedNotFoundIsDifferentFromOtherBadRequests() = runTest {
        withRepo("""{"message":"Digimon not found"}""", HttpStatusCode.BadRequest) { repo ->
            assertEquals(ProfileResult.Failure(ProfileFailure.NotFound), repo.profile(1))
        }
        withRepo("Bad request", HttpStatusCode.BadRequest) { repo ->
            assertEquals(ProfileResult.Failure(ProfileFailure.RequestRejected), repo.profile(1))
        }
    }

    @Test fun plainTextServerFailureIsRetryable() = runTest {
        withRepo("Internal Error", HttpStatusCode.InternalServerError) { repo ->
            assertEquals(ProfileResult.Failure(ProfileFailure.Unavailable), repo.profile(1))
        }
    }

    private suspend fun withRepo(body: String, status: HttpStatusCode = HttpStatusCode.OK,
        block: suspend (DapiProfileRepository) -> Unit) {
        val engine = MockEngine { request ->
            assertEquals("/api/v1/digimon/1", request.url.encodedPath)
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val client = HttpClient(engine) { expectSuccess = false }
        try { block(DapiProfileRepository(DapiSource(client))) } finally { client.close(); engine.close() }
    }
}
