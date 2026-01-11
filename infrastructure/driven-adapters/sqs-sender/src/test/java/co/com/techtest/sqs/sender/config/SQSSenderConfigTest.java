package co.com.techtest.sqs.sender.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSSenderConfigTest {

    @Mock
    private SQSSenderProperties properties;

    @InjectMocks
    private SQSSenderConfig sqsSenderConfig;

    @Test
    void shouldCreateSqsAsyncClient() {
        when(properties.region()).thenReturn("us-east-1");

        SqsAsyncClient client = sqsSenderConfig.sqsAsyncClient(properties);

        assertNotNull(client);
    }
}
