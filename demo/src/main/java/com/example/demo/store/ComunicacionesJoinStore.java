package com.example.demo.store;

import com.example.demo.dto.ComunicacionesJoinEstado;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

public class ComunicacionesJoinStore {

    private KeyValueStore<String, ValueAndTimestamp<ComunicacionesJoinEstado>> comuniacionesKeyValueStore;
    public static final String STORE_NAME = "cumunicaciones-join-store";

    public ComunicacionesJoinStore(ProcessorContext context) {
        this.comuniacionesKeyValueStore = (KeyValueStore<String, ValueAndTimestamp<ComunicacionesJoinEstado>>) context.getStateStore(STORE_NAME);
    }

    public ValueAndTimestamp<ComunicacionesJoinEstado> get(String key) {
        return comuniacionesKeyValueStore.get(key);
    }

    public KeyValueIterator<String, ValueAndTimestamp<ComunicacionesJoinEstado>> getRange(String from, String to) {
        return comuniacionesKeyValueStore.range(from, to);
    }
    public KeyValueIterator<String, ValueAndTimestamp<ComunicacionesJoinEstado>> getPrefix(String index) {

        return comuniacionesKeyValueStore.prefixScan(index, Serdes.String().serializer());

    }
    //

    public KeyValueIterator<String, ValueAndTimestamp<ComunicacionesJoinEstado>> all() {
        return comuniacionesKeyValueStore.all();
    }

}
