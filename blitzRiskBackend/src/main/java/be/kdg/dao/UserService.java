package be.kdg.dao;

import be.kdg.model.User;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Marlies on 4/01/2015.
 */

@Service("userService")

public class UserService {

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User checkLogin(String username, String password) {
        Query query = sessionFactory.getCurrentSession().createQuery("from User u where u.name = :username and u.password = :password");
        query.setParameter("username", username);
        query.setParameter("password", password);
        User user = (User) query.uniqueResult();
        return user;
    }

    public User checkLoginByEmail(String email, String password) {
        //Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        //Transaction tx = session.beginTransaction();
        Query query = sessionFactory.getCurrentSession().createQuery("from User u where u.email = :email and u.password = :password");
        query.setParameter("email", email);
        query.setParameter("password", password);
        User user = (User) query.uniqueResult();
        //tx.commit();
        return user;
    }

    public void addUser(String username, String password, String email) {
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(password);
        try {
            sessionFactory.getCurrentSession().save(user);
        } catch (ConstraintViolationException e) {
            throw e;
        }
    }

    public User loadUserByUsername(String username) {
        Query query = sessionFactory.getCurrentSession().createQuery("from User where name = :username");
        query.setParameter("username", username);
        return (User) query.uniqueResult();
    }

    public List<User> findall() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
        return criteria.list();
    }

    public void removeUser(String username) {
        Query query = sessionFactory.getCurrentSession().createQuery("from User where name = :username");
        query.setParameter("username", username);
        User user = (User) query.uniqueResult();
        sessionFactory.getCurrentSession().delete(user);
    }

}
