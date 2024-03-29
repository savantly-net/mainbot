package net.savantly.mainbot.dom.chatmessage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChatResponseDto {

    private String systemMessage;
    private String userMessage;
    private String aiMessage;
    private List<ResponseMessageDocRefDto> docRefs = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime created = ZonedDateTime.now();
}
