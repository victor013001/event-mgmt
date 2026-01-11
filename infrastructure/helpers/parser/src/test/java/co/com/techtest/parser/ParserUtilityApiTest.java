package co.com.techtest.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParserUtilityApiTest {

    @Test
    void parserUtilityApi_ParserToString_ShouldReturnJsonString() {
        TestObject testObject = new TestObject("test", 123);

        String result = ParserUtilityApi.parserToString(testObject);
        assertThat(result).isNotNull().contains("test");
    }

    @Test
    void parserUtilityApi_ParserToString_WithUnserializableObject_ShouldReturnNull() {
        Object unserializableObject = new Object() {
        };

        String result = ParserUtilityApi.parserToString(unserializableObject);
        assertThat(result).isNull();
    }

    private record TestObject(String name, int value) {
    }
}
