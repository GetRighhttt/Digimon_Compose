package com.getrighhttt.digimoncompose.data.browse

import com.getrighhttt.digimoncompose.data.browse.repository.DapiBrowseRepository
import com.getrighhttt.digimoncompose.data.browse.source.DapiSource
import com.getrighhttt.digimoncompose.domain.browse.model.BrowseFailure
import com.getrighhttt.digimoncompose.domain.browse.model.BrowseResult
import com.getrighhttt.digimoncompose.domain.browse.model.DigimonSummary
import com.getrighhttt.digimoncompose.domain.browse.model.PageKey
import com.getrighhttt.digimoncompose.domain.browse.model.SearchCriteria
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Recorded response shapes exercise the real source and mapper without a live
 * dependency.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
class DapiBrowseRepositoryTest {
    @Test
    fun blankCriteriaAreOmittedAndSuccessMapsIntoDomain() =
        runTest {
            withRepo({ request ->
                assertNull(request.url.parameters["name"])
                assertNull(request.url.parameters["level"])
                assertEquals("0", request.url.parameters["page"])
                assertEquals("2", request.url.parameters["pageSize"])
                json(browseJson())
            }) { repo ->
                val result = success(repo.browse(SearchCriteria("  ", ""), PageKey(0)))
                assertEquals(
                    DigimonSummary(1, "Agumon", "https://digi-api.com/images/Agumon.png"),
                    result.items.single(),
                )
                assertNull(result.nextPage)
            }
        }

    @Test
    fun combinedCriteriaAreNormalizedAndEncoded() =
        runTest {
            withRepo({ request ->
                assertEquals("agumon (black)", request.url.parameters["name"])
                assertEquals("child", request.url.parameters["level"])
                json(browseJson())
            }) { repo ->
                success(repo.browse(SearchCriteria(" Agumon (Black) ", "Child"), PageKey(0)))
            }
        }

    @Test
    fun malformedPageSizeInContinuationIsNeverSentBackToServer() =
        runTest {
            val requests = mutableListOf<Url>()
            withRepo({ request ->
                requests += request.url
                if (requests.size == 1) {
                    json(
                        browseJson(
                            next =
                                "https://digi-api.com/api/v1/digimon?name=agumon&pageSize=pageSize=2&page=1",
                        ),
                    )
                } else {
                    json(browseJson(current = 1))
                }
            }) { repo ->
                val criteria = SearchCriteria("agumon")
                val first = success(repo.browse(criteria, PageKey(0)))
                success(repo.browse(criteria, first.nextPage!!))
                assertEquals(listOf("2"), requests[1].parameters.getAll("pageSize"))
                assertEquals("agumon", requests[1].parameters["name"])
                assertEquals("1", requests[1].parameters["page"])
            }
        }

    @Test
    fun omittedContentWithZeroElementsIsEmptySuccess() =
        runTest {
            withRepo({ json(browseJson(rows = emptyList(), omitContent = true)) }) { repo ->
                val page = success(repo.browse(SearchCriteria("zzz"), PageKey(0)))
                assertTrue(page.items.isEmpty())
                assertNull(page.nextPage)
            }
        }

    @Test
    fun omittedContentWithNonzeroElementsFails() =
        runTest {
            withRepo({ json(browseJson(omitContent = true)) }) { repo ->
                assertEquals(
                    BrowseResult.Failure(BrowseFailure.InvalidResponse),
                    repo.browse(SearchCriteria(), PageKey(0)),
                )
            }
        }

    @Test
    fun invalidJsonAndInvalidEnvelopeFailInsteadOfBecomingEmptyResults() =
        runTest {
            for (body in listOf("not json", "{}", browseJson(elements = 2, total = 2))) {
                withRepo({ json(body) }) { repo ->
                    assertEquals(
                        BrowseResult.Failure(BrowseFailure.InvalidResponse),
                        repo.browse(SearchCriteria(), PageKey(0)),
                    )
                }
            }
        }

    @Test
    fun wrongCurrentPageFails() =
        runTest {
            withRepo({ json(browseJson(current = 1)) }) { repo ->
                assertEquals(
                    BrowseResult.Failure(BrowseFailure.InvalidResponse),
                    repo.browse(SearchCriteria(), PageKey(0)),
                )
            }
        }

    @Test
    fun nonadvancingSkippedDuplicateAndForeignContinuationsFail() =
        runTest {
            val links =
                listOf(
                    "https://digi-api.com/api/v1/digimon?page=0",
                    "https://digi-api.com/api/v1/digimon?page=2",
                    "https://digi-api.com/api/v1/digimon?page=1&page=2",
                    "https://other.example/api/v1/digimon?page=1",
                    "http://digi-api.com/api/v1/digimon?page=1",
                    "https://digi-api.com/api/v1/level?page=1",
                )
            for (link in links) {
                withRepo({ json(browseJson(next = link)) }) { repo ->
                    assertEquals(
                        BrowseResult.Failure(BrowseFailure.InvalidResponse),
                        repo.browse(SearchCriteria(), PageKey(0)),
                        link,
                    )
                }
            }
        }

    @Test
    fun continuationCannotChangeActiveCriteria() =
        runTest {
            withRepo({
                json(browseJson(next = "https://digi-api.com/api/v1/digimon?name=gabumon&page=1"))
            }) { repo ->
                assertEquals(
                    BrowseResult.Failure(BrowseFailure.InvalidResponse),
                    repo.browse(SearchCriteria("agumon"), PageKey(0)),
                )
            }
        }

    @Test
    fun finalPageIgnoresMisleadingTotalPagesCount() =
        runTest {
            val body = browseJson(current = 3)
            withRepo({ json(body) }) { repo ->
                assertNull(success(repo.browse(SearchCriteria(), PageKey(3))).nextPage)
            }
        }

    @Test
    fun plainTextServerFailureRemainsFailureOnAdvertisedPage() =
        runTest {
            withRepo({
                respond(
                    "Internal Error",
                    HttpStatusCode.InternalServerError,
                    headersOf(HttpHeaders.ContentType, "text/plain"),
                )
            }) { repo ->
                assertEquals(
                    BrowseResult.Failure(BrowseFailure.Unavailable),
                    repo.browse(SearchCriteria(), PageKey(743)),
                )
            }
        }

    @Test
    fun badRequestIsNotConvertedToEmptyCatalog() =
        runTest {
            withRepo({
                respond("{\"error\":1,\"message\":\"Bad request\"}", HttpStatusCode.BadRequest)
            }) { repo ->
                assertEquals(
                    BrowseResult.Failure(BrowseFailure.RequestRejected),
                    repo.browse(SearchCriteria(), PageKey(0)),
                )
            }
        }

    @Test
    fun rateLimitIsRetryableUnavailable() =
        runTest {
            withRepo({ respond("Too many requests", HttpStatusCode.TooManyRequests) }) { repo ->
                assertEquals(
                    BrowseResult.Failure(BrowseFailure.Unavailable),
                    repo.browse(SearchCriteria(), PageKey(0)),
                )
            }
        }

    @Test
    fun missingAndInsecureImagesMapToFallback() =
        runTest {
            val rows =
                listOf(
                    Triple(1, "Agumon", null),
                    Triple(2, "Airdramon", "http://example.com/a.png"),
                )
            withRepo({ json(browseJson(rows)) }) { repo ->
                val page = success(repo.browse(SearchCriteria(), PageKey(0)))

                assertTrue(page.items.all { it.image == null })
            }
        }

    @Test
    fun levelsUseTheirOwnEnvelopeAndTraverseAllPages() =
        runTest {
            val pages = mutableListOf<String?>()
            withRepo({ request ->
                assertEquals("/api/v1/level", request.url.encodedPath)
                pages += request.url.parameters["page"]
                if (pages.size == 1) {
                    json(
                        levelJson(
                            0,
                            listOf("Child", "Adult"),
                            "https://digi-api.com/api/v1/level?pageSize=pageSize=5&page=1",
                        ),
                    )
                } else {
                    json(levelJson(1, listOf("Perfect")))
                }
            }) { repo ->
                assertEquals(listOf("Child", "Adult", "Perfect"), success(repo.levels()))
                assertEquals<List<String?>>(listOf("0", "1"), pages)
            }
        }

    @Test
    fun levelFailureAfterFirstPageDoesNotPublishPartialOptions() =
        runTest {
            withRepo({ request ->
                if (request.url.parameters["page"] == "0") {
                    json(levelJson(0, listOf("Child"), "https://digi-api.com/api/v1/level?page=1"))
                } else {
                    respond("Error", HttpStatusCode.InternalServerError)
                }
            }) { repo ->
                assertEquals(BrowseResult.Failure(BrowseFailure.Unavailable), repo.levels())
            }
        }

    @Test
    fun cancellationPropagatesWithoutBecomingUnavailable() =
        runTest {
            withRepo({ throw CancellationException("cancelled") }) { repo ->
                assertFailsWith<CancellationException> { repo.browse(SearchCriteria(), PageKey(0)) }
            }
        }

    @Test
    fun transportFailureMapsToUnavailable() =
        runTest {
            withRepo({ throw IllegalStateException("transport failed") }) { repo ->
                assertEquals(
                    BrowseResult.Failure(BrowseFailure.Unavailable),
                    repo.browse(SearchCriteria(), PageKey(0)),
                )
            }
        }

    private suspend fun withRepo(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
        block: suspend (DapiBrowseRepository) -> Unit,
    ) {
        val engine = MockEngine(handler)
        val client = HttpClient(engine) { expectSuccess = false }
        try {
            block(DapiBrowseRepository(DapiSource(client), pageSize = 2))
        } finally {
            client.close()
            engine.close()
        }
    }

    private fun MockRequestHandleScope.json(body: String) =
        respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))

    private fun <T> success(result: BrowseResult<T>): T =
        assertIs<BrowseResult.Success<T>>(result).value

    private fun browseJson(
        rows: List<Triple<Int, String, String?>> =
            listOf(Triple(1, "Agumon", "https://digi-api.com/images/Agumon.png")),
        current: Int = 0,
        next: String = "",
        elements: Int = rows.size,
        total: Int = rows.size,
        omitContent: Boolean = false,
    ) = buildJsonObject {
        put("ignoredField", "ignored")
        if (!omitContent) {
            putJsonArray("content") {
                rows.forEach { (id, name, image) ->
                    addJsonObject {
                        put("id", id)
                        put("name", name)
                        put("image", image)
                    }
                }
            }
        }
        put("pageable", metadata(current, elements, total, next))
    }.toString()

    private fun levelJson(
        current: Int,
        names: List<String>,
        next: String = "",
    ) = buildJsonObject {
        putJsonObject("content") {
            putJsonArray("fields") {
                names.forEachIndexed { i, name ->
                    addJsonObject {
                        put("id", i + 1)
                        put("name", name)
                    }
                }
            }
        }
        put("pageable", metadata(current, names.size, names.size, next))
    }.toString()

    private fun metadata(
        current: Int,
        elements: Int,
        total: Int,
        next: String,
    ) = buildJsonObject {
        put("currentPage", current)
        put("elementsOnPage", elements)
        put("totalElements", total)
        put("totalPages", current)
        put("nextPage", next)
    }
}
