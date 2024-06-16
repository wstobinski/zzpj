package com.handballleague.servicesTests;

import com.handballleague.exceptions.ImageProcessingException;
import com.handballleague.services.VisionService;
import com.handballleague.util.HttpUrlConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisionServiceTest {


    @Mock
    private HttpUrlConnectionFactory connectionFactory;

    @InjectMocks
    private VisionService underTestService;

    private OutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream(); // Mock OutputStream
    }


    @Test
    void detectText_WithValidResponse_ReturnsText() throws IOException {
        // Given
        String base64Image = "base64ImageString";
        String jsonResponse = "{\"responses\": [{\"textAnnotations\": [{\"description\": \"Detected text\"}]}]}";
        InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes());

        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockHttpURLConnection.getInputStream()).thenReturn(inputStream);
        when(mockHttpURLConnection.getOutputStream()).thenReturn(outputStream);

        when(connectionFactory.createHttpURLConnection(any(URL.class))).thenReturn(mockHttpURLConnection);

        // When
        String result = underTestService.detectText(base64Image);

        // Then
        assertThat(result).isEqualTo("Detected text");
        verify(mockHttpURLConnection).setDoOutput(true);
        verify(mockHttpURLConnection).setRequestMethod("POST");
        verify(mockHttpURLConnection).setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    void detectText_WithInvalidResponseCode_ThrowsException() throws IOException {
        // Given
        String base64Image = "base64ImageString";
        String errorResponse = "{\"error\": {\"message\": \"Invalid request\"}}";
        InputStream errorStream = new ByteArrayInputStream(errorResponse.getBytes());

        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(mockHttpURLConnection.getErrorStream()).thenReturn(errorStream);
        when(mockHttpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(connectionFactory.createHttpURLConnection(any(URL.class))).thenReturn(mockHttpURLConnection);


        // When
        Exception exception = assertThrows(ImageProcessingException.class, () -> {
            underTestService.detectText(base64Image);
        });

        // Then
        assertThat(exception.getMessage()).contains("Invalid request");
        verify(mockHttpURLConnection).setDoOutput(true);
        verify(mockHttpURLConnection).setRequestMethod("POST");
        verify(mockHttpURLConnection).setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    void detectText_WithErrorStream_ThrowsException() throws IOException {
        // Given
        String base64Image = "base64ImageString";
        String errorResponse = "{\"error\": {\"message\": \"Some error occurred\"}}";
        InputStream errorStream = new ByteArrayInputStream(errorResponse.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream(); // Mock OutputStream

        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
        when(mockHttpURLConnection.getErrorStream()).thenReturn(errorStream);
        when(mockHttpURLConnection.getOutputStream()).thenReturn(outputStream);
        when(connectionFactory.createHttpURLConnection(any(URL.class))).thenReturn(mockHttpURLConnection);

        // When
        Exception exception = assertThrows(ImageProcessingException.class, () -> {
            underTestService.detectText(base64Image);
        });

        // Then
        assertThat(exception.getMessage()).contains("Some error occurred");
        verify(mockHttpURLConnection).setDoOutput(true);
        verify(mockHttpURLConnection).setRequestMethod("POST");
        verify(mockHttpURLConnection).setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    void detectText_WithEmptyTextAnnotations_ReturnsEmptyString() throws IOException {
        // Given
        String base64Image = "base64ImageString";
        String jsonResponse = "{\"responses\": [{\"textAnnotations\": []}]}";
        InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes());

        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockHttpURLConnection.getInputStream()).thenReturn(inputStream);
        when(mockHttpURLConnection.getOutputStream()).thenReturn(outputStream); // Provide the mock OutputStream
        when(connectionFactory.createHttpURLConnection(any(URL.class))).thenReturn(mockHttpURLConnection);


        Exception exception = assertThrows(ImageProcessingException.class, () -> {
            underTestService.detectText(base64Image);
        });
        assertThat(exception.getMessage()).contains("Text not provided in the response");
        verify(mockHttpURLConnection).setDoOutput(true);
        verify(mockHttpURLConnection).setRequestMethod("POST");
        verify(mockHttpURLConnection).setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    }


}