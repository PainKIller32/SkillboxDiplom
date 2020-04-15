package main.dto;

import java.util.HashMap;

public class ResultWithErrorsDto extends ResultDto {
    public HashMap<String, String> errors;

    public ResultWithErrorsDto(boolean result, HashMap<String, String> errors) {
        super(result);
        this.errors = errors;
    }
}
