package com.sap.sss.vendorfake.interceptors;

import com.sap.sss.vendorfake.utiilities.CommonUtility;

import java.io.*;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonInterceptor implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

    private static String UTF_8 = "UTF-8";


    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        InputStreamReader streamReader = new InputStreamReader(inputStream, UTF_8);
        try {
            return CommonUtility.getConfiguredGsonInstance().fromJson(streamReader, type);
        } catch ( com.google.gson.JsonSyntaxException e) {
            // Log exception
        } finally {
            streamReader.close();
        }
        return null;
    }

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, UTF_8);

        try {
            CommonUtility.getConfiguredGsonInstance().toJson(o, type, writer);
        } finally {
            writer.close();
        }
    }
}