package ua.edu.sumdu.j2se.obolonsky.tasks;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TaskIO {
    // Binary

    public static void write(@NotNull AbstractTaskList tasks,@NotNull OutputStream out) throws IOException {
        DataOutputStream outStream = new DataOutputStream(out);
        outStream.writeInt(tasks.size());
        for (Task task : tasks) {
            outStream.writeInt(task.getTitle().length());
            outStream.writeChars(task.getTitle());
            outStream.writeBoolean(task.isActive());
            int interval = task.getRepeatInterval();
            outStream.writeInt(interval);
            if (interval > 0) {
                outStream.writeLong(task.getStartTime().toEpochSecond(ZoneOffset.UTC));
                outStream.writeLong(task.getEndTime().toEpochSecond(ZoneOffset.UTC));
            } else {
                outStream.writeLong(task.getTime().toEpochSecond(ZoneOffset.UTC));
            }
        }
        outStream.close();
    }

    public static void read(@NotNull AbstractTaskList tasks,@NotNull InputStream in) throws IOException {
        DataInputStream inStream = new DataInputStream(in);
        int size = inStream.readInt();
        Task task;
        LocalDateTime start;
        LocalDateTime end;
        for (; size > 0; size--) {
            int length = inStream.readInt() * 2;
            byte[] b = new byte[length];
            inStream.readFully(b, 0, length);
            String title = new String(b, 0, length, StandardCharsets.UTF_16);
            boolean active = inStream.readBoolean();
            int interval = inStream.readInt();
            start = LocalDateTime.ofEpochSecond(inStream.readLong(), 0, ZoneOffset.UTC);

            if (interval > 0) {
                end = LocalDateTime.ofEpochSecond(inStream.readLong(), 0, ZoneOffset.UTC);
                task = new Task(title, start, end, interval);
            } else {
                task = new Task(title, start);
            }
            task.setActive(active);
            tasks.add(task);
        }
        inStream.close();
    }

    public static void writeBinary(@NotNull AbstractTaskList tasks,@NotNull File file) throws IOException {
        FileOutputStream fo = new FileOutputStream(file);
        DataOutputStream outStream = new DataOutputStream(fo);
        outStream.writeInt(tasks.size());
        for (Task task : tasks) {
            outStream.writeInt(task.getTitle().length());
            outStream.writeChars(task.getTitle());
            outStream.writeBoolean(task.isActive());
            int interval = task.getRepeatInterval();
            outStream.writeInt(interval);
            if (interval > 0) {
                outStream.writeLong(task.getStartTime().toEpochSecond(ZoneOffset.UTC));
                outStream.writeLong(task.getEndTime().toEpochSecond(ZoneOffset.UTC));
            } else {
                outStream.writeLong(task.getTime().toEpochSecond(ZoneOffset.UTC));
            }
        }
        outStream.close();
        fo.close();
    }

    public static void readBinary(@NotNull AbstractTaskList tasks,@NotNull File file) throws IOException {
        FileInputStream fi = new FileInputStream(file);
        DataInputStream inStream = new DataInputStream(fi);
        int size = inStream.readInt();
        Task task;
        LocalDateTime start;
        LocalDateTime end;
        byte[] b;
        for (; size > 0; size--) {
            int length = inStream.readInt() * 2;
            b = new byte[length];
            inStream.readFully(b, 0, length);
            String title = new String(b, 0, length, StandardCharsets.UTF_16);
            boolean active = inStream.readBoolean();
            int interval = inStream.readInt();
            start = LocalDateTime.ofEpochSecond(inStream.readLong(), 0, ZoneOffset.UTC);

            if (interval > 0) {
                end = LocalDateTime.ofEpochSecond(inStream.readLong(), 0, ZoneOffset.UTC);
                task = new Task(title, start, end, interval);
            } else {
                task = new Task(title, start);
            }
            task.setActive(active);
            tasks.add(task);
        }
        inStream.close();
        fi.close();
    }

    //Text

    public static void write(@NotNull AbstractTaskList tasks, @NotNull Writer out) throws IOException {
        Gson gson = createGson();
        JsonWriter jsonWriter = gson.newJsonWriter(out);

        jsonWriter.beginArray();
        for (Task task: tasks){
            jsonWriter.jsonValue(gson.toJson(task));
        }
        jsonWriter.endArray();
        out.flush();
    }

    public static void read(@NotNull AbstractTaskList tasks, @NotNull Reader in) throws IOException{
        Gson gson = createGson();
        JsonReader jsonReader = gson.newJsonReader(in);

        jsonReader.beginArray();
        while (jsonReader.hasNext()){
            tasks.add(gson.fromJson(jsonReader, Task.class));
        }
        jsonReader.endArray();

    }
    public static void writeText(@NotNull AbstractTaskList tasks,@NotNull File file) throws IOException {
        Gson gson = createGson();
        FileWriter fw = new FileWriter(file);
        JsonWriter jsonWriter = gson.newJsonWriter(fw);

        jsonWriter.beginArray();
        for (Task task: tasks){
            jsonWriter.jsonValue(gson.toJson(task));
        }
        jsonWriter.endArray();
        fw.flush();
        fw.close();

    }

    public static void readText(@NotNull AbstractTaskList tasks,@NotNull File file) throws IOException {
        Gson gson = createGson();
        FileReader fr = new FileReader(file);
        JsonReader jsonReader = gson.newJsonReader(fr);

        jsonReader.beginArray();
        while (jsonReader.hasNext()){
            tasks.add(gson.fromJson(jsonReader, Task.class));
        }
        jsonReader.endArray();
        fr.close();


    }

    private static Gson createGson() {
        JsonSerializer<LocalDateTime> localDateTimeJsonSerializer = new JsonSerializer<LocalDateTime>() {

            @Override
            public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
                return new JsonPrimitive(localDateTime.toEpochSecond(ZoneOffset.UTC));
            }
        };

        JsonDeserializer<LocalDateTime> localDateTimeJsonDeserializer = new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement jsonElement, Type type,
                                             JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDateTime.ofEpochSecond(jsonElement.getAsLong(), 0, ZoneOffset.UTC);
            }
        };

        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, localDateTimeJsonSerializer);
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, localDateTimeJsonDeserializer);

        Gson gson = gsonBuilder.setPrettyPrinting().create();
        return gson;
    }
}
