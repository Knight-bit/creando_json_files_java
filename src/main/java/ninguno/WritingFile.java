/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninguno;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WritingFile {
    JSONParser json_parser;
    JSONObject chicos_id;
    JSONObject heroes_id;
    String chicos_id_path;
    String heroes_id_path;
    String chicos_path;
    JSONObject chicos_list_name;
    Scanner sc;
    File[] files;
    public WritingFile(File[] files){
        this.json_parser = new JSONParser();
        this.chicos_list_name = new JSONObject();
        this.chicos_path = "C:/Users/WorldEditor/Documents/Python_Scripts/Python_Dataanalysis/datos/chicos/";
        this.chicos_id_path = "C:/Users/WorldEditor/Documents/Python_Scripts/Python_Dataanalysis/datos/chicos/chicos_id.json";
        this.heroes_id_path = "C:\\Users\\WorldEditor\\Documents\\Python_Scripts\\Python_Dataanalysis\\datos\\heroes_id\\heroes_id.json";
        this.files = files;
        this.sc = new Scanner(System.in);
        getIds();
        writeFile(files);
    }
    public void getIds(){
        try{
            
            File chico_file = new File(chicos_id_path);
            File heroe_file = new File(heroes_id_path);
            FileReader chico_reader = new FileReader(chico_file);
            this.chicos_id = (JSONObject) json_parser.parse(chico_reader);
            chico_reader.close();
            FileReader heroe_reader = new FileReader(heroe_file);
            this.heroes_id = (JSONObject) json_parser.parse(heroe_reader);
            heroe_reader.close();      
        }catch(FileNotFoundException e){
            System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }catch(ParseException e){
            System.out.println(e);
        }
        
    }
    private void writeFile(File[] files){
       int length = files.length;
       int counter = 0;
       for(File file: files){
           try{
               FileReader reader = new FileReader(file);
               JSONObject obj = (JSONObject) json_parser.parse(reader);
               JSONObject result = (JSONObject) obj.get("result");
               JSONArray players = (JSONArray) result.get("players");
               JSONArray store_friends = new JSONArray();
               //checkeamos si la partida es ap o ranked ap, sino, no continues
               long game_mode = (long) result.get("game_mode");
               if(game_mode == 1 || game_mode == 22){
                   for(Object item : players){
                       JSONObject player = getAmigos((JSONObject)item);
                       if(player == null){
                           continue;
                       }else{
                           store_friends.add(player);
                       }
                   }
               }
               
               if(!store_friends.isEmpty()){
                   JSONObject chico_obj;
                   JSONObject match;
                   JSONArray copia_lista_chicos;
                   for(Object item : store_friends){
                       copia_lista_chicos =(JSONArray) store_friends.clone();
                       //Tengo que remover el objecto que se va  escribir para que no lo escriba como amigo
                       copia_lista_chicos.remove((JSONObject) item);
                       chico_obj = (JSONObject) item;
                       String name = (String) chico_obj.get("name");
                       match = (JSONObject) chico_obj.get("data"); 
                       readAndWriteMatch(match, result, name, copia_lista_chicos);
                       copia_lista_chicos.clear();
                   }
               }
               //Despeja ambas variables para futuro uso
               obj.clear();
               store_friends.clear();
               //sc.next();
               //Contador para fijarse cuando falta
               counter ++;
               System.out.println(counter + " de " + length +  " archivos");
           }catch(FileNotFoundException e){
               //No hacer nada si no existe
               continue;
           } catch (IOException ex) {
               Logger.getLogger(WritingFile.class.getName()).log(Level.SEVERE, null, ex);
           } catch (ParseException ex) {
               Logger.getLogger(WritingFile.class.getName()).log(Level.SEVERE, null, ex);
           }
       }

    }
     //Metodo para filtrar amigos de desconocidos y meterlos en el JSONArray
    private JSONObject getAmigos(JSONObject item){
        
        String player_id = String.valueOf(item.get("account_id"));
        if(chicos_id.containsKey(player_id)){
            String name = (String)chicos_id.get(player_id);
            JSONObject chico = new JSONObject();
            chico.put("data", item);
            chico.put("name", name);
            return chico;
        }
        return null;
        
    }
    
    private void  readAndWriteMatch(JSONObject player, JSONObject result, String name_chico,
            JSONArray amigos){
        //Si el id es igual al de mi amigos, entonces guarda los datos
        String name = name_chico;
        /*
            if(this.chicos_id.containsKey(player_id)){
        */
            //Chequear si conecto a la partida, sino no ponga los datos
            long leaver_status =(long) player.get("leaver_status");
            if(leaver_status >= 5){
                return;
            }
            
            FileReader reader = getFile(name);
            //Leemos el archivo de los chicos para escribirlo
            try {
                //Quiero el objecto de los chicos y jsonarray del heroe
                JSONObject chico_profile =(JSONObject)this.json_parser.parse(reader);
                JSONArray heroe = (JSONArray) chico_profile.get("heroes");
                //Consigue el name del heroe que se jugo ese game
                String heroe_id = String.valueOf(player.get("hero_id"));
                System.out.println(heroe_id);
                JSONObject heroe_objecto = (JSONObject) heroes_id.get(heroe_id);
                String heroe_name = (String) heroe_objecto.get("name");
                String heroe_localized_name =(String) heroe_objecto.get("localized_name");
                
                
                //Declaramos aca el modelo que usaremos de referencia
                JSONObject modelo = null;
                long match_id =(long) result.get("match_id");
                //Nos fijamos si esta vacia la lista de heroes
                if(heroe.isEmpty()){
                    //Creamos el modelo y lo agregamos al JSONArray
                    modelo = getFirstHeroe(name ,heroe_localized_name);
                    heroe.add(modelo);
                }
                else{
                    //Hacemos la condicion para recorer la lista y 
                    //pedir el objecto correcto del heroe
                    JSONObject item_dummy;
                    String name_item;
                    boolean exist = false;
                    for(Object item : heroe){
                        item_dummy = (JSONObject) item;
                        name_item = (String) item_dummy.get("name");
                        //Objecto existe y le pasamos la referencia a modelo
                        //Cortamos el loop
                        if(name_item.equals(heroe_name)){
                            modelo = item_dummy;
                            exist = true;
                            break;
                        }
                    }
                    //El heroe no existe en el jsonarray y debe crearse
                    if(exist == false){
                        //Creamos el modelo y lo agregamos al JSONArray
                        modelo = getFirstHeroe(name ,heroe_localized_name);
                        heroe.add(modelo);
                    }
                    //Compruebo si el match ya esta archivado
                    else{
                        ArrayList matches_played = (ArrayList) modelo.get("match_id");
                        if(matches_played.contains(match_id)){
                            return;
                        }
                    }
                }
                /*
                else if(heroe.containsKey(heroe_name)){
                    //Pedimos el modelo desde el archivo
                    modelo =(JSONObject) heroe.get(heroe_name);
                    //Comprobar si la partida esa ya esta guardada
                    match_id =(long) result.get("match_id");
                    ArrayList matches_played =(ArrayList) modelo.get("match_id");
                    if(matches_played.contains(match_id)){
                        return;
                    } 
                }else{
                    //Invocamos la funcion para crearlo ya que no existe el modelo
                    modelo = getFirstHeroe(heroe_localized_name);
                    heroe.put(heroe_name, modelo);
                    match_id =(long) result.get("match_id");
                    
                }
                */
                //Modelo contiene el JSONObject del heroe a escribir
                
                //Todos los datos que queremos guardar para el archivo
                long kills, deaths, assists, level, dummy;
                long item_0, item_1,item_2,item_3,item_4, 
                      item_5, backpack_0, backpack_1, backpack_2,
                      item_neutral, last_hits, denies, gold_per_min, exp_per_min;
                boolean radiant_win;
                level = (long) player.get("level");
                
                kills = (long) player.get("kills");
                deaths = (long) player.get("deaths");
                assists = (long) player.get("assists");
                
                item_0 = (long) player.get("item_0");
                item_1 = (long) player.get("item_1");
                item_2 = (long) player.get("item_2");
                item_3 = (long) player.get("item_3");
                item_4 = (long) player.get("item_4");
                item_5 = (long) player.get("item_5");
                backpack_0 = (long) player.get("backpack_0");
                backpack_1 = (long) player.get("backpack_1");
                backpack_2 = (long) player.get("backpack_2");
                item_neutral = (long) player.get("item_neutral");
                
                last_hits = (long) player.get("last_hits");
                denies = (long) player.get("denies");
                gold_per_min = (long) player.get("gold_per_min");
                exp_per_min = (long) player.get("xp_per_min");
                radiant_win =(boolean) result.get("radiant_win");

                //Ahora a escribir el modelo;
                ArrayList modelo_list_copy;
                modelo.put("total_matches", (long) modelo.get("total_matches") + 1);
                modelo_list_copy = (ArrayList) modelo.get("match_id");
                modelo_list_copy.add(match_id);
                modelo.put("match_id", modelo_list_copy);
                modelo_list_copy = (ArrayList) chico_profile.get("match_id");
                modelo_list_copy.add(match_id);
                chico_profile.put("match_id", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("kills");
                modelo_list_copy.add(kills);
                modelo.put("kills", modelo_list_copy);
                dummy = (long) chico_profile.get("kills");
                chico_profile.put("kills", dummy  + kills);
                
                modelo_list_copy = (ArrayList) modelo.get("assists");
                modelo_list_copy.add(assists);
                modelo.put("assists", modelo_list_copy);
                dummy = (long) chico_profile.get("assists");
                chico_profile.put("assists", dummy + assists);
                
                modelo_list_copy = (ArrayList) modelo.get("denies");
                modelo_list_copy.add(denies);
                modelo.put("denies", modelo_list_copy);
                dummy = (long) chico_profile.get("denies");
                chico_profile.put("denies", dummy + denies);
                
                modelo_list_copy = (ArrayList) modelo.get("deaths");
                modelo_list_copy.add(deaths);
                modelo.put("deaths", modelo_list_copy);
                dummy = (long) chico_profile.get("deaths");
                chico_profile.put("deaths", dummy + deaths);
                
                modelo_list_copy = (ArrayList) modelo.get("last_hits");
                modelo_list_copy.add(last_hits);
                modelo.put("last_hits", modelo_list_copy );
                dummy = (long) chico_profile.get("last_hits");
                chico_profile.put("last_hits", dummy + last_hits);
                
                modelo_list_copy = (ArrayList) modelo.get("item_neutral");
                modelo_list_copy.add(item_neutral);
                modelo.put("item_neutral", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("backpack_0");
                modelo_list_copy.add(backpack_0);
                modelo.put("backpack_0", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("backpack_1");
                modelo_list_copy.add(backpack_1);
                modelo.put("backpack_1", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("backpack_2");
                modelo_list_copy.add(backpack_2);
                modelo.put("backpack_2", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("item_0");
                modelo_list_copy.add(item_0);
                modelo.put("item_0", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("item_1");
                modelo_list_copy.add(item_1);
                modelo.put("item_1", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("item_2");
                modelo_list_copy.add(item_2);
                modelo.put("item_2", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("item_3");
                modelo_list_copy.add(item_3);
                modelo.put("item_3", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("item_4");
                modelo_list_copy.add(item_4);
                modelo.put("item_4", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("item_5");
                modelo_list_copy.add(item_5);
                modelo.put("item_5", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("xp_per_min");
                modelo_list_copy.add(exp_per_min);
                modelo.put("exp_per_min", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("gold_per_min");
                modelo_list_copy.add(gold_per_min);
                modelo.put("gold_per_min", modelo_list_copy);
                
                modelo_list_copy = (ArrayList) modelo.get("level");
                modelo_list_copy.add(level);
                modelo.put("level", modelo_list_copy);
                
                chico_profile.put("total_matches",(long) chico_profile.get("total_matches") + 1);
                modelo.put("total_matches", (long) modelo.get("total_matches") + 1);
                if(leaver_status > 1 && leaver_status < 5){
                    //ABandono la partida, entonces perdio y sumamos el leave
                    chico_profile.put("loses", (long) chico_profile.get("loses") + 1);
                    chico_profile.put("leaves", (long) chico_profile.get("leaves") + 1);
                    modelo.put("loses", (long) modelo.get("loses") + 1);
                    modelo.put("leaves", (long) modelo.get("leaves") + 1);
                    chico_profile = amigoDerrota(chico_profile, modelo, amigos);
                }
                //Gano radiant y el jugador es de radiant
                else if(radiant_win &&(long) player.get("player_slot") < 100){
                    chico_profile.put("wins", (long) chico_profile.get("wins") + 1);
                    modelo.put("wins", (long) modelo.get("wins") + 1);
                    chico_profile = amigoVictoria(chico_profile, modelo, amigos);
                //Gano radiant y el jugador es dire
                }else if(radiant_win && (long) player.get("player_slot") > 100){
                    chico_profile.put("loses", (long) chico_profile.get("loses") + 1);
                    modelo.put("loses", (long) modelo.get("loses") + 1);
                    chico_profile = amigoDerrota(chico_profile, modelo, amigos);
                }//Perdio radiant y el jugador es de radiant
                else if(!radiant_win && (long) player.get("player_slot") < 100){
                    chico_profile.put("loses", (long) chico_profile.get("loses") + 1);
                    modelo.put("loses", (long) modelo.get("loses") + 1);
                    chico_profile = amigoDerrota(chico_profile, modelo, amigos);
                }else{
                    chico_profile.put("wins", (long) chico_profile.get("wins") + 1);
                    modelo.put("wins", (long) modelo.get("wins") + 1);
                    chico_profile = amigoVictoria(chico_profile, modelo, amigos);
                }
                writeJson(chico_profile, name);
                System.out.println("Archivo de " + name + " escrito con exito");
                
            } catch (IOException ex) {
                System.out.println(ex);
            } catch (ParseException ex) {
                System.out.println(ex);
            } catch(NullPointerException ex){
                ex.printStackTrace();
            }
        /*    
        }
        */
        
    }
    //Funciones para escribir el json del amigo y modificar las victorias y derrotas
    private JSONObject amigoVictoria(JSONObject modelo_principal ,JSONObject modelo_match,
            JSONArray amigos){
        
        if(!amigos.isEmpty()){
            JSONArray modelo =(JSONArray) modelo_principal.get("friends");
            JSONArray modelo_partida =(JSONArray) modelo_match.get("friends");
            JSONObject dummy;
            JSONObject dummy2;
            long value;
            String amigo_name;
            boolean check = false;
            for(Object amigo: amigos){
                //Primero escribo el principal
                JSONObject referencia_principal;
                dummy = (JSONObject) amigo;
                amigo_name = (String) dummy.get("name");
                for(Object item : modelo){
                    //Referencia al modelo principal
                    referencia_principal = (JSONObject) item;
                    if(referencia_principal.containsValue(amigo_name)){
                        value =(long) referencia_principal.get("wins");
                        referencia_principal.put("wins", value);
                        value = (long) referencia_principal.get("total_matches");
                        referencia_principal.put("total_matches",value); 
                        check = true;
                        break;
                    }
                }
                
                if(!check){
                    dummy2 =(JSONObject) dummy.get("data");
                    long amigo_id =(long) dummy2.get("account_id");
                    JSONObject data = getFriend(amigo_name, amigo_id);
                    data.put("wins",(long) data.get("wins") + 1);
                    data.put("total_matches",(long) data.get("total_matches") + 1);
                    modelo.add(data);
                    check = false;
                }
                for(Object item : modelo_partida){
                    //Referencia al modelo principal
                    referencia_principal = (JSONObject) item;
                    if(referencia_principal.containsValue(amigo_name)){
                        value =(long) referencia_principal.get("wins");
                        referencia_principal.put("wins", value);
                        value = (long) referencia_principal.get("total_matches");
                        referencia_principal.put("total_matches",value); 
                        check = true;
                        break;
                    }
                }
                if(!check){
                    dummy2 =(JSONObject) dummy.get("data");
                    long amigo_id =(long) dummy2.get("account_id");
                    JSONObject data = getFriend(amigo_name, amigo_id);
                    data.put("wins",(long) data.get("wins") + 1);
                    data.put("total_matches",(long) data.get("total_matches") + 1);
                    modelo_partida.add(data);
                    check = false;
                }
            }
            return modelo_principal;
        } else{
            return modelo_principal;
        }
    }
    
    private JSONObject amigoDerrota(JSONObject modelo_principal ,JSONObject modelo_match,
            JSONArray amigos){
        
        if(!amigos.isEmpty()){
            JSONArray modelo =(JSONArray) modelo_principal.get("friends");
            JSONArray modelo_partida =(JSONArray) modelo_match.get("friends");
            JSONObject dummy;
            JSONObject dummy2;
            long value;
            String amigo_name;
            boolean check = false;
            for(Object amigo: amigos){
                //Primero escribo el principal
                JSONObject referencia_principal;
                dummy = (JSONObject) amigo;
                amigo_name = (String) dummy.get("name");
                for(Object item : modelo){
                    //Referencia al modelo principal
                    referencia_principal = (JSONObject) item;
                    if(referencia_principal.containsValue(amigo_name)){
                        value =(long) referencia_principal.get("loses");
                        referencia_principal.put("loses", value);
                        value = (long) referencia_principal.get("total_matches");
                        referencia_principal.put("total_matches",value); 
                        check = true;
                        break;
                    }
                }
                
                if(!check){
                    dummy2 =(JSONObject) dummy.get("data");
                    long amigo_id =(long) dummy2.get("account_id");
                    JSONObject data = getFriend(amigo_name, amigo_id);
                    data.put("loses",(long) data.get("loses") + 1);
                    data.put("total_matches",(long) data.get("total_matches") + 1);
                    modelo.add(data);
                    check = false;
                }
                for(Object item : modelo_partida){
                    //Referencia al modelo principal
                    referencia_principal = (JSONObject) item;
                    if(referencia_principal.containsValue(amigo_name)){
                        value =(long) referencia_principal.get("loses");
                        referencia_principal.put("loses", value);
                        value = (long) referencia_principal.get("total_matches");
                        referencia_principal.put("total_matches",value); 
                        check = true;
                        break;
                    }
                }
                if(!check){
                    dummy2 =(JSONObject) dummy.get("data");
                    long amigo_id =(long) dummy2.get("account_id");
                    JSONObject data = getFriend(amigo_name, amigo_id);
                    data.put("loses",(long) data.get("loses") + 1);
                    data.put("total_matches",(long) data.get("total_matches") + 1);
                    modelo_partida.add(data);
                    check = false;
                }
            }
            return modelo_principal;
        } else{
            return modelo_principal;
        }
    }
    

    //Funciones para escribir el json del amigo y modificar las victorias y derrotas end
    
    private void writeJson(JSONObject chico_profile,String chico_name){
        try {
            FileWriter write_json = new FileWriter(chicos_path + chico_name + ".json");
            write_json.write(chico_profile.toJSONString());
            write_json.flush();
        } catch (IOException ex) {
            Logger.getLogger(WritingFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private JSONObject getFriend(String name, long account_id){
        long _0 = 0;
        JSONObject modelo = new JSONObject();
        modelo.put("name", name);
        modelo.put("account_id", account_id);
        modelo.put("total_matches", _0);
        modelo.put("wins", _0);
        modelo.put("loses", _0);
        return modelo;
        
    }
    private JSONObject getFirstHeroe(String name, String heroe_localized_name){
        JSONObject modelo = new JSONObject();
        long number = 0;
        modelo.put("name", name);
        modelo.put("name_localized", heroe_localized_name);
        modelo.put("loses", number);
        modelo.put("wins", number);
        modelo.put("total_matches", number);
        modelo.put("leaves", number);
        modelo.put("match_id", new ArrayList<Long>());
        modelo.put("kills", new ArrayList<Long>());
        modelo.put("deaths", new ArrayList<Long>());
        modelo.put("assists", new ArrayList<Long>());
        modelo.put("last_hits", new ArrayList<Long>());
        modelo.put("denies", new ArrayList<Long>());
        modelo.put("item_neutral", new ArrayList<Long>());
        modelo.put("backpack_0", new ArrayList<Long>());
        modelo.put("backpack_1", new ArrayList<Long>());
        modelo.put("backpack_2", new ArrayList<Long>());
        modelo.put("item_0", new ArrayList<Long>());
        modelo.put("item_1", new ArrayList<Long>());
        modelo.put("item_2", new ArrayList<Long>());
        modelo.put("item_3", new ArrayList<Long>());
        modelo.put("item_4", new ArrayList<Long>());
        modelo.put("item_5", new ArrayList<Long>());
        modelo.put("gold_per_min", new ArrayList<Long>());
        modelo.put("xp_per_min", new ArrayList<Long>());
        modelo.put("level", new ArrayList<Long>());
        modelo.put("friends", new JSONArray());
        modelo.put("heroes", new JSONArray());
        return modelo;
    }
    private FileReader getFile(String name_argument){
        //Aca se crea o se devuelve el archivo para leer
        String name = name_argument;
        FileReader reader = null;
        try {
            reader = new FileReader(chicos_path + name + ".json");
            
        } catch (FileNotFoundException ex) {
            try{
                JSONObject modelo = new JSONObject();
                modelo.put("name" , name);
                modelo.put("total_matches", 0);
                modelo.put("wins", 0);
                modelo.put("loses", 0);
                modelo.put("leaves", 0);
                modelo.put("last_hits", 0);
                modelo.put("denies", 0);
                modelo.put("kills", 0);
                modelo.put("deaths", 0);
                modelo.put("assists", 0);
                modelo.put("match_id", new ArrayList<Long>());
                modelo.put("heroes", new JSONArray());
                modelo.put("friends", new JSONArray());
                FileWriter file = new FileWriter(chicos_path + name + ".json");  
                file.write(modelo.toJSONString());
                file.flush();
                file.close();
                reader = new FileReader(chicos_path + name + ".json");
                
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return reader;
    }
}
