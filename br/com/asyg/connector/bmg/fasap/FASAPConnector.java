package br.com.asyg.connector.bmg.fasap;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

import br.com.asyg.connector.bmg.fasap.controllers.CustWorks;
import br.com.asyg.connector.bmg.fasap.controllers.EmpEmployment;
import br.com.asyg.connector.bmg.fasap.controllers.EmpJobs;
import br.com.asyg.connector.bmg.fasap.controllers.PerPerson;
import br.com.asyg.connector.bmg.fasap.controllers.PerPersonals;
import br.com.asyg.connector.bmg.fasap.dao.PreFeedDAO;
import br.com.asyg.connector.bmg.fasap.model.Account;
import br.com.asyg.connector.bmg.fasap.model.DadosConexaoVO;

public class FASAPConnector extends Connector implements ConnectorInterface {
	private static String VERSION_INFO = "1.0.0";
	private String url, clientId, userId, grantType, privatekey, companyId, jdbcurl, jdbcpassword, jdbcuser, printMsg;
	private DadosConexaoVO dadosConexaoVO = new DadosConexaoVO();
	private List<Object> userList = new ArrayList<>();
	private int current;
	
	public FASAPConnector() {
		setName("FASAP Connector");
	    setModes(new String[] { "Iterator", "Lookup" });
	}
	
	public void terminate() throws Exception {
		this.url = null;
	    this.clientId = null;
	    this.userId = null;
	    this.grantType = null;
	    this.privatekey = null;
	    this.companyId = null;
	    this.jdbcurl = null;
		this.jdbcpassword = null;
		this.jdbcuser = null;
		
	    super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		logmsg("Initialize FASAP Connector");
		this.grantType = getParam("grantType");
		this.companyId = getParam("companyId");
		this.userId = getParam("userId");
		this.url = getParam("url");
	    this.clientId = getParam("clientId");
	    this.jdbcurl = getParam("jdbcurl");
		this.jdbcpassword = getParam("jdbcpassword");
		this.jdbcuser = getParam("jdbcuser");
	    this.privatekey = readPrivateKey(getParam("privatekey"));
	    this.printMsg = getParam("printMsg");
	    
	    dadosConexaoVO.setUrlSAP(url);
		dadosConexaoVO.setClientIdSAP(clientId);
		dadosConexaoVO.setUserIdSAP(userId);
		dadosConexaoVO.setPrivateKeySAP(privatekey);
		dadosConexaoVO.setGrantTypeSAP(grantType);
		dadosConexaoVO.setCompanyIdSAP(companyId);
		dadosConexaoVO.setUrlIGI(jdbcurl);
		dadosConexaoVO.setUserIGI(jdbcuser);
		dadosConexaoVO.setPassIGI(jdbcpassword);
		dadosConexaoVO.setPagination("200");
		dadosConexaoVO.setAllowMsg(printMsg);
		dadosConexaoVO.setCodificacaoJSON("UTF-8");
	}
	
	public synchronized void selectEntries() throws Exception {
		this.current = 1;
		
		try {
			List<String> matriculasAtivas = new ArrayList<>();
			
			logmsg("Starting Execution PerPersonals ...");
			PerPersonals perPersonals = new PerPersonals();
			matriculasAtivas = perPersonals.preFeed(dadosConexaoVO);
			
			logmsg("Starting Execution EmpJobs ...");
			EmpJobs empJobs = new EmpJobs();
			empJobs.preFeed(dadosConexaoVO, matriculasAtivas);
			
			logmsg("Starting Execution PerPerson ...");
			PerPerson perPerson = new PerPerson();
			perPerson.preFeed(dadosConexaoVO);
			
			logmsg("Starting Execution CustWorks ...");
			CustWorks custWorks = new CustWorks();
			custWorks.preFeed(dadosConexaoVO);
			
			logmsg("Starting Execution EmpEmployment ...");
			EmpEmployment empEmployment = new EmpEmployment();
			empEmployment.preFeed(dadosConexaoVO);
			
			PreFeedDAO SAPDao = new PreFeedDAO(dadosConexaoVO);
			this.userList.addAll(SAPDao.retFeed());
		}
		catch (Exception e) {
			logerror("Exception selectEntries: " + ExceptionUtils.getStackTrace(e));
			throw new Exception("Exception selectEntries: " + e.getMessage());
	    }
	}
	
	public synchronized Entry getNextEntry() throws Exception {
		Entry entry = null;

		try {
			logmsg("Total current: " + current);
			
			if (userList != null && current < userList.size()) {
				entry = new Entry();
				
				Account var2 = (Account)userList.get(current);
				entry.setAttribute("objectclass", "erFASAPAccount");
				entry.setAttribute("eraccountstatus", var2.getErCustStatus().equals("A") ? 0 : 1);
				entry.setAttribute("eruid", var2.getErCpf());
				entry.setAttribute("erUserId", var2.getErUserId());
				entry.setAttribute("erMatricula", var2.getErMatricula());
				entry.setAttribute("erLogin", var2.getErLogin());
				entry.setAttribute("erFirstName", var2.getErFirstName());
				entry.setAttribute("erLastName", var2.getErLastName());
				entry.setAttribute("erPreferedName", var2.getErPreferedName());
				entry.setAttribute("erGender", var2.getErGender());
				entry.setAttribute("erDtIniPerpersonal", var2.getErDtIniPerpersonal());
				entry.setAttribute("erDtFimPerpersonal", var2.getErDtFimPerpersonal());
				entry.setAttribute("erDtUltModEmpjob", var2.getErDtUltModEmpjob());
				entry.setAttribute("erCpf", var2.getErCpf());
				entry.setAttribute("erDtNasc", var2.getErDtNasc());
				entry.setAttribute("erRgNum", var2.getErRgNum());
				entry.setAttribute("erDtEmissaoDoc", var2.getErDtEmissaoDoc());
				entry.setAttribute("erDtValidadeDoc", var2.getErDtValidadeDoc());
				entry.setAttribute("erStatusColaborador", var2.getErStatusColaborador());
				entry.setAttribute("erDtIniEmpjob", var2.getErDtIniEmpjob());
				entry.setAttribute("erDtFimEmpjob", var2.getErDtFimEmpjob());
				entry.setAttribute("erMotivoEvento", var2.getErMotivoEvento());
				entry.setAttribute("erDtIniItrOrg", var2.getErDtIniItrOrg());
				entry.setAttribute("erDtFimAtrOrg", var2.getErDtFimAtrOrg());
				entry.setAttribute("erEmpresa", var2.getErEmpresa());
				entry.setAttribute("erEmpresaDesc", var2.getErEmpresaDesc());
				entry.setAttribute("erEstabelecimento", var2.getErEstabelecimento());
				entry.setAttribute("erEstabelecimentoDesc", var2.getErEstabelecimentoDesc());
				entry.setAttribute("erDepartamento", var2.getErDepartamento());
				entry.setAttribute("erDepartamentoDesc", var2.getErDepartamentoDesc());
				entry.setAttribute("erCargo", var2.getErCargo());
				entry.setAttribute("erCargoDesc", var2.getErCargoDesc());
				entry.setAttribute("erGrupoEmpregado", var2.getErGrupoEmpregado());
				entry.setAttribute("erSubGrupoEmpregado", var2.getErSubGrupoEmpregado());
				entry.setAttribute("erCentroCusto", var2.getErCentroCusto());
				entry.setAttribute("erMatriculaGestor", var2.getErMatriculaGestor());
				entry.setAttribute("erDtUltModAtrOrg", var2.getErDtUltModAtrOrg());
				entry.setAttribute("erStatusColaboradorDesc", var2.getErStatusColaboradorDesc());
				entry.setAttribute("erMotivoEventoDesc", var2.getErMotivoEventoDesc());
				entry.setAttribute("erGrupoEmpregadoDesc", var2.getErGrupoEmpregadoDesc());
				entry.setAttribute("erSubGrupoEmpregadoDesc", var2.getErSubGrupoEmpregadoDesc());
				entry.setAttribute("erCentroCustoDesc", var2.getErCentroCustoDesc());
				entry.setAttribute("erTipoAfastamento", var2.getErTipoAfastamento());
				entry.setAttribute("erDiasAfastamento", var2.getErDiasAfastamento());
				entry.setAttribute("erDtIniAfast", var2.getErDtIniAfast());
				entry.setAttribute("erDtFimAfast", var2.getErDtFimAfast());
				entry.setAttribute("erTipoAfastamentoDesc", var2.getErTipoAfastamentoDesc());
				entry.setAttribute("erDtUltModAfast", var2.getErDtUltModAfast());
				entry.setAttribute("erCustStatus", var2.getErCustStatus());
				entry.setAttribute("erPhoneNumber", var2.getErPhoneNumber());
				entry.setAttribute("erNivelCargo", var2.getErNivelCargo());
				entry.setOperation("modify");
		        current++;
			}
			
			return entry;
		}
		catch (Exception e) {
			logerror("Exception getNextEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception("Exception getNextEntry: " + e.getMessage());
	    }
	}
	
	public void querySchema() throws Exception {
		logmsg("Enter in querySchema");
	}
	
	public void modifyUser(Entry entry) throws Exception { }
	
	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception { }
	
	public Entry queryReply (Entry entry) {
		logmsg("Enter in queryReply");
		return null;
	}
	
	public Entry findEntry(SearchCriteria paramSearchCriteria) throws Exception {
		logmsg("Entering findEntry() method");
		Entry entry = new Entry();
		
		PreFeedSAP controller = new PreFeedSAP(dadosConexaoVO);
		String ret = controller.gerarToken();
		
		if (ret.equals("")) {
			throw new Exception(String.valueOf("Connection test failed, see log for details"));
		}
		
		return entry;
	}
	
	@Override
	public String getVersion() {
		loginfo("Licensed Materials - Property of Asyg");
		
		if (VERSION_INFO == null)
			try {
				String str = getClass().getName().replace(".", "/") + ".class";
		        URL uRL = getClass().getClassLoader().getResource(str);
		        String[] arrayOfString = uRL.toExternalForm().split("!");
		        uRL = new URL(arrayOfString[0].replaceFirst("jar:", ""));
		        JarInputStream jarInputStream = new JarInputStream(uRL.openStream(), false);
		        Attributes attributes = jarInputStream.getManifest().getMainAttributes();
		        VERSION_INFO = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
			}
			catch (Exception e) {
				logerror("Fail to determine the version information: " + ExceptionUtils.getStackTrace(e));
			}
		return VERSION_INFO;
	}
	
	private String readPrivateKey(String path) throws Exception {
		String line = null;
		
		try {
			if (path.equals("")) {
				throw new Exception("Fail read private key, path is null");
            }
			
			File file = new File(path);
			line = FileUtils.readFileToString(file, "UTF-8");
			
			if (line == null) {
				throw new Exception("Fail read private key, line is null");
            }
		}
		catch (Exception e) {
			logmsg("Fail readPrivateKey: " + e);
			throw new Exception("Fail readPrivateKey: " + e.getMessage());
		}
		
		return line;
	}
	
	private void loginfo(String paramString) {
		this.myLog.info(paramString);
	}
	
	private void logerror(String paramString) {
		this.myLog.error(paramString);
	}
}
