package com.example.demo.store;

import com.example.demo.dto.Comunicaciones;
import com.example.demo.dto.ComunicacionesJoinEstado;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

public class IndexComunicacionesEstado {
    private KeyValueStore<String, ValueAndTimestamp<String>> transaccionVentaStoreKvStore;
    public static final String STORE_NAME = "indexComunicacionesEstado-store";

    public IndexComunicacionesEstado(ProcessorContext context) {
        this.transaccionVentaStoreKvStore = (KeyValueStore<String, ValueAndTimestamp<String>>) context.getStateStore(STORE_NAME);
    }

    public ValueAndTimestamp<String> get(String key) {
        return transaccionVentaStoreKvStore.get(key);
    }

    public KeyValueIterator<String, ValueAndTimestamp<String>> getRange(String from, String to) {
        return transaccionVentaStoreKvStore.range(from, to);
    }

    public KeyValueIterator<String, ValueAndTimestamp<String>> all() {
        return transaccionVentaStoreKvStore.all();
    }

}
