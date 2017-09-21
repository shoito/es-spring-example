package io.shoito.elastic;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class Male {
    public static final String INDEX = "male";
    public static final String TYPE = "male";

    private String userId;

    private Integer chest;

    private Integer prefectureId;

    private List<Integer> hobbies;

    private Set<String> excludeUsers;

    /* nested
    private Set<ExcludeUser> excludeUsers;

    @AllArgsConstructor
    @Data
    public static class ExcludeUser {
        private String userId;
        private Reason reason;
    }
    */
}
