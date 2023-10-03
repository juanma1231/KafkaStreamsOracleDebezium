package com.example.demo.store;

import com.example.demo.dto.Comunicaciones;
import com.example.demo.dto.ComunicacionesJoinEstado;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

public class IndexComunicacionesEstado {
    private KeyValueStore<String, ValueAndTimestamp<ComunicacionesJoinEstado>> transaccionVentaStoreKvStore;
    public static final String STORE_NAME = "indexComunicacionesEstado-store";

    public IndexComunicacionesEstado(ProcessorContext context) {
        this.transaccionVentaStoreKvStore = (KeyValueStore<String, ValueAndTimestamp<ComunicacionesJoinEstado>>) context.getStateStore(STORE_NAME);
    }

    public ValueAndTimestamp<ComunicacionesJoinEstado> get(String key) {
        return transaccionVentaStoreKvStore.get(key);
    }

    public KeyValueIterator<String, ValueAndTimestamp<ComunicacionesJoinEstado>> getRange(String from, String to) {
        return transaccionVentaStoreKvStore.range(from, to);
    }

    public KeyValueIterator<String, ValueAndTimestamp<ComunicacionesJoinEstado>> all() {
        return transaccionVentaStoreKvStore.all();
    }

}
