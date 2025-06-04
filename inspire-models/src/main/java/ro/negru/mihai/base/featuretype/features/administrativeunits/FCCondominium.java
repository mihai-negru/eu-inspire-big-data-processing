package ro.negru.mihai.base.featuretype.features.administrativeunits;

import ro.negru.mihai.application.schema.administrativeunits.featuretype.Condominium;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.base.featuretype.features.EtsFamily;

public class FCCondominium extends FeatureCollection<Condominium> {
    @Override
    public String etsFamily() {
        return EtsFamily.ADM_UNITS.toString();
    }
}
