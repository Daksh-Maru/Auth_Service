package org.example.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.example.model.UserInfoDto;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoDto> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, UserInfoDto userInfoDto) {

        byte[] returnValue = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            returnValue = objectMapper.writeValueAsString(userInfoDto).getBytes();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return returnValue;
    }

    @Override
    public void close() {

    }
}
