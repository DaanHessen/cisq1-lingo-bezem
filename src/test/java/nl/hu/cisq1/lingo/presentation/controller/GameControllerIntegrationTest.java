package nl.hu.cisq1.lingo.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.hu.cisq1.lingo.presentation.dto.request.GuessRequest;
import nl.hu.cisq1.lingo.presentation.dto.request.StartGameRequest;
import nl.hu.cisq1.lingo.repository.GameRepository;
import nl.hu.cisq1.lingo.words.data.WordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GameControllerIntegrationTest {
    private static final String TEST_WORD_5 = "bruhh";
    private static final String TEST_WORD_6 = "kroket";
    private static final String TEST_WORD_7 = "student";
    private static final String TEST_USERNAME = "WillSmith";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private WordRepository wordRepository;

    @BeforeEach
    void setup() {
        gameRepository.deleteAll();
        wordRepository.deleteAll();
        wordRepository.save(new Word(TEST_WORD_5));
        wordRepository.save(new Word(TEST_WORD_6));
        wordRepository.save(new Word(TEST_WORD_7));
    }

    @AfterEach
    void cleanup() {
        gameRepository.deleteAll();
        wordRepository.deleteAll();
    }

    @Test
    @DisplayName("Start a new game with username")
    void startNewGame() throws Exception {
        StartGameRequest request = new StartGameRequest(TEST_USERNAME, false);

        mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.score", is(0)))
                .andExpect(jsonPath("$.state", is("IN_ROUND")))
                .andExpect(jsonPath("$.currentRound", notNullValue()))
                .andExpect(jsonPath("$.currentRound.hint", notNullValue()))
                .andExpect(jsonPath("$.currentRound.attemptsRemaining", is(5)));
    }

    @Test
    @DisplayName("Start a new game with random word length")
    void startNewGameWithRandomLength() throws Exception {
        StartGameRequest request = new StartGameRequest(TEST_USERNAME, true);

        mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.state", is("IN_ROUND")))
                .andExpect(jsonPath("$.currentRound", notNullValue()));
    }

    @Test
    @DisplayName("Get an existing game by ID")
    void getGame() throws Exception {
        StartGameRequest request = new StartGameRequest(TEST_USERNAME, false);
        MvcResult result = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/games/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gameId)))
                .andExpect(jsonPath("$.state", is("IN_ROUND")))
                .andExpect(jsonPath("$.currentRound", notNullValue()));
    }

    @Test
    @DisplayName("Getting a non-existing game should return 404")
    void getNonExistingGame() throws Exception {
        String randomId = "00000000-0000-0000-0000-000000000000";

        mockMvc.perform(get("/games/" + randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Make a correct guess")
    void makeCorrectGuess() throws Exception {
        StartGameRequest startRequest = new StartGameRequest(TEST_USERNAME, false);
        MvcResult result = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("id").asText();

        GuessRequest guessRequest = new GuessRequest(TEST_WORD_5);

        mockMvc.perform(post("/games/" + gameId + "/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guessRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback", notNullValue()))
                .andExpect(jsonPath("$.feedback.marks", hasSize(5)))
                .andExpect(jsonPath("$.feedback.marks[*]", everyItem(is("CORRECT"))))
                .andExpect(jsonPath("$.feedback.correct", is(true)))
                .andExpect(jsonPath("$.gameState.state", is("WAITING_FOR_ROUND")))
                .andExpect(jsonPath("$.gameState.currentRound.attemptsRemaining", is(4)));
    }

    @Test
    @DisplayName("Make an incorrect guess")
    void makeIncorrectGuess() throws Exception {
        StartGameRequest startRequest = new StartGameRequest(TEST_USERNAME, false);
        MvcResult result = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("id").asText();

        GuessRequest guessRequest = new GuessRequest("wrong");

        mockMvc.perform(post("/games/" + gameId + "/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guessRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback", notNullValue()))
                .andExpect(jsonPath("$.feedback.marks", hasSize(5)))
                .andExpect(jsonPath("$.gameState.currentRound.hint", notNullValue()))
                .andExpect(jsonPath("$.gameState.currentRound.attemptsRemaining", is(4)))
                .andExpect(jsonPath("$.gameState.state", is("IN_ROUND")));
    }

    @Test
    @DisplayName("Start a new round after winning")
    void startNewRound() throws Exception {
        StartGameRequest startRequest = new StartGameRequest(TEST_USERNAME, false);
        MvcResult result = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("id").asText();

        GuessRequest guessRequest = new GuessRequest(TEST_WORD_5);
        mockMvc.perform(post("/games/" + gameId + "/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guessRequest)));

        mockMvc.perform(post("/games/" + gameId + "/rounds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is("IN_ROUND")))
                .andExpect(jsonPath("$.currentRound.attemptsRemaining", is(5)))
                .andExpect(jsonPath("$.currentRound.hint", notNullValue()));
    }

    @Test
    @DisplayName("Start a new round with random word length")
    void startNewRoundWithRandomLength() throws Exception {
        StartGameRequest startRequest = new StartGameRequest(TEST_USERNAME, false);
        MvcResult result = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("id").asText();

        GuessRequest guessRequest = new GuessRequest(TEST_WORD_5);
        mockMvc.perform(post("/games/" + gameId + "/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guessRequest)));

        mockMvc.perform(post("/games/" + gameId + "/rounds?random=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is("IN_ROUND")))
                .andExpect(jsonPath("$.currentRound.attemptsRemaining", is(5)));
    }

    @Test
    @DisplayName("Cannot start new round when in active round throws InvalidActionException")
    void cannotStartNewRoundWhenInRound() throws Exception {
        StartGameRequest startRequest = new StartGameRequest(TEST_USERNAME, false);
        MvcResult result = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(post("/games/" + gameId + "/rounds"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Forfeit a game")
    void forfeitGame() throws Exception {
        StartGameRequest startRequest = new StartGameRequest(TEST_USERNAME, false);
        MvcResult result = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String gameId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/games/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is("ELIMINATED")));
    }

    @Test
    @DisplayName("Get scoreboard")
    void getScoreboard() throws Exception {
        for (int i = 1; i <= 3; i++) {
            StartGameRequest startRequest = new StartGameRequest("Player" + i, false);
            MvcResult result = mockMvc.perform(post("/games")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(startRequest)))
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            String gameId = objectMapper.readTree(response).get("id").asText();

            GuessRequest guessRequest = new GuessRequest(TEST_WORD_5);
            mockMvc.perform(post("/games/" + gameId + "/guess")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(guessRequest)));
        }

        mockMvc.perform(get("/games/scoreboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username", notNullValue()))
                .andExpect(jsonPath("$[0].score", greaterThan(0)));
    }

    @Test
    @DisplayName("Scoreboard returns empty list when no games")
    void getEmptyScoreboard() throws Exception {
        mockMvc.perform(get("/games/scoreboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Scoreboard is sorted by score descending")
    void scoreboardSortedByScore() throws Exception {
        StartGameRequest request1 = new StartGameRequest("HighScorer", false);
        MvcResult result1 = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andReturn();
        String gameId1 = objectMapper.readTree(result1.getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(post("/games/" + gameId1 + "/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GuessRequest(TEST_WORD_5))));

        StartGameRequest request2 = new StartGameRequest("MidScorer", false);
        MvcResult result2 = mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andReturn();
        String gameId2 = objectMapper.readTree(result2.getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(post("/games/" + gameId2 + "/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GuessRequest("wrong"))));
        mockMvc.perform(post("/games/" + gameId2 + "/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GuessRequest(TEST_WORD_5))));

        mockMvc.perform(get("/games/scoreboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("HighScorer")))
                .andExpect(jsonPath("$[0].score", is(25)))
                .andExpect(jsonPath("$[1].username", is("MidScorer")))
                .andExpect(jsonPath("$[1].score", is(20)));
    }
}
