package de.elbe5.base.json;

import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Strings;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JsonDeserializer implements Deserializer {

    public Object deserialize(InputStream in) throws DeserializationException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[0x4000];
        int len;
        try {
            while ((len = in.read(buffer, 0, 0x4000)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e){
            throw new DeserializationException(e.getMessage());
        }
        return deserialize(outputStream.toByteArray());
    }

    @Override
    public Object deserialize(byte[] bytes) throws DeserializationException {

        Assert.notNull(bytes, "JSON byte array cannot be null");

        if (bytes.length == 0) {
            throw new DeserializationException("Invalid JSON: zero length byte array.");
        }

        try {
            String s = new String(bytes, Strings.UTF_8);
            return new JSONParser().parse(s);
        } catch (Exception e) {
            String msg = "Invalid JSON: " + e.getMessage();
            throw new DeserializationException(msg, e);
        }
    }

}
