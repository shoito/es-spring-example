package io.shoito.elastic;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Set;

@Document(indexName = Female.INDEX, type = Female.TYPE, shards = 1, replicas = 1, refreshInterval = "1s")
@Data
@Builder
public class Female {
    public static final String INDEX = "female";
    public static final String TYPE = "female";

    @Id
    private String userId;

    @Field(type = FieldType.Integer, store = true)
    private Integer bust;

    @Field(type = FieldType.Integer, store = true)
    private Integer prefectureId;

    @Field(type = FieldType.Integer, store = true)
    private List<Integer> hobbies;

    @Field(type = FieldType.text, store = true)
    private Set<String> excludeUsers;

    /* nested
    @Field(type = FieldType.Nested, store = true)
    private Set<ExcludeUser> excludeUsers;

    @AllArgsConstructor
    @Data
    public static class ExcludeUser {
        @Field(type = FieldType.text, store = true)
        private String userId;

        @Field(type = FieldType.text, store = true)
        private Reason reason;
    }
    */
}
