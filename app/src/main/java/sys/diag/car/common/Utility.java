package sys.diag.car.common;

import android.os.Build;

import java.time.LocalDate;

import sys.diag.car.dto.Result;

public class Utility {
    public static final String GUEST ="quest";
    public static final String NO_IMAGE = "placeholder";
    public static final String NO_PREDICTED="Не диагностирован";
    public static final String OBD_II = "OBDII";
    public static Result<Boolean> validate_input_data(String brand, String model, String yearStr){
        if (brand == null || brand.trim().isEmpty() || !brand.matches("[A-Za-zА-Яа-я0-9\\- ]+")) {
            return Result.error("Ошибка: Некорректная марка автомобиля.",false);
        }

        // Проверка модели
        if (model == null || model.trim().isEmpty() || !model.matches("[A-Za-zА-Яа-я0-9\\- ]+")) {
            return Result.error("Ошибка: Некорректная модель автомобиля.",false);
        }

        int currentYear;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentYear = java.time.LocalDate.now().getYear();
        } else {
            
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            currentYear = calendar.get(java.util.Calendar.YEAR);
        }
        // Проверка года выпуска
        try {
            int year = Integer.parseInt(yearStr);
            if (year < 1886 || year > currentYear) {
                System.out.println( + currentYear);
                return Result.error("Ошибка: Год выпуска должен быть между 1886 и "+currentYear,false);


            }
        } catch (NumberFormatException e) {
            return Result.error("Ошибка: Год выпуска должен быть числом.",false);
        }

        // Все проверки пройдены
        return Result.success(true);

    }
}
