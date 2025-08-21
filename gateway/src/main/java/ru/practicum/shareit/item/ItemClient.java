package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.commons.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory()).build());
    }

    public ResponseEntity<Object> findAll(Long userId, @PositiveOrZero Integer from, @Positive Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("/?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findByText(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", "from",
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> findById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> addItem(Long userId, ItemCreateDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> addComment(Long authorId, Long itemId, @Valid CommentCreateDto dto) {
        return post("/" + itemId + "/comment", authorId, dto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Long itemId) {
        return delete("/" + itemId, userId);
    }
}
