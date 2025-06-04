package ro.negru.mihai.base.featuretype.features.geographicalnames;

import ro.negru.mihai.application.schema.geographicalnames.datatype.SpellingOfName;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.base.featuretype.features.EtsFamily;

public class FCSpellingOfName extends FeatureCollection<SpellingOfName> {
    @Override
    public String etsFamily() {
        return EtsFamily.GEO_NAMES.toString();
    }
}
