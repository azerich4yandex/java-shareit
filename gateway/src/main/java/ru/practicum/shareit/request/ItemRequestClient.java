package ru.practicum.shareit.request;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.commons.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory()).build());
    }


    public ResponseEntity<Object> findAll(Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("/all", null, parameters);
    }

    public ResponseEntity<Object> getByRequestorId(Long requestorId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", requestorId, parameters);
    }

    public ResponseEntity<Object> getById(Long itemRequestId) {
        return get("/" + itemRequestId);
    }

    public ResponseEntity<Object> createItemRequest(Long requestorId, ItemRequestCreateDto dto) {
        return post("", requestorId, dto);
    }
}
