package co.com.techtest.sqs.listener.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanBodyUtilTest {

    @Test
    void shouldRemoveTabsNewlinesAndBackslashes() {
        String input = "Hello\tWorld\nTest\\String";
        String expected = "HelloWorldTestString";

        String result = CleanBodyUtil.cleanBody(input);

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnSameStringWhenNoSpecialCharacters() {
        String input = "HelloWorld";

        String result = CleanBodyUtil.cleanBody(input);

        assertEquals(input, result);
    }

    @Test
    void shouldHandleEmptyString() {
        String input = "";

        String result = CleanBodyUtil.cleanBody(input);

        assertEquals("", result);
    }
}
