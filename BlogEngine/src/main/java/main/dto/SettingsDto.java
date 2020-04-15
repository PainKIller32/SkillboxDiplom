package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingsDto {
    @JsonProperty
    private boolean MULTIUSER_MODE;
    @JsonProperty
    private boolean POST_PREMODERATION;
    @JsonProperty
    private boolean STATISTICS_IS_PUBLIC;

    public boolean getMULTIUSER_MODE() {
        return MULTIUSER_MODE;
    }

    public void setMULTIUSER_MODE(boolean MULTIUSER_MODE) {
        this.MULTIUSER_MODE = MULTIUSER_MODE;
    }

    public boolean getPOST_PREMODERATION() {
        return POST_PREMODERATION;
    }

    public void setPOST_PREMODERATION(boolean POST_PREMODERATION) {
        this.POST_PREMODERATION = POST_PREMODERATION;
    }

    public boolean getSTATISTICS_IS_PUBLIC() {
        return STATISTICS_IS_PUBLIC;
    }

    public void setSTATISTICS_IS_PUBLIC(boolean STATISTICS_IS_PUBLIC) {
        this.STATISTICS_IS_PUBLIC = STATISTICS_IS_PUBLIC;
    }
}