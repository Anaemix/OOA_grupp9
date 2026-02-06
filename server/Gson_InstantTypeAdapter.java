// Source - https://stackoverflow.com/a/75502641
// Posted by Basil Bourque, modified by community. See post 'Timeline' for change history
// Retrieved 2026-02-03, License - CC BY-SA 4.0

package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class Gson_InstantTypeAdapter extends TypeAdapter < Instant >
{
    @Override
    public void write ( JsonWriter jsonWriter , Instant instant ) throws IOException
    {
        jsonWriter.value( instant.toString() );  // Writes in standard ISO 8601 format.
    }

    @Override
    public Instant read ( JsonReader jsonReader ) throws IOException
    {
        return Instant.parse( jsonReader.nextString() );   // Parses standard ISO 8601 format.
    }
}
