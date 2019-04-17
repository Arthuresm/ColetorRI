/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler.escalonadorCurtoPrazo;

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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Bruno
 */
public class PageFetcher {
    
    private static EscalonadorSimples e;
    
    public PageFetcher (EscalonadorSimples e){
        this.e = e;
    }
    
    public void pageRequest() throws MalformedURLException{
        RobotExclusion robotExclusion = new RobotExclusion();
        URLAddress proxUrl = e.getURL();
        
        Record recordRobot = e.getRecordAllowRobots(proxUrl);
        
        if(recordRobot == null){
            
            URL proxRobot = new URL(proxUrl.getDomain()+"/robots.txt");      
            RecordIterator recordIterRobot = robotExclusion.get(proxRobot);
            
            if (recordIterRobot != null){
                recordRobot = recordIterRobot.getNext();
                e.putRecorded(proxUrl.getAddress(), recordRobot);
          
            }
        
        }
        if(recordRobot != null){
            if(recordRobot.allows(proxUrl.getAddress())){
                
                /*
                    Descobrir como baixar a URL -> ColetorUtil
                    Para descobrir se um path é alcançavel ou não utilizar o metodo
                    allows do Record 
                    Adicionar paginas extraidas das sementes quando necessário 
                
                */
                
                
                
            }
        }
        
    }
}
