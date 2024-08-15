package com.polaris.fooddelivery.helpers;

import com.polaris.fooddelivery.dto.OutletWithDistance;
import lombok.NonNull;

import java.util.Comparator;

public class GenericHelper {

    @NonNull
    public static Comparator<OutletWithDistance> getComparator() {
        return new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                Integer x1 = ((OutletWithDistance) o1).getTags().size();
                Integer x2 = ((OutletWithDistance) o2).getTags().size();
                if (x1 != x2) return x2.compareTo(x1);

                Double d1 = ((OutletWithDistance) o1).getDistance();
                Double d2 = ((OutletWithDistance) o2).getDistance();
                return d1.compareTo(d2);
            }
        };
    }

}
