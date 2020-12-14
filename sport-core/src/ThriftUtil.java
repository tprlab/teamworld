package com.leaguetor;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TBase;


public class ThriftUtil {

    public static <T extends TBase> Map<Integer, T> listToMap(List<T> lst) {
        if (StringUtil.emptyOrNull(lst))
            return null;
        Map<Integer, T> ret = new HashMap<Integer, T>();
        try {
            for (T t : lst) {
                Object val = t.getFieldValue(t.fieldForId(1));
                if (val == null || !(val instanceof Integer))
                    continue;
                ret.put((Integer)val, t);
            }
        } catch(Throwable t) {
            Tracer.err("Cannot get map from " + lst, t);
        }
        return ret;
    }

    public static <T extends TBase> T findList(Collection<T> lst, int id, int fld) {
        if (lst == null)
            return null;
        for (T t : lst) {
            Object val = t.getFieldValue(t.fieldForId(fld));
            if (val == null || !(val instanceof Integer))
                continue;
            Integer ival = (Integer)val;
            if (id == ival.intValue())
                return t;
        }
        return null;
    }


    public static <T extends TBase> T findList(Collection<T> lst, int id) {
        return findList(lst, id, 1);
    }
}