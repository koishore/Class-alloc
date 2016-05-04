import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.File;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.lang.String;

class Course{
    public String course_name;
    public String professor_name;
    public List<Integer> preference;
    public LinkedHashMap course_to_prof;
    public Course(String coursename, Professor prof){
        course_name = coursename;
        professor_name = prof.prof_name;
        preference = prof.teaching_preference;
        course_to_prof = new LinkedHashMap();
        course_to_prof.put(coursename, prof);
        //Professor check = (Professor) course_to_prof.get(coursename); //down check
        //allocation(coursename, Prof.prof_name, Prof.teaching_preference );
        }
    }

class Professor{ //Creates Professors object with object field (attributes) constraints: Professor name, and Teaching Preference
    public String prof_name;
    public List<Integer> teaching_preference;
    public Professor(String professor_name, String preference){
        prof_name = professor_name;
        teaching_preference = new ArrayList();
        for (String s : preference.split(",")) {
            teaching_preference.add(Integer.parseInt(s));
        }
        Collections.shuffle(teaching_preference); //randomizing teaching_preference list

    }
}

class Room{
    public int ser;
    public String roomname;
    public int cap;
    public Room(String serial, String room_name, String capacity ){
        ser = Integer.parseInt(serial);
        roomname = room_name;
        cap = Integer.parseInt(capacity);
    }
}

public class allocator {
    public static final ArrayList<Room> ROOMS = new ArrayList<>();
    public static final String [] FILE_HEADER_MAPPING = {"Course","Professor Name", "Teaching Preference"};
    public static final String [] ROOM_MAPPING = {"Serial", "Classroom", "Capacity"};
    public static String[][] coursealloc = new String[10][10];
    public void parseFile(File file1) throws IOException, NullPointerException, IndexOutOfBoundsException{
         // excel file first row
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        FileReader fileReader = new FileReader(file1);
        CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat); //second argument
        List courseobj = new ArrayList();
        List csvRecords = csvFileParser.getRecords();
        Course course;
        Professor professor;
        //start reading the csv inputfile
       for (int i = 1; i < csvRecords.size(); i++){ //goes through each row
           CSVRecord record = (CSVRecord) csvRecords.get(i); //of type CSVRecord
           //System.out.println(record.get("Professor name") + " " + record.get("Course 1"));
           professor = new Professor(record.get("Professor Name"), record.get("Teaching Preference"));
           course = new Course(record.get("Course"), professor);
           courseobj.add(course);
        }
        //printing each professor object
        Iterator<Course> itr = courseobj.iterator();
        while(itr.hasNext()){
            Course element = itr.next();
            //call the allocation algorithm here
            allocation(element.course_name, element.professor_name, element.preference);

            //printing output
            /* for (int i =1; i<=10; i++ ) {
                for (int j = 1; j <= ROOMS.size(); j++) {
                    System.out.println(coursealloc[i][j] + " " + professor);
                }
            }*/
           // System.out.println(element.course_name); //   use.get() to access particular item of the List
        }
//System.out.println(ROOMS.size());
    }//end of parsefile

    public void roomToCapacity(File file2) throws IOException {
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(ROOM_MAPPING);
        FileReader fileReader = new FileReader(file2);
        CSVParser csvFileParser1 = new CSVParser(fileReader, csvFileFormat);
        List csvRecords = csvFileParser1.getRecords();
        for (int i = 1; i < csvRecords.size(); i++) { //goes through each row
            CSVRecord record = (CSVRecord) csvRecords.get(i); //of type CSVRecord
            Room room = new Room(record.get("Serial"),record.get("Classroom"),record.get("Capacity"));
            ROOMS.add(room);
        }
        System.out.println();

    }

    public void allocation(String course_name, String professor_name, List<Integer> preference) throws NullPointerException{
        String[ ] slots = { "Monday,9am-10:30am", "Monday,10:40-12:10pm”, ”Monday,12:20-1:50pm", "Monday,2:35 pm-4:05 pm",
                "Monday,4:15 pm-5:45 pm", "Tuesday,9:00 am-10:30 am", "Tuesday,10:40 am-12:10 pm", "Tuesday,12:20 pm-1:50 pm",
                "Tuesday,2:35 pm-4:05 pm", "Tuesday,4:15 pm-5:45 pm"};
       // String[][] coursealloc = new String[10][10];
        for (int i =1; i<=10; i++ ){
            for(int j = 1; j<= ROOMS.size(); j++ ){

                if(course_name.contains("FC")){ //room cap should be 50(Secondary check)
                    if(ROOMS.get(j-1).cap == 50) {
                        for (int x = 0; x < preference.size(); x++) {
                            if (preference.get(x) == i) {
                                if(coursealloc[i-1][j-1] == null){
                                    coursealloc[i-1][j-1] = course_name;
                                    System.out.println(coursealloc[i-1][j-1] + " " + professor_name + " " + slots[i-1] + " " + ROOMS.get(j-1).roomname);
                                    return;
                                }
                            }
                        }
                    }
                }

                else{ //not an FC course
                    for (int x = 0; x < preference.size(); x++) {
                        if (preference.get(x) == i) {
                            if(coursealloc[i-1][j-1] == null){
                                coursealloc[i-1][j-1] = course_name;
                                System.out.println(coursealloc[i-1][j-1] + " " + professor_name + " " + slots[i-1] + " " + ROOMS.get(j-1).roomname);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws IOException, NullPointerException, IndexOutOfBoundsException{
        File inputFile = new File(args[0]);
        File inputFile1 = new File(args[1]);
        allocator start = new allocator();
        start.roomToCapacity(inputFile1);
        start.parseFile(inputFile);
    }
}
