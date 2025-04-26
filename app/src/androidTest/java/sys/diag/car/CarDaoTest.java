package sys.diag.car;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import sys.diag.car.DB.AppDatabase;
import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.DB.DAO.UserDAO;
import sys.diag.car.DB.Entity.CarEntity;
import sys.diag.car.DB.Entity.UserEntity;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class CarDaoTest {

    private AppDatabase db;
    private CarDAO carDAO;
    private UserDAO userDAO;

    private List<CarEntity> preparedData;
    @Before
    public void init(){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // только для тестов
                .build();
        this.carDAO = db.carDAO();
        this.userDAO = db.userDAO();
        preparedData = new ArrayList<CarEntity>();
        preparedData.add(new CarEntity(1,
                "Toyota",
                "Camry",
                "2009",
                "Engine",
                "path/1/34e.img",
                1));
    }
    @After
    public void closeDb(){
        db.close();
    }
    @Test
    public void insertAndGetCar(){
        UserEntity user= new UserEntity(1,"Samira","samira@gmail.com");
        CarEntity car;
        userDAO.insert(user);
        carDAO.insert(preparedData.get(0));
        car =carDAO.getCarById(1);
        assertEquals("Camry",car.getModelCar());
    }



}