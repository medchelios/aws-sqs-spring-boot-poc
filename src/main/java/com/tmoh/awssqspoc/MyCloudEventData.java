package com.tmoh.awssqspoc;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonClassDescription("MyCloudEventData")
public class MyCloudEventData {

    private String myData;
    private Integer myCounter;
}
