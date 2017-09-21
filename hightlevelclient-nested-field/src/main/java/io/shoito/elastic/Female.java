package io.shoito.elastic;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class Female {
    public static final String INDEX = "female";
    public static final String TYPE = "female";

    private String userId;

    private Integer bust;

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
