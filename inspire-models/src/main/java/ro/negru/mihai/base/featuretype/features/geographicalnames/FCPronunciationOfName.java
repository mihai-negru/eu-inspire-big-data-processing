package ro.negru.mihai.base.featuretype.features.geographicalnames;

import ro.negru.mihai.application.schema.geographicalnames.datatype.PronunciationOfName;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.base.featuretype.features.EtsFamily;

public class FCPronunciationOfName extends FeatureCollection<PronunciationOfName> {
    @Override
    public String etsFamily() {
        return EtsFamily.GEO_NAMES.toString();
    }
}
