package com.example.chillisauce.security;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;

public class GrantedAuthoritySerializer extends JsonSerializer<GrantedAuthority> {
    /* UserDetails 의 직렬화를 수동으로 하기 위해 사용 */
    @Override
    public void serialize(GrantedAuthority grantedAuthority, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(grantedAuthority.getAuthority());
    }
}
