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

import crawler.Servidor;
import crawler.URLAddress;

public class EscalonadorSimples implements Escalonador{

	@Override
	public URLAddress getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean adicionaNovaPagina(URLAddress urlAdd) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Record getRecordAllowRobots(URLAddress url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putRecorded(String domain, Record domainRec) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean finalizouColeta() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void countFetchedPage() {
		// TODO Auto-generated method stub
		
	}

	
}
