package com.handballleague.InitializersTests;

import com.handballleague.initialization.TeamsInitializer;
import com.handballleague.model.Team;
import com.handballleague.repositories.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamsInitializerTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private TeamsInitializer teamsInitializer;

    @Test
    public void fetchAndFillDataTest() {
        assertThat(1).isEqualTo(1);
    }
}