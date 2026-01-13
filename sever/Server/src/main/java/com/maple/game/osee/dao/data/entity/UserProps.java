package com.maple.game.osee.dao.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProps {

    private Long userId;
    private Integer propsId;
    private long quantity;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date expirationTime;

    public UserProps(Long userId, Integer propsId, long quantity) {
        this.userId = userId;
        this.propsId = propsId;
        this.quantity = quantity;
    }
}
