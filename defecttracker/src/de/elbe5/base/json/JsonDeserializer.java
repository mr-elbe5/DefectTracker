package de.elbe5.base.json;

import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonDeserializer {

    public Object deserialize(InputStream in) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[0x4000];
        int len;
        try {
            while ((len = in.read(buffer, 0, 0x4000)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e){
            throw new IOException(e.getMessage());
        }
        return deserialize(outputStream.toByteArray());
    }

    public Object deserialize(byte[] bytes) throws IOException {

        if (bytes==null){
            throw new IOException("JSON byte array cannot be null");
        }
        if (bytes.length == 0) {
            throw new IOException("Invalid JSON: zero length byte array.");
        }
        try {
            String s = new String(bytes, StandardCharsets.UTF_8);
            return new JSONParser().parse(s);
        } catch (Exception e) {
            String msg = "Invalid JSON: " + e.getMessage();
            throw new IOException(msg, e);
        }
    }

}
