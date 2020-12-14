package com.leaguetor.db;

import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Set;
import java.util.ArrayList;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.HibernateException; 
import org.hibernate.exception.*; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class HibernateWrapper {

    Log logger = LogFactory.getLog(getClass());
    String mCfgPath;
    SessionFactory mFactory; 

    public void setConf(String cfg) {
        mCfgPath = cfg;
    }

    public void init() throws HibernateException {
        mFactory = new Configuration().configure(mCfgPath).buildSessionFactory();        
    }

    public SessionFactory getFactory() {
        return mFactory;
    }

    public Object get(Class cls, Serializable id) throws HibernateException {
        Session session = null;
        Object ret = null;
        long t0 = System.nanoTime();
        try {
            session = openSes();
            ret = session.get(cls, id);
        } finally {
            if (session != null)
                session.close();
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Got object for " + t0 + " mcsec");
        return ret;
    }


    public List getAll(Class cls, Collection<Integer> ids) throws Exception {
        Session session = null;
        List ret = new ArrayList();
        long t0 = System.nanoTime();
        try {
            session = openSes();
            for (Integer id : ids) {
                Object o = session.get(cls, id);
                if (o != null)
                    ret.add(o);
            }
        } finally {
            if (session != null)
                session.close();
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Got list of " + ret.size() +" for " + t0 + " mcsec");
        return ret;
    }


    public void put(Object o) throws Exception {
        if (o == null)
            return;
        Session session = null;
        Transaction t = null;
        long t0 = System.nanoTime();
        try {
            session = openSes();
            t = session.beginTransaction();
            session.saveOrUpdate(o);
            t.commit();
        } catch (HibernateException e) {
            if (t != null)
                t.rollback();
            throw e;
        } finally {
            if (session != null)
                session.close();
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Put object for " + t0 + " mcsec");
    }

    public void delete(Object o) throws Exception {
        if (o == null)
            return;
        Session session = null;
        Transaction t = null;
        long t0 = System.nanoTime();
        try {
            session = openSes();
            t = session.beginTransaction();
            session.delete(o);
            t.commit();
        } catch (HibernateException e) {
            if (t != null)
                t.rollback();
            throw e;
        } finally {
            if (session != null)
                session.close();
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Deleted  object for " + t0 + " mcsec");

    }


    public void putAll(Collection l) throws Exception {
        if (l == null)
            return;
        Session session = null;
        Transaction t = null;
        logger.debug("Saving " + l.size() + " objects");
        long t0 = System.nanoTime();
        try {
            session = openSes();
            t = session.beginTransaction();
            int idx = 0;
            for (Object o : l) {
                session.saveOrUpdate(o);
                if (++idx % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            t.commit();
        } catch (HibernateException e) {
            if (t != null)
                t.rollback();
            throw e;
        } finally {
            if (session != null)
                session.close();
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Put " + l.size() + " objects for " + t0 + " mcsec");
    }


    public Query prepareQuery(Session session, String qname, Map<String, Object> params) throws HibernateException {
        Query q = session.getNamedQuery(qname);
        return substituteParams(q, params, qname);
    }

    private Query substituteParams(Query query, Map<String, Object> params, String qname) {
        if (params == null)
            return query;
        Set<Map.Entry<String, Object> > vals = params.entrySet();
        for (Map.Entry<String, Object> kv : vals) {
            logger.debug("Bind param [" + kv.getKey() + "] = " + kv.getValue() + " for query <" + qname + ">");
            if (kv.getValue() instanceof Collection) {
                query.setParameterList(kv.getKey(), (Collection) kv.getValue());
            } else {
                query.setParameter(kv.getKey(), kv.getValue());
            }
        }
        return query;
    }

    public Session openSes() {
        long t0 = System.nanoTime();
        Session session = mFactory.openSession();
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Opened session for " + t0 + " mcsec");
        return session;
    }


    public Object executeNamedSelectOne(String qname, Map<String, Object> params) throws HibernateException {
        Session session = openSes();
        long t0 = System.nanoTime();
        Object ret = null;

        try {
            ret = prepareQuery(session, qname, params).uniqueResult();
        } finally {
            session.close(); 
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Executed single select " + qname + " for " + t0 + " mcsec");
        return ret;
    }

    public List executeSelect(String squery, Map<String, Object> params, int start_position, int page_size) throws Exception {
        Session session = openSes();
        long t0 = System.nanoTime();
        List ret = null;
        try {
            Query query = session.createQuery(squery);
            ret = substituteParams(query, params, squery)
                .setFirstResult(start_position)
                .setMaxResults(page_size).list();
        } catch (HibernateException e) {
            logger.error("executeQuery" + squery, e);
            throw e;
        } finally {
            session.close();
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Executed query " + squery + " for " + t0 + " mcsec");
        return ret;
    }

    public int executeQuery(String qname, Map<String, Object> params) throws HibernateException {
        Session session = openSes();
        Transaction t = null;
        long t0 = System.nanoTime();
        int ret = 0;
        try {
            t = session.beginTransaction();
            ret = prepareQuery(session, qname, params).executeUpdate();
            t.commit();
        } catch(HibernateException e) {
            if (t != null)
                t.rollback();
            throw e;
        } finally {
            session.close(); 
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Executed query " + qname + " for " + t0 + " mcsec");
        return ret;
    }

    public void executeQueryAll(String qname, Collection<Map<String, Object>> cparams) throws Exception {
        long t0 = System.nanoTime();
        Session session = openSes();
        Transaction t = null;
        try {
            t = session.beginTransaction();
            for (Map<String, Object> params : cparams) {
                prepareQuery(session, qname, params).executeUpdate();
            }
            t.commit();
        } catch(HibernateException e) {
            if (t != null)
                t.rollback();
            throw e;
        } finally {
            session.close(); 
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Executed queries " + qname + " of " + cparams.size() + " for " + t0 + " mcsec");
    }



    public List executeNamedSelect(String qname, Map<String, Object> params) throws HibernateException {
        Session session = openSes();
        long t0 = System.nanoTime();
        List ret = null;
        try {
            ret = prepareQuery(session, qname, params).list();
        } finally {
            session.close(); 
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Executed select " + qname + " for " + t0 + " mcsec");
        return ret;
    }

    public List executeNamedSelect(String qname, Map<String, Object> params, int page_num, int page_size) throws HibernateException {
        Session session = openSes();
        long t0 = System.nanoTime();
        List ret = null;
        try {
            ret = prepareQuery(session, qname, params).setFirstResult(page_num * page_size).
                setMaxResults(page_size).list();
        } finally {
            session.close(); 
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Executed select " + qname + " for " + t0 + " mcsec");
        return ret;
    }



    public Map<String, Object> createParams(Object ...args) throws IllegalArgumentException {
        int idx = 0;
        String key = null;
        Map<String, Object> ret = new HashMap<String, Object>();
        for (Object o : args) {
            if (idx % 2 == 0) {
                if (!(o instanceof String))
                    throw new IllegalArgumentException("Non-String argument: " + idx);
                key = (String)o;
            } else {
                ret.put(key, o);
            }
            idx++;
        } 
        return ret;
    }


    public void deleteAll(List l) throws HibernateException {
        if (l == null)
            return;
        Session session = null;
        Transaction t = null;
        long t0 = System.nanoTime();
        try {
            session = openSes();
            t = session.beginTransaction();
            for (Object o : l) {
                session.delete(o);
            }
            t.commit();
        } catch (HibernateException e) {
            if (t != null)
                t.rollback();
            throw e;
        } finally {
            if (session != null)
                session.close();
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("Executed mass delete of " + l.size() + " for " + t0 + " mcsec");
    }

    public List getAll(Class cls) throws HibernateException {
        return getAll(cls, 0, 100, null, true);
    }

    public List getAll(Class cls, int start_position, int page_size) throws HibernateException {
        return getAll(cls, start_position, page_size, null, true);
    }

    public List getAll(Class cls, int start_position, int page_size, String order, boolean direction) throws HibernateException {
        return getAll(cls, start_position, page_size, null, order, direction);
    }

    public List getAll(Class cls, int start_position, int page_size, Map<String, Object> filter, String order, boolean direction) throws HibernateException {
        List<String> olist = order == null ? null : createList(order);
        
        return getAll(cls, start_position, page_size, filter, olist, createList(new Boolean(direction)));
    }

    public List getAll(Class cls, int start_position, int page_size, Map<String, Object> filter, List<String> order, List<Boolean> direction) throws HibernateException {
        Session session = openSes();
        long t0 = System.nanoTime();
        List ret = null;
        try {
            Criteria criteria = session.createCriteria(cls);
            if (order != null) {
                for (int i = 0; i < order.size(); i++) {    
                    String col = order.get(i);
                    logger.debug("Add order " + col + " direction " + direction.get(i));
                    Order corder = direction.get(i) ? Order.asc(col) : Order.desc(col);
                    criteria.addOrder(corder);
                }
            }
            if (filter != null)
                criteria.add(Restrictions.allEq(filter));

            criteria.setFirstResult(start_position).setMaxResults(page_size);
            
            ret = criteria.list();
        } finally {
            session.close(); 
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("gotAll for " + t0 + " mcsec, size " + (ret == null ? 0 : ret.size()));
        return ret;
    }

    public Object getOne(Class cls, Map<String, Object> filter) throws HibernateException {
        return getOne(cls, filter, null);
    }

    public Object getOne(Class cls, Map<String, Object> filter, List<String> enfilter) throws HibernateException {
        Session session = openSes();
        if (enfilter != null) {
            for(String f : enfilter)
                session.enableFilter(f);
        }

        long t0 = System.nanoTime();
        Object ret = null;
        try {
            Criteria criteria = session.createCriteria(cls);
            criteria.add(Restrictions.allEq(filter));
            ret = criteria.uniqueResult();
        } finally {
            session.close(); 
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("gotOne for " + t0 + " mcsec");
        return ret;
    }


    static<T> List<T> createList(T ...args) {
        ArrayList<T> ret = new ArrayList<T>();
        if (args == null)
            return ret;
        for (T a : args)
            ret.add(a);
        return ret;

    }
/*
    public List getByIds(Class cls, List<Integer> ids) throws HibernateException {
       Session session = openSes();
        long t0 = System.nanoTime();
        List ret = null;
        try {
            Criteria criteria = session.createCriteria(cls);
            Object[] vals = ids.toArray();
            criteria.add(Restrictions.in("id", vals));
            ret = criteria.list();
        } finally {
            session.close(); 
        }
        t0 = (System.nanoTime() - t0) / 1000;
        logger.debug("gotByIds " + ids.size() + " for " + t0 + " mcsec, size " + (ret == null ? 0 : ret.size()));
        return ret;
    }
*/

/*
    List<String> createList(String ...args) {
        ArrayList<String> ret = new ArrayList<String>();
        if (args == null)
            return ret;
        for (String a : args)
            ret.add(a);
        return ret;
    }
*/


}

