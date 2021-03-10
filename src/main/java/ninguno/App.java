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
        /*
        //Path del directorio donde estan los archivos
        String path2 = "C:/Users/WorldEditor/Documents/Python_Scripts/Python_Dataanalysis/datos/matches/";
        //Pasar la direccion en FILE object y hacerlo una lista 
        File f = new File(path2);
        File[] file_list = f.listFiles();
        //new WritingFile(file_list);
        */
        JSONArray lista_json = new JSONArray();
        JSONArray lista_json_vacia = new JSONArray();
        JSONObject modelo1 = new JSONObject();
        JSONObject modelo2 = new JSONObject();
        modelo1.put("name", "mati");
        modelo2.put("name", "migue");
        lista_json.add(modelo1);
        lista_json.add(modelo2);
        lista_json.forEach(item -> {
            
            System.out.println(modelo1.equals((JSONObject)item));
        });
        System.out.println(lista_json_vacia.isEmpty());
    }
}
