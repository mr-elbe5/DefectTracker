package de.elbe5.base.json;

import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;

public class JwtSerializer<T> extends JsonSerializer implements Serializer<T> {

    @Override
    public byte[] serialize(T t) throws SerializationException {
        try {
            return serializeObject(t).getBytes();
        } catch (SerializationException se) {
            throw se;
        } catch (Exception e) {
            String msg = "Unable to serialize object of type " + t.getClass().getName() + " to JSON: " + e.getMessage();
            throw new SerializationException(msg, e);
        }
    }


}
