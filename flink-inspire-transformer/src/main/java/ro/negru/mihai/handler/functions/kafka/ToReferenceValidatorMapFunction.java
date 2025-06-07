package ro.negru.mihai.handler.functions.kafka;

import org.apache.flink.api.common.functions.RichMapFunction;
import ro.negru.mihai.entity.kafka.PostTransformRequest;
import ro.negru.mihai.entity.kafka.ValidatorTestRequest;

public class ToReferenceValidatorMapFunction extends RichMapFunction<PostTransformRequest, ValidatorTestRequest> {
    @Override
    public ValidatorTestRequest map(PostTransformRequest post){
        return new ValidatorTestRequest(post.getId(), post.getEtsFamily(), post.getXml());
    }
}
