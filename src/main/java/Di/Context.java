package Di;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Context {
    static Map<String, Class> types = new HashMap<>();
    static Map<String, Object> beans = new HashMap<>();

    static void register(String name, Class<?> type) {
        types.put(name, type);
    }

    static Object getBeans(String name){
        if(beans.containsKey(name)){
            return beans.get(name);
        }else{
            try {
                Class<?> type = types.get(name);
                Objects.requireNonNull(type, name +": not found");
                Object obj = createObject(type);
                beans.put(name, obj);
                return obj;
            }catch (Exception e){
                throw new RuntimeException(name + " can not instanciate", e);
            }
        }
    }
    private static <T> T createObject(Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T object = type.getDeclaredConstructor().newInstance();
        Field[] fields = type.getDeclaredFields();
        // @Inject annotation
        for (Field field : fields) {
            if(!field.isAnnotationPresent(Inject.class)){
                continue;
            }
            field.setAccessible(true);
            field.set(object, getBeans(field.getName()));
        }
        return object;
    }

    public static void autoRegister() {
        Path classPath = Paths.get(Thread.currentThread().getContextClassLoader().getResource("").getPath());

        try(Stream<Path> stream = Files.walk(classPath)){
            stream.filter(p -> !Files.isDirectory(p))
                    .filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classPath.relativize(p))
                    .map(p -> p.toString().replace(File.separatorChar, '.'))
                    .map(n -> n.substring(0, n.length() - 6))
                    .map(n -> {
                        try {
                            return Class.forName(n);
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .filter(c -> c.isAnnotationPresent(Named.class))
                    .forEach(c -> {
                        String className = c.getSimpleName();
                        register(className.substring(0,1).toLowerCase() + className.substring(1),c);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
