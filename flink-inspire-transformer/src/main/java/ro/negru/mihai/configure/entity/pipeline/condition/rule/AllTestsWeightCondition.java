package ro.negru.mihai.configure.entity.pipeline.condition.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.operator.TestOperator;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class AllTestsWeightCondition implements TestCondition {
    private TestOperator operator;
    private double value;
}
