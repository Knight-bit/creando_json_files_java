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
        JSONArray data = new JSONArray();
        JSONObject modelo1 = new JSONObject();
        modelo1.put("name", "mati");
        JSONObject modelo2 = new JSONObject();
        modelo2.put("name", "migue");
        data.add(modelo1);
        data.add(modelo2);
        JSONObject referencia;
        String name = "mati";
        for(Object item : data){
            referencia = (JSONObject) item;
            System.out.println(referencia);
            if(referencia.containsValue(name))
                System.out.println("Si, tiene mati");
        }
        */
    }
}
