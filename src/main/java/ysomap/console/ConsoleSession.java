package ysomap.console;

import org.jline.reader.EndOfFileException;
import org.reflections.Reflections;
import ysomap.annotation.Bullets;
import ysomap.annotation.Exploits;
import ysomap.annotation.Payloads;
import ysomap.exception.ArgumentsMissMatchException;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author wh1t3P1g
 * @since 2020/2/19
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ConsoleSession {

    public String current;
    public String command;
    public List<String> args;
    public HashMap<String, Map<String, String>> settings;
    public HashMap<String,Session> sessions;
    public List<Session> running;
    public Map<String, String> prompt;// from ConsoleRunner
    public Map<String, Class<?>> bullets;
    public Map<String, Class<?>> payloads;
    public Map<String, Class<?>> exploits;

    public ConsoleSession() {
        args = new LinkedList<>();
        settings = new LinkedHashMap<>();
        settings.put("payload", new LinkedHashMap<>());
        settings.put("exploit", new LinkedHashMap<>());
        settings.put("bullet", new LinkedHashMap<>());
        sessions = new LinkedHashMap<>();
        running = new LinkedList<>();
        Reflections reflections = new Reflections("ysomap.core");
        bullets = loadClass(reflections, Bullets.class);
        payloads = loadClass(reflections, Payloads.class);
        exploits = loadClass(reflections, Exploits.class);
    }

    public void accept(List<String> words, Map<String, String> prompt) throws Exception {
        this.prompt = prompt;
        parse(words);
        switch (command){
            case "use":
                // use payload/exploit/bullet name
                ConsoleHandler.use(this);
                break;
            case "set":
                // set key value, args from exploit or bullet
                ConsoleHandler.set(this);
                break;
            case "list":
                // list exploits, bullets and payloads
                ConsoleHandler.list(this);
                break;
            case "show":
                // show payload/bullet/exploit details
                ConsoleHandler.show(this);
                break;
            case "run":
                // run current payload
                ConsoleHandler.run(this);
                break;
            case "help":
                // help
                ConsoleHandler.help();
                break;
            case "sessions":
                // print current running exploit sessions
                ConsoleHandler.sessions(this);
                break;
            case "kill":
                // kill running exploit sessions
                ConsoleHandler.kill(this);
                break;
            case "clear":
                // clear current sessions
                clearAll();
                break;
            case "":
                // do nothing
                break;
            case "exit":
                // exit ysomap
                throw new EndOfFileException();
            default:
                // unknown command
                throw new ArgumentsMissMatchException("help");
        }
    }

    public void parse(List<String> words){
        int size = words.size();
        if(size == 1){
            command = words.get(0);
        }else if(size > 1){
            command = words.get(0);
            args = new LinkedList<>(words.subList(1, size));
            args.removeIf(String::isEmpty);
        }
    }

    public void clearAll(){
        sessions.clear();
        for(Map.Entry<String, Map<String,String>> item:settings.entrySet()){
            item.getValue().clear();
        }
        prompt.clear();
    }

    public void clear(String type){
        if(settings.containsKey(type)){
            settings.get(type).clear();
        }
        sessions.remove(type);
        prompt.remove(type);
    }

    public void stopAllSessions(){
        for(Session session:running){
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        running.clear();
    }

    public void removeStoppedSessions(){
        List<Session> copy = new LinkedList<>(running);
        for(Session session: copy){
            if(session.isExit()){
                running.remove(session);
            }
        }
    }

    public Map<String, Class<?>> loadClass(Reflections reflections, Class<? extends Annotation> annotation){
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotation);
        Map<String, Class<?>> retMap = new LinkedHashMap<>();
        for(Class<?> clazz: classes){
            retMap.put(clazz.getSimpleName(), clazz);
        }
        return retMap;
    }


}