import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import java.lang.String;

class Professor{
    public String prof_name = null;
    public String course_1 = null;
    public String course_2 = null;
    public List attributes;
    public Professor(String profname, String course1, String course2, String preference){ //professor object's constructor
        prof_name = profname;
        course_1 = course1;
        course_2 = course2;  //object fields
        attributes = new ArrayList();
        attributes.add(profname);
        attributes.add(course1);
        attributes.add(course2);
        attributes.add(preference);
    }
}

public class allocator {
    public static final String [] FILE_HEADER_MAPPING = {"Professor Name","Course 1","Course 2", "Teaching Preference"};
    private static final String PROF_NAME = "Professor name";

    public void parsefile(File file1) throws IOException, FileNotFoundException{
         // excel file first row
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        FileReader fileReader = new FileReader(file1);
        CSVParser csvFileParser = new CSVParser(fileReader, csvFileFormat); //second argument
        List professors = new ArrayList();
        List csvRecords = csvFileParser.getRecords();
        Professor professor;
        //start reading the csv inputfile
       for (int i = 1; i < csvRecords.size(); i++){ //goes through each row
           CSVRecord record = (CSVRecord) csvRecords.get(i); //of type CSVRecord
           //System.out.println(record.get("Professor name") + " " + record.get("Course 1"));
           professor = new Professor(record.get("Professor Name"),record.get("Course 1"),record.get("Course 2"), record.get("Teaching Preference"));
           professors.add(professor);
        }
        //printing each professor object
        Iterator<Professor> itr = professors.iterator();
        while(itr.hasNext()){
            Professor element = itr.next();
            System.out.println(element.attributes); //use.get() to access particular item of the List
        }
    }//end of parsefile

    public static void main(String[] args) throws IOException{
        File inputFile = new File(args[0]);
        allocator start = new allocator();
        start.parsefile(inputFile);

    }
}
