package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

/**
 * Source - https://stackoverflow.com/a/75502641 <br>
 * Posted by Basil Bourque, modified by community. See post 'Timeline' for change history <br>
 * Retrieved 2026-02-03, License - CC BY-SA 4.0 <br>
 * Doc comments provided by Henning. <br>
 * 
 * TypeAdapter for the Instant data type since it cant be natively serialized into json by Gson.
 * @author Basil Bourque
 * @version 2026-02-03
 */
public class Gson_InstantTypeAdapter extends TypeAdapter < Instant >
{
    /**
     * Constructor added to prevent javadoc warning
     */
    public Gson_InstantTypeAdapter() {}
    /** 
     * Serialization step. Turns the Instant into a String with ISO 8601 format
     */
    @Override
    public void write ( JsonWriter jsonWriter , Instant instant ) throws IOException
    {
        jsonWriter.value( instant.toString() );  // Writes in standard ISO 8601 format.
    }

    /** 
     * Deserialization step. Parses the String back into an Instant object.
     */
    @Override
    public Instant read ( JsonReader jsonReader ) throws IOException
    {
        return Instant.parse( jsonReader.nextString() );   // Parses standard ISO 8601 format.
    }
}
