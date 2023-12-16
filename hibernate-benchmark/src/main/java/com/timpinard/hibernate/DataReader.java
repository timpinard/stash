package com.timpinard.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import com.timpinard.hibernate.Data;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataReader {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, "jdbc:postgresql://localhost:5432/postgres");
                settings.put(Environment.USER, "tim");
                settings.put(Environment.PASS, "password");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");

                //settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.GENERATE_STATISTICS, "true");

                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                settings.put(Environment.HBM2DDL_AUTO, "create-drop");

                configuration.setProperties(settings);
                configuration.setProperty("ssl", "false");

                configuration.addAnnotatedClass(Data.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }


    /*
    SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
Session session = sessionFactory.openSession();
Transaction transaction = session.beginTransaction();
List<Employee> employees = session.createQuery("FROM Employee", Employee.class).getResultList();
for (Employee employee : employees) {
 System.out.println("ID: " + employee.getId() + ", Name: " + employee.getName());
}
transaction.commit();
session.close();
sessionFactory.close();
     */

    public void save(DataNode dataNode) {
        try (Session session = getSessionFactory().openSession().getSession()) {
            session.beginTransaction();
            save(session, dataNode);
            session.getTransaction().commit();
        }
    }
    public void save(Session session, DataNode dataNode) {
        Data m = dataNode.getParent();
        if (!dataNode.getChildren().isEmpty()) {
            dataNode.getChildren().forEach(d -> save(session, d));
        }
        session.save(m);
    }

    public void delete(DataNode dataNode) {
        try (Session session = getSessionFactory().openSession().getSession()) {
            session.beginTransaction();
            delete(session, dataNode);
            session.getTransaction().commit();
        }
    }
    public void delete(Session session, DataNode dataNode) {
        Data m = dataNode.getParent();
        if (!dataNode.getChildren().isEmpty()) {
            dataNode.getChildren().forEach(d -> session.delete(d.getParent()));
        }
        session.delete(m);
    }

//    public void destroy() {
//        this.session.getTransaction().commit();
//        this.session.close();
//    }


    public DataReader() {

//        try {
//            session = getSessionFactory().openSession().getSession();
//            session.beginTransaction();
//        } catch (Exception e) {
//
//        }
    }

    public DataNode buildTestNode(String id, String parentId, int depth, int leafCount) {
        if (depth > 0 ) {
            List<DataNode> children = IntStream.range(0,leafCount).mapToObj(value -> {
                return buildTestNode(id + value, id, depth -1, leafCount);
            }).collect(Collectors.toList());
            return new DataNode(new Data(id, parentId), children );
        } else {
            return new DataNode(new Data(id, parentId), new ArrayList<>() );
        }
    }

    public DataNode getDataNode(String uid) {
        try (Session session = getSessionFactory().openSession().getSession()) {
            DataNode node = getNode(session, uid);
            return node;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DataNode getNode(Session session, String uid) {

            DataNode node = null;
                Data b = session.get(Data.class, uid);
                node = new DataNode(b);
                String query = "select g from Data g where g.parentId = '" + b.getId() + "'";
                Query<Data> q = session.createQuery(query, Data.class);

                List<Data> c = q.list();
                if (c.isEmpty()) {
//                    return node;
                } else {
                    //recurse
                    for (Data groupDO : c) {
                        node.getChildren().add(this.getNode(session, groupDO.getId()));
                    }

                }

        return node;
    }

    public DataNode clone(DataNode dataNode) {
        //session.evict(dataNode.getParent());
        dataNode.getParent().setId(dataNode.getParent().getId() + ".copy");
        if (!dataNode.getChildren().isEmpty()) {
            dataNode.getChildren().forEach(dataNode1 -> {
                Data m = dataNode1.getParent();
                Data c = (Data) dataNode1.getParent();
                String uid = c.getParentId();
                c.setParentId(uid + ".copy");
                this.clone(dataNode1);
            });
        }
        return dataNode;
    }


}