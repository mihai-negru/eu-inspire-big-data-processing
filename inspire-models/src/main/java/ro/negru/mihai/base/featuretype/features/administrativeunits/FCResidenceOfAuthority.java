package ro.negru.mihai.base.featuretype.features.administrativeunits;

import ro.negru.mihai.application.schema.administrativeunits.datatype.ResidenceOfAuthority;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.base.featuretype.features.EtsFamily;

public class FCResidenceOfAuthority extends FeatureCollection<ResidenceOfAuthority> {
    @Override
    public String etsFamily() {
        return EtsFamily.ADM_UNITS.toString();
    }
}
