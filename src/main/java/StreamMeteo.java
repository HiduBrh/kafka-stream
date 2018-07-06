/**
 * Created by idu on 04/07/2018.
 */
import javassist.bytecode.ByteArray;
import model.Commune;
import model.MeteoDataRecord;
import model.MinMax;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import pojo.JsonPOJODeserializer;
import pojo.JsonPOJOSerializer;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class StreamMeteo {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-meteo-data");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);


        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder builder = new StreamsBuilder();

        // TODO: the following can be removed with a serialization factory
        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<MeteoDataRecord> meteoRecordSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", MeteoDataRecord.class);
        meteoRecordSerializer.configure(serdeProps, false);

        final Deserializer<MeteoDataRecord> meteoRecordDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", MeteoDataRecord.class);
        meteoRecordDeserializer.configure(serdeProps, false);

        final Serde<MeteoDataRecord> meteoRecordSerde = Serdes.serdeFrom(meteoRecordSerializer, meteoRecordDeserializer);

        //min max serdes
        final Serializer<MinMax> minmaxSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", MinMax.class);
        minmaxSerializer.configure(serdeProps, false);

        final Deserializer<MinMax> minmaxDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", MinMax.class);
        minmaxDeserializer.configure(serdeProps, false);

        final Serde<MinMax> minmaxSerde = Serdes.serdeFrom(minmaxSerializer, minmaxDeserializer);

        KStream<String, MeteoDataRecord> records = builder.stream("raw_station_data", Consumed.with(Serdes.String(), meteoRecordSerde));

        // create rest client

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://geo.api.gouv.fr")
                .path("communes")
                .queryParam("format", "json")
                .queryParam("fields","codeDepartement,departement");

        records.map(
                new KeyValueMapper<String, MeteoDataRecord, KeyValue<String, MeteoDataRecord>>() {
                    @Override
                    public KeyValue<String, MeteoDataRecord> apply(String key, MeteoDataRecord value) {
                        Commune[] commune = target.queryParam("lat", value.getLat())
                                .queryParam("lon", value.getLon())
                                .request(MediaType.APPLICATION_JSON)
                                .get(Commune[].class);

                        value.setDepartement("unknown");
                        value.setCommune("unknown");
                        key = "unknown";

                        if(commune.length>0) {
                            if(commune[0].getDepartement().getNom() !=null)
                                value.setDepartement(commune[0].getDepartement().getNom());
                            if(commune[0].getNom() !=null)
                                value.setCommune(commune[0].getNom());
                            if(commune[0].getDepartement().getCode() != null)
                                key = commune[0].getDepartement().getCode();
                        }
                        //System.out.println(value);
                        return new KeyValue<String, MeteoDataRecord>(key, value);
                    }
                }).to("aggregated_station_data", Produced.with(Serdes.String(),meteoRecordSerde));

        KGroupedStream<String, MeteoDataRecord> recordsTableStream = builder.stream("aggregated_station_data", Consumed.with(Serdes.String(), meteoRecordSerde)).groupByKey();

        // initialisation de l'objet de stockage des aggregations
        Initializer<MinMax> initializer = new Initializer<MinMax>(){
            @Override
            public MinMax apply() {
                return new MinMax();
            }
        };
        // recuperer min Temperature, minPressure, max temperature, max pressure par departement
        Aggregator< String, MeteoDataRecord, MinMax> aggregator = new Aggregator< String, MeteoDataRecord, MinMax>(){

            @Override
            public MinMax apply(String aggKey, MeteoDataRecord newValue, MinMax aggValue) {
                aggValue.setMinTemp(Math.min(aggValue.getMinTemp(),newValue.getTemperature()));
                aggValue.setMaxTemp(Math.max(aggValue.getMaxTemp(),newValue.getTemperature()));
                aggValue.setMinPres(Math.min(aggValue.getMinPres(),newValue.getPression()));
                aggValue.setMaxPres(Math.max(aggValue.getMaxPres(),newValue.getPression()));
                aggValue.setDepartement(newValue.getDepartement());
                return aggValue;
            }
        };

        KTable<String, MinMax> recordsTable = recordsTableStream.aggregate(initializer,aggregator,minmaxSerde,"min_max_store");
        recordsTable.print();

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
/*
        ReadOnlyKeyValueStore<String, MinMax> keyValueStore =
                streams.store("min_max_store", QueryableStoreTypes.keyValueStore());
        keyValueStore.get()
*/

        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        //streams.close();
    }
}
