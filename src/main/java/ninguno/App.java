package ninguno;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class App 
{
    public static void main( String[] args ) throws FileNotFoundException, IOException, ParseException
    {
        
        //Path del directorio donde estan los archivos
        String path2 = "C:/Users/WorldEditor/Documents/Python_Scripts/Python_Dataanalysis/datos/matches/";
        //Pasar la direccion en FILE object y hacerlo una lista 
        File f = new File(path2);
        File[] file_list = f.listFiles();
        new WritingFile(file_list);
        
        /*
        JSONArray lista_json = new JSONArray();
        JSONArray lista_json_vacia = new JSONArray();
        JSONObject modelo1 = new JSONObject();
        JSONObject modelo2 = new JSONObject();
        JSONObject modelo3 = new JSONObject();
        modelo1.put("name", "mati");
        modelo1.put("ohaiho", "oasd");
        modelo2.put("name", "asd");
        modelo3.put("name", "223");
        lista_json.add(modelo1);
        lista_json.add(modelo2);
        lista_json.add(modelo3);
        JSONObject dummy;
        for(Object item : lista_json){
            dummy = (JSONObject) item;
            System.out.println(dummy.containsValue("mati"));
        }
        
        //System.out.println(lista_json_vacia.isEmpty());
        //lista_json.remove(modelo2);
        System.out.println(lista_json);
        */
    }
}
