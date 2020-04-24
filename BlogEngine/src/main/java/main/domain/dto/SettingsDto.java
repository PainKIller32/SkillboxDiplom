package main.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SettingsDto {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiUserMode;
    @JsonProperty("POST_PREMODERATION")
    private boolean postPreModeration;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;

    public boolean getMultiUserMode() {
        return multiUserMode;
    }

    public void setMultiUserMode(boolean multiUserMode) {
        this.multiUserMode = multiUserMode;
    }

    public boolean getPostPreModeration() {
        return postPreModeration;
    }

    public void setPostPreModeration(boolean postPreModeration) {
        this.postPreModeration = postPreModeration;
    }

    public boolean getStatisticsIsPublic() {
        return statisticsIsPublic;
    }

    public void setStatisticsIsPublic(boolean statisticsIsPublic) {
        this.statisticsIsPublic = statisticsIsPublic;
    }
}