package ro.negru.mihai.entity.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MergeCommandGroupedData {
    private String groupId;
    private List<MergeCommandData> data;
}
