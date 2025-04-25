package sys.diag.car;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import sys.diag.car.DB.AppDatabase;
import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.DB.Entity.CarEntity;
import sys.diag.car.helpers.LiveDataTestUtil;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CarDaoTest {
    private AppDatabase db;
    private CarDAO carDAO;
    @Before
    public void init(){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // только для тестов
                .build();
        this.carDAO = db.carDAO();
    }
    @After
    public void closeDb(){
        db.close();
    }
    @Test
    public void insertAndGetCar(){
        CarEntity car=new CarEntity(1,"Toyota",
                "Camry","2009",
                "Engine","path/1/34e.img",
                1);
        carDAO.insert(car);
        List<CarEntity> result;
        try {
             result = LiveDataTestUtil.getValue(carDAO.getAllCar());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        assertEquals("Camry",result.get(0).getModelCar());
    }



}