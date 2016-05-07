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
    public String prof_name;
    public List<Integer> preference;
    public int cap;
    public List<String> clashes_course;
    public Course(String coursename, String professor_name, String teaching_preference, String capacity, String clash){ //course constructor
        course_name = coursename.toUpperCase();
        prof_name = professor_name;
        cap = Integer.parseInt(capacity);
        preference = new ArrayList();
        clashes_course = new ArrayList();
        for (String s : teaching_preference.split(", ")) {
            preference.add(Integer.parseInt(s));
            }
        Collections.shuffle(preference);
        for (String str : clash.split(",") ){
            clashes_course.add(str.toUpperCase());
            }
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

public class allocator { //ROOT CLASS make it Capslock
    public static final ArrayList<Room> ROOMS = new ArrayList<>();
    public static final ArrayList<String> SLOTS = new ArrayList<>();
    public static final String[] FILE_HEADER_MAPPING = {"Course", "Professor Name", "Teaching Preference", "Capacity", "Clashes"};
    public static final String[] ROOM_MAPPING = {"Serial", "Classroom", "Capacity"};
    public static final String[] SLOT_MAPPING = {"Slot Number", "Duration"};
    public static List courseobj = new ArrayList();
    public static String[][] coursealloc;

    public void parseFile(File file1) throws IOException, NullPointerException, IndexOutOfBoundsException {
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING); // excel file first row
        FileReader fileReader = new FileReader(file1);
        CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat); //second argument
        List csvRecords = csvFileParser.getRecords();
        Course course;

        //start reading the csv inputfile
        for (int i = 1; i < csvRecords.size(); i++) { //goes through each row
            CSVRecord record = (CSVRecord) csvRecords.get(i); //of type CSVRecord
            course = new Course(record.get("Course"), record.get("Professor Name"), record.get("Teaching Preference"),record.get("Capacity"),record.get("Clashes")); //course object
            courseobj.add(course);
            //System.out.println(courseobj);
            //course
        }

        //calling allocate method
        allocate(courseobj);


        //printing each professor object
        Iterator<Course> itr = courseobj.iterator();
        while (itr.hasNext()) {
            Course element = itr.next();
            //System.out.println(element.course_name + " : " + element.prof_name + " : " + element.preference + " : "+ element.cap+" : "+element.clashes_course);
        }
    } //end of parsefile


    public void roomToCapacity(File file2) throws IOException { //Reading File2 (ROOMS.csv)
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(ROOM_MAPPING);
        FileReader fileReader = new FileReader(file2);
        CSVParser csvFileParser1 = new CSVParser(fileReader, csvFileFormat);
        List csvRecords = csvFileParser1.getRecords();
        for (int i = 1; i < csvRecords.size(); i++) { //goes through each row
            CSVRecord record = (CSVRecord) csvRecords.get(i); //of type CSVRecord
            Room room = new Room(record.get("Serial"), record.get("Classroom"), record.get("Capacity"));
            ROOMS.add(room);
        }
        Collections.shuffle(ROOMS);

    }

    public void slotsToTimings(File file3) throws IOException{
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(SLOT_MAPPING);
        FileReader fileReader = new FileReader(file3);
        CSVParser csvFileParser2 = new CSVParser(fileReader, csvFileFormat);
        List csvRecords = csvFileParser2.getRecords();
        for (int i = 1; i < csvRecords.size(); i++) { //goes through each row
            CSVRecord record = (CSVRecord) csvRecords.get(i); //of type CSVRecord
           SLOTS.add(record.get("Duration"));
        }

    }

    /**
     * @param courseobj
     * @return
     * input: courselist <Course>, state of the matrix (few cells maybe assigned and few may not be unallocated)
     * returns true when coursealloc matrix has been correctly assigned with input number of courses
     * returns false: couldn't find an assignment for all input courses. If so, it leaves the state when the function was called
     */

    public boolean allocate(List<Course> courseobj) {
        if (courseobj.size() > 0){
            for (int courseobjlength = 0; courseobjlength < courseobj.size(); courseobjlength++) {
                Course lastcourse = courseobj.get(courseobj.size() - 1); //setting lastcourse to last course in the courseobj list
                for (int currentslot : lastcourse.preference) {
                    //loops through faculty preference
                    //System.out.println(lastcourse.preference.get(preflength));
                    for (int room = 0; room < ROOMS.size(); room++) { //looping through matrix

                        // 1) if the room is occupied at the current time slot or course capacity > room capacity: go to next room

                        if(coursealloc[currentslot-1][room] != null || lastcourse.cap > ROOMS.get(room).cap) {
                              continue;
                          }
                        
                        // 2) check if any clashing course exist in the same slot: chuck the room; go to next room

                              boolean clashes = false;
                              for (int roomcheck = 0; roomcheck < ROOMS.size(); roomcheck++) {
                                if(lastcourse.clashes_course.contains(coursealloc[currentslot-1][roomcheck])) {
                                  clashes = true;
                                }
                              }

                              if (clashes) {
                                  continue;
                              }

                              //3) place the course in the room at that slot

                              coursealloc[currentslot-1][room] = lastcourse.course_name;
                              List<Course> updatedcourseobj = courseobj.subList(0, courseobj.size() - 1);
                              allocate(updatedcourseobj);
                              //System.out.println (coursealloc[currentslot-1][room]);

                              /*4) allocate the remaining courses
                                   a) TRUE: return true
                                   b) FALSE: remove the course allocated in 3. */

                              if (allocate(updatedcourseobj)) {
                                  return true;
                              }

                              else {
                                  coursealloc[currentslot-1][room] = null;
                              }
                    }
                }
            }
        }
        return false;
    }



    public void allocation(String course_name, String professor_name, List<Integer> preference) throws NullPointerException {
        String[] slots = {"Monday,9am-10:30am", "Monday,10:40-12:10pm”, ”Monday,12:20-1:50pm", "Monday,2:35 pm-4:05 pm",
                "Monday,4:15 pm-5:45 pm", "Tuesday,9:00 am-10:30 am", "Tuesday,10:40 am-12:10 pm", "Tuesday,12:20 pm-1:50 pm",
                "Tuesday,2:35 pm-4:05 pm", "Tuesday,4:15 pm-5:45 pm"};


        for (int i = 1; i <= 10; i++) { //filling cells of matrix with none
            for (int j = 1; j <= ROOMS.size(); j++) {
                coursealloc[i - 1][j - 1] = "none";
            }
        }


        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= ROOMS.size(); j++) {

                if (course_name.contains("FC")) { //room cap should be 50(Secondary check)
                    if (ROOMS.get(j - 1).cap == 50) {
                        for (int x = 0; x < preference.size(); x++) {
                            if (preference.get(x) == i) {
                                //System.out.println(preference.get(x));
                                if (coursealloc[i - 1][j - 1] == "none") {
                                    coursealloc[i - 1][j - 1] = course_name;
                                    System.out.println(coursealloc[i - 1][j - 1] + " " + professor_name + " " + slots[i - 1] + " " + ROOMS.get(j - 1).roomname);
                                    return;
                                } else {
                                    for (int k = 0; k < preference.size(); k++) { //running through the same preference list
                                        //allocating the course in the next best preference slot
                                        if (coursealloc[k][j - 1] == "none") {
                                            coursealloc[k][j - 1] = course_name;
                                            System.out.println(coursealloc[k][j - 1] + " " + professor_name + " " + slots[k] + " " + ROOMS.get(j - 1).roomname);
                                            return;
                                        } else {
                                            preference.remove(k);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else { //not an FC course
                    for (int x = 0; x < preference.size(); x++) {
                        if (preference.get(x) == i) {
                            //System.out.println(preference.get(x) + "  " + course_name);
                            if (coursealloc[i - 1][j - 1] == "none") {
                                coursealloc[i - 1][j - 1] = course_name;
                                System.out.println(coursealloc[i - 1][j - 1] + " " + professor_name + " " + slots[i - 1] + " " + ROOMS.get(j - 1).roomname);
                                return;
                            }
                        } else {
                            for (int l = 0; l < preference.size(); l++) {
                                if (coursealloc[l][j - 1] == "none") {
                                    coursealloc[l][j - 1] = course_name;
                                    System.out.println(coursealloc[l][j - 1] + " " + professor_name + " " + slots[l] + " " + ROOMS.get(j - 1).roomname);
                                    return;
                                } else {
                                    preference.remove(l);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, NullPointerException, IndexOutOfBoundsException {
        File inputFile = new File(args[0]);
        File inputFile1 = new File(args[1]);
        File inputFile2 = new File(args[2]);
        allocator start = new allocator();
        start.roomToCapacity(inputFile1);
        start.slotsToTimings(inputFile2);
        coursealloc = new String[SLOTS.size()][ROOMS.size()]; //setting the size of the matrix and making each cell equal to "null"
        for(int i =0; i< SLOTS.size();i++){
            for(int j=0; j<ROOMS.size();j++){
                coursealloc[i][j] = null;
            }
        }
        start.parseFile(inputFile);

    }
}

