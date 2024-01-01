package cloud.computer.backend.Entity;

import java.lang.reflect.Field;
import java.util.Objects;

public class OpenStackEntity {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        // 使用反射获取字段
        Field[] fields = getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object thisValue = field.get(this);
                Object thatValue = field.get(obj);

                if (!Objects.equals(thisValue, thatValue)) {
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public int hashCode() {
        // 使用反射获取字段
        Field[] fields = getClass().getDeclaredFields();

        int result = 1;

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(this);
                result = 31 * result + Objects.hashCode(value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }
}
