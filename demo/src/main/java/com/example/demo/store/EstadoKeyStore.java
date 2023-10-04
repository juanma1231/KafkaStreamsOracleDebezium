package com.example.demo.store;

import com.example.demo.dto.ComunicacionesEstado;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

public class EstadoKeyStore {
    private KeyValueStore<String, ValueAndTimestamp<String>> comuniacionesKeyValueStore;
    public static final String STORE_NAME = "estado_key_store";

    public EstadoKeyStore(ProcessorContext context) {
        this.comuniacionesKeyValueStore = (KeyValueStore<String, ValueAndTimestamp<String>>) context.getStateStore(STORE_NAME);
    }

    public ValueAndTimestamp<String> get(String key) {
        return comuniacionesKeyValueStore.get(key);
    }

    public KeyValueIterator<String, ValueAndTimestamp<String>> getRange(String from, String to) {
        return comuniacionesKeyValueStore.range(from, to);
    }
    public KeyValueIterator<String, ValueAndTimestamp<String>> getPrefix(String index) {

        return comuniacionesKeyValueStore.prefixScan(index, Serdes.String().serializer());

    }
    //

    public KeyValueIterator<String, ValueAndTimestamp<String>> all() {
        return comuniacionesKeyValueStore.all();
    }
}
