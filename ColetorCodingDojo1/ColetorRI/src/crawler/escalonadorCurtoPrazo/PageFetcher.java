/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.escalonadorCurtoPrazo;

import coletorri.ColetorUtil;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;

import coletorri.Servidor;
import coletorri.URLAddress;
import com.trigonic.jrobotx.RecordIterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document; 
import org.jsoup.nodes.Element; 
import org.jsoup.select.Elements;



/**
 *
 * @author Bruno
 */
public class PageFetcher {
    
    private static EscalonadorSimples e;
    private Record recordRobot;
    private URLAddress proxUrl;
    
    public PageFetcher (EscalonadorSimples e){
        this.e = e;
        this.proxUrl = e.getURL();
    }
            
    public boolean pageRequest() throws MalformedURLException{
        RobotExclusion robotExclusion = new RobotExclusion();
        
        if(proxUrl != null){
            recordRobot = e.getRecordAllowRobots(proxUrl);
            //System.out.println("Record Robot!");
            if(recordRobot == null){
                
                //System.out.println("Record Robot não encontrado!");
                URL proxRobot = new URL(proxUrl.getAddress()+"/robots.txt");
                //System.out.println("RobotExclusion executado!");
                RecordIterator recordIterRobot = robotExclusion.get(proxRobot);
                //System.out.println("RobotExclusion executado!");
                
                if (recordIterRobot != null){
                    //System.out.println("Record Iter Robot encontrado!");
                    recordRobot = recordIterRobot.getNext();
                    
                    if(recordRobot == null){
                        //System.out.println("Record Robot mal formado!");
                        return false;
                    }
                    
                    System.out.println("Record Robot: "+recordRobot.toString());
                    e.putRecorded(proxUrl.getAddress(), recordRobot);
                    System.out.println("Record Robot salvo!");
                }else{
                   System.out.println("recordIterRobot is null!");
                   return false;
                }   
            }
            String path = proxUrl.getPath();
            System.out.println("Robot: "+recordRobot.toString()+" - "+"Length: "+proxUrl.getPath().length());
            if(recordRobot.allows(path)){
                    /*
                        Descobrir como baixar a URL -> ColetorUtil
                        Para descobrir se um path é alcançavel ou não utilizar o metodo
                        allows do Record 
                        Adicionar paginas extraidas das sementes quando necessário 

                    */
                System.out.println("Record Robot allowed!");    
                InputStream downloadStream;
                Iterator<String> iteratorAgent = recordRobot.getUserAgents().iterator();
                String paginaColetada;
                //System.out.println("Record Robot allowed!");
                proxAgent:
                while (iteratorAgent.hasNext()){
                    String Agent = iteratorAgent.next();

                    try{
                        downloadStream = ColetorUtil.getUrlStream(Agent, proxUrl.getURL());
                        //System.out.println("Download Stream open!");
                        paginaColetada = ColetorUtil.consumeStream(downloadStream);
                        System.out.println("pagina obtida!");
                        List<String> links = extractLinks(paginaColetada);
                        for(String link : links){
                            if(ColetorUtil.isAbsoluteURL(link)){
                                URLAddress nova = new URLAddress(link, proxUrl.getDepth()+1);
                                e.countFetchedPage();
                                System.out.println("Fetched!");
                                return e.adicionaNovaPagina(nova);
                            }else{
                                URLAddress nova = new URLAddress(proxUrl.getAddress()+'/'+link, proxUrl.getDepth()+1);
                                e.countFetchedPage();
                                System.out.println("Fetched!");
                                return e.adicionaNovaPagina(nova);
                            }
                        }
                    }catch(Exception except){
                        break proxAgent; 
                    }
                }
                System.out.println("Agent not allowed!");
                return false;
            }else{
                System.out.println("Record Robot not allowed!");
                return false;        
            }
        }
        System.out.println("Url not allowed!");
        return false;
    }
    
    
    public static List<String>extractLinks(String pag) throws Exception { 
        final ArrayList<String> result = new ArrayList<String>(); 
        
        Document doc = Jsoup.parse(pag); 
        Elements links = doc.select("a[href]"); 
        
        for (Element link : links) { 
            result.add(link.attr("abs:href")); 
        } 
        return result; 
    } 
}
