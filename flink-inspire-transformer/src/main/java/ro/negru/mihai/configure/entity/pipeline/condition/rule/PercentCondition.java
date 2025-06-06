package ro.negru.mihai.configure.entity.pipeline.condition.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.operator.TestOperator;
import ro.negru.mihai.configure.entity.status.Status;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class PercentCondition implements TestCondition {
    private Status category;
    private TestOperator operator;
    private double value;
}
