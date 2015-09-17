package uk.gov.register.presentation.representations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.gov.register.presentation.EntryView;
import uk.gov.register.presentation.view.EntryListView;

import java.io.IOException;
import java.util.List;

public class ListResultJsonSerializer extends JsonSerializer<EntryListView> {
    @Override
    public void serialize(EntryListView value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        List<EntryView> entries = value.getEntries();
        JsonSerializer<Object> listSerializer = serializers.findValueSerializer(List.class);
        listSerializer.serialize(entries, gen, serializers);
    }
}
