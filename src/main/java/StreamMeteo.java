/**
 * Created by idu on 04/07/2018.
 */
import model.Commune;
import model.MeteoDataRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
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

public class StreamMeteo {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-meteo-data");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");

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
        KStream<String, MeteoDataRecord> records = builder.stream("raw_station_data", Consumed.with(Serdes.String(), meteoRecordSerde));

        // create rest client

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("https://geo.api.gouv.fr")
                .path("communes")
                .queryParam("format", "json")
                .queryParam("fields","codeDepartement,departement");

        records.foreach(new ForeachAction<String, MeteoDataRecord>() {
            @Override
            public void apply(String key, MeteoDataRecord value) {
                System.out.println(key + ": " + value);
                Commune[] commune = target.queryParam("lat", value.getLat())
                        .queryParam("lon", value.getLon())
                        .request(MediaType.APPLICATION_JSON)
                        .get(Commune[].class);
                System.out.println(commune);
            }
        });
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        //streams.close();
    }
}
