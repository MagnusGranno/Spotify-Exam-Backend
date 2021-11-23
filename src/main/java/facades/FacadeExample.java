package facades;


import callables.Parallel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class FacadeExample {

    private static FacadeExample instance;
    private static EntityManagerFactory emf;
    private Gson gson = new Gson();
    
    //Private Constructor to ensure Singleton
    private FacadeExample() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static FacadeExample getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FacadeExample();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    //TODO Remove/Change this before use
    public long getRenameMeCount(){
        EntityManager em = emf.createEntityManager();
        try{
            long renameMeCount = (long)em.createQuery("SELECT COUNT(r) FROM RenameMe r").getSingleResult();
            return renameMeCount;
        }finally{  
            em.close();
        }
    }

   /* public List<List<Object>> getDataFromFiveServers() throws ExecutionException, InterruptedException {

        String[] hosts = {
                "https://api.chucknorris.io/jokes/random",
                "https://www.boredapi.com/api/activity",
                "http://universities.hipolabs.com/search?country=Denmark",
                "https://api.nasa.gov/planetary/apod?api_key=EqvAdUyaT8zI6j8ga19Rff2VR70NfnhLsjBHdlXc",
                "https://catfact.ninja/fact"
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<String>> futures = new ArrayList<>();
        List<String> data = new ArrayList<>();
        List<List<Object>> response = new ArrayList<>();

        for (String s: hosts) {
            Future future = executor.submit(new Parallel(s));
            futures.add(future);
        }

        //Get the results
        for (Future<String> future : futures) {
            String dto = future.get();
            data.add(dto);
        }

        List<Object> chuckJokes = new ArrayList<>();
        chuckJokes.add(gson.fromJson(data.get(0),ChuckJokeJsonDTO.class));
        response.add(chuckJokes);

        List<Object> bored = new ArrayList<>();
        bored.add(gson.fromJson(data.get(1), BoredJsonDTO.class));
        response.add(bored);

        // how to receive a list of object through a DTO object
        Type listType = new TypeToken<ArrayList<UniversityJsonDTO>>(){}.getType();
        List<Object> uni = gson.fromJson(data.get(2), listType);
        response.add(uni);

        List<Object> nasa = new ArrayList<>();
        nasa.add(gson.fromJson(data.get(3), NasaJsonDTO.class));
        response.add(nasa);

        List<Object> catFact = new ArrayList<>();
        catFact.add(gson.fromJson(data.get(4), CatFactJsonDTO.class));
        response.add(catFact);

        return response;
    }*/
}
