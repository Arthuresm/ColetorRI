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
    }
    
    public PageFetcher(){
        
    }
            
    public boolean pageRequest() throws MalformedURLException, IOException, Exception{
        
        RobotExclusion robotExclusion = new RobotExclusion();
        proxUrl = e.getURL();
//        Record rFB = robotExclusion.get(new URL("https://www.facebook.com/robots.txt"), "daniBot");
//        System.out.println("Aceitou o fb index?" + rFB.allows("/index.html"));
//        System.out.println("Aceitou o cgi-bin?" + rFB.allows("/cgi-bin/oioi"));
//        System.out.println("Aceitou o fb o oioi?" + rFB.allows("/lala/oioi"));
//        
//        Record rTerra = robotExclusion.get(new URL("https://www.terra.com.br/robots.txt"), "daniBot");
//        System.out.println("Aceitou o terra index?" + rTerra.allows("/index.html"));
//        System.out.println("Aceitou o cgi-bin?" + rTerra.allows("/cgi-bin/oioi"));
//        System.out.println("Aceitou o terra o oioi?" + rTerra.allows("/lala/oioi"));
        
        if(proxUrl != null){
            //Verificando se o record ja esta disponivel no escalonador
            recordRobot = e.getRecordAllowRobots(proxUrl);
            
            if(recordRobot == null){
                //Record nao disponivel
                //System.out.println("\nRealizando requisicao");
                
                recordRobot = robotExclusion.get(new URL(proxUrl.getURL()+"robots.txt"), "arthurBot");
                
                //System.out.println("Requisicao finalizada\n");
                
                if(recordRobot == null){
                    //System.out.println("Impossivel criar o record para "+proxUrl.getURL()+"robots.txt");
                    if(e.retiraPagina(proxUrl))
                        System.out.println("Pagina retirada do Escalonador"+'\n'+'\n');
                    return false;      
                }     
            }
            //Inserindo o record no escalonador para solicitacao futura
            e.putRecorded(proxUrl.getAddress(), recordRobot);
            
            if(recordRobot.allows(proxUrl.getPath())){
                InputStream download = ColetorUtil.getUrlStream("arthurBot", new URL(proxUrl.getAddress()));
                String pagina = ColetorUtil.consumeStream(download);    //Nesse ponto ja e possivel imprimir o html da pagina
                List<String> links = extractLinks(pagina);
                //mostList(links);
                
                for(String link : links){
                    if(ColetorUtil.isAbsoluteURL(link)){
                        URLAddress nvpg = new URLAddress(link,proxUrl.getDepth()+1);
                        if(e.adicionaNovaPagina(nvpg)){
                            e.countFetchedPage();
                            System.out.println("Link: "+link+" coletado.");
                        }//else
                            //System.out.println("Link: "+link+" "+"recusado.");
                    }else{
                        URLAddress nvpg = new URLAddress(proxUrl.getAddress()+proxUrl.getPath()+link,proxUrl.getDepth()+1);
                        if(e.adicionaNovaPagina(nvpg)){
                            e.countFetchedPage();
                            System.out.println("Link: "+proxUrl.getAddress()+proxUrl.getPath()+link+" relativo coletado.");
                        }//else
                            //System.out.println("Link: "+link+" "+"recusado.");
                    }
                }
            }else{
                System.out.println("Nao possui autorizacao para coletar a pagina");
                if(e.retiraPagina(proxUrl))
                    System.out.println("Pagina retirada");
                return false;
            }          
        }
        //System.out.println("Fim page request");
        return true;
    }
    
    public static void mostList(List<String> lista){
        Iterator<String> listaAsIterator = lista.iterator();
        while(listaAsIterator.hasNext()){
            System.out.println(listaAsIterator.next());
        }
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