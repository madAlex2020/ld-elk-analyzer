package gg.live.data.elk.analyzer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoutableMessage {
    private Object payload;
    private String title;
    private String type;
    private String subject;
    private String action;
    private String liveDataMatchUrn;
}
