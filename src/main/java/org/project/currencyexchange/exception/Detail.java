package org.project.currencyexchange.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Detail {
    private List<InvalidFieldInfo> invalidFieldInfos;
    public Detail(List<InvalidFieldInfo> invalidFieldInfos){
        this.invalidFieldInfos = invalidFieldInfos;
    }
}
