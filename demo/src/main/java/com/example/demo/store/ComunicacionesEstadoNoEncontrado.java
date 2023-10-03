package com.example.demo.store;

import com.example.demo.dto.ComunicacionesEstado;
import com.example.demo.dto.ComunicacionesJoinEstado;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

public class ComunicacionesEstadoNoEncontrado {
    private KeyValueStore<String, ValueAndTimestamp<ComunicacionesEstado>> comuniacionesKeyValueStore;
    public static final String STORE_NAME = "cumunicaciones-estado-no-encontrado-store";

    public ComunicacionesEstadoNoEncontrado(ProcessorContext context) {
        this.comuniacionesKeyValueStore = (KeyValueStore<String, ValueAndTimestamp<ComunicacionesEstado>>) context.getStateStore(STORE_NAME);
    }

    public ValueAndTimestamp<ComunicacionesEstado> get(String key) {
        return comuniacionesKeyValueStore.get(key);
    }

    public KeyValueIterator<String, ValueAndTimestamp<ComunicacionesEstado>> getRange(String from, String to) {
        return comuniacionesKeyValueStore.range(from, to);
    }
    public KeyValueIterator<String, ValueAndTimestamp<ComunicacionesEstado>> getPrefix(String index) {

        return comuniacionesKeyValueStore.prefixScan(index, Serdes.String().serializer());

    }
    //

    public KeyValueIterator<String, ValueAndTimestamp<ComunicacionesEstado>> all() {
        return comuniacionesKeyValueStore.all();
    }
}
