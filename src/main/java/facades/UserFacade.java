package facades;

import DTO.StatusDTOS.StatusDTO;
import DTO.UserDTOS.CreateUserDTO;
import DTO.UserDTOS.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Role;
import entities.User;
import security.errorhandling.AuthenticationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private UserFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVerifiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password, user.getUserPass())) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public String createUser(String username, String password) {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        EntityManager em = emf.createEntityManager();

        User user = new User(username, password);
        Role userRole;
        try {
            if (em.find(Role.class, "user") != null) {
                em.getTransaction().begin();
                userRole = em.find(Role.class, "user");
                user.addRole(userRole);
                em.persist(user);
                em.getTransaction().commit();
            } else {
                Role newUserRole = new Role("user");
                em.getTransaction().begin();
                em.persist(newUserRole);
                user.addRole(newUserRole);
                em.persist(user);
                em.getTransaction().commit();
            }


        } catch (Exception e) {
            createUserDTO.setStatus("failed");
            createUserDTO.setMessage(username + " already exists!");
            return gson.toJson(createUserDTO);
        } finally {
            em.close();
        }
        createUserDTO.setStatus("success");
        createUserDTO.setMessage(username + " created successfully!");
        return gson.toJson(createUserDTO);
    }

    public StatusDTO deleteUser(String userName) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new StatusDTO("Success", userName + " deleted");
    }

    public StatusDTO updateUser(String userName, String newPassword) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            user.setUserPass(newPassword);
            em.merge(user);
            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return new StatusDTO("Success", "Password changed on " + userName);
    }

    public List<UserDTO> partialUsernameSearch(String userName) {
        EntityManager em = emf.createEntityManager();
        List<User> users;
        List<UserDTO> userDTOS = new ArrayList<>();

        try {
            TypedQuery<User> tq = em.createQuery("select u from User u where u.userName like :username",User.class);
            tq.setParameter("username","%" + userName + "%");
            users = tq.getResultList();
        } finally {
            em.close();
        }

        for (User user: users) {
            userDTOS.add(new UserDTO(user));
        }

        return userDTOS;
    }
}
