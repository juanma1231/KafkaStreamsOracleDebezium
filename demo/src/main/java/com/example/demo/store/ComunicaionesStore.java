package com.example.demo.store;

import com.example.demo.dto.Comunicaciones;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

public class ComunicaionesStore {
    private KeyValueStore<String, ValueAndTimestamp<Comunicaciones>> transaccionVentaStoreKvStore;
    public static final String STORE_NAME = "cumunicaciones-store";

    public ComunicaionesStore(ProcessorContext context) {
        this.transaccionVentaStoreKvStore = (KeyValueStore<String, ValueAndTimestamp<Comunicaciones>>) context.getStateStore(STORE_NAME);
    }

    public ValueAndTimestamp<Comunicaciones> get(String key) {
        return transaccionVentaStoreKvStore.get(key);
    }

    public KeyValueIterator<String, ValueAndTimestamp<Comunicaciones>> getRange(String from, String to) {
        return transaccionVentaStoreKvStore.range(from, to);
    }

    public KeyValueIterator<String, ValueAndTimestamp<Comunicaciones>> all() {
        return transaccionVentaStoreKvStore.all();
    }

}
