package co.com.techtest.model.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationType {
  CREATE_EVENT("createEvent", "/api/v1/event", "Create Event Request", "createEventRQ", "Create Event Response", "createEventRS");

  private final String name;
  private final String path;
  private final String nameRequest;
  private final String kvRequest;
  private final String nameResponse;
  private final String kvResponse;
}
