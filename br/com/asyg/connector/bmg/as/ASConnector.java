package br.com.asyg.connector.bmg.as;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

import br.com.asyg.connector.bmg.as.model.Account;
import br.com.asyg.connector.bmg.as.model.Entitlements;

public class ASConnector extends Connector implements ConnectorInterface {
	private static String VERSION_INFO = null;
	private List<Object> userList = new ArrayList<>();
	private List<Object> roleList = new ArrayList<>();
	@SuppressWarnings("unused")
	private String clientId, clientSecret, token, url, type, operation, jdbcurl, jdbcuser, jdbcpassword, uid;
	private static Util util = new Util();
	private int current;

	public ASConnector() {
		setName("AS Connector");
		setModes(new String[] { "Iterator", "AddOnly", "Update", "Lookup", "Delete" });
	}
	
	public void terminate() throws Exception {
		this.clientId = null;
		this.clientSecret = null;
		this.token = null;
		this.url = null;
		this.jdbcurl = null;
		this.jdbcpassword = null;
		this.jdbcuser = null;
		super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		loginfo("Initialize AS Connector");
		
		this.type = getParam("type");
		this.url = getParam("url");
		this.clientId = getParam("clientId");
		this.clientSecret = getParam("clientSecret");
		this.token = ""; //getToken(clientSecret, "client_credentials", url, clientId);
		this.jdbcurl = getParam("jdbcurl");
		this.jdbcpassword = getParam("jdbcpassword");
		this.jdbcuser = getParam("jdbcuser");
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
	
	public void selectEntries() throws Exception {
		String context = "", cpf = "", date_s = "", codigoNivel = "", codigoProjeto = "", tipoNivel = "", descricaoNivel = "";
		String ret = "SUCCESS";
		this.current = 0;
		Date date = null;
		JSONObject jsonObject = null;
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		try {
			switch (type) {
				case "User":
					context = "/buscausuarios";
					jsonObject = sendGet(context, "");
					
					if (!type.equals("User"))
			            break;
					
					if (jsonObject != null) {
						if (jsonObject.has("Error")) {
							ret = jsonObject.getString("Error");
						} else {
							for (String key: jsonObject.keySet()) {
								Account usr = new Account();
								JSONObject d = jsonObject.getJSONObject(key);
								
								cpf = d.isNull("CPF") ? "" : d.getString("CPF");
								
								usr.setMatricula(d.isNull("Matricula") ? "" : d.getString("Matricula"));
								usr.setNome(d.isNull("Nome") ? "" : d.getString("Nome"));
								usr.setEmail(d.isNull("Email") ? "" : d.getString("Email"));
								usr.setCpf(cpf);
								usr.setSituacao(d.isNull("Situacao") ? null : d.getInt("Situacao"));
								usr.setCodigoCentroCusto(d.isNull("CodigoCentroCusto") ? null : d.getInt("CodigoCentroCusto"));
								usr.setCodigoEmpresa(d.isNull("CodigoEmpresa") ? null : d.getInt("CodigoEmpresa"));
								usr.setNivelCargo(d.isNull("NivelCargo") ? "" : d.getString("NivelCargo"));
								usr.setCargo(d.isNull("Cargo") ? "" : d.getString("Cargo"));
								usr.setCidade(d.isNull("Cidade") ? "" : d.getString("Cidade"));
								usr.setSetor(d.isNull("Setor") ? "" : d.getString("Setor"));
								usr.setCpfSuperior(d.isNull("CpfSuperiorImediato") ? "" : d.getString("CpfSuperiorImediato"));
								
								date_s = d.isNull("DataNascimento") ? null : d.getString("DataNascimento");
								
								if (date_s != null)
									date = dt.parse(date_s);
								
								usr.setDtNascimento(date == null ? null : date);
								date_s = d.isNull("DataAdmissao") ? null : d.getString("DataAdmissao");
								
								if (date_s != null)
									date = dt.parse(date_s);
								
								usr.setDtAdmissao(date == null ? null : date);
								date_s = d.isNull("DataInclusao") ? null : d.getString("DataInclusao");
								
								if (date_s != null)
									date = dt.parse(date_s);
								
								usr.setDtInclusao(date == null ? null : date);
								date_s = d.isNull("DataMudancaSetor") ? null : d.getString("DataMudancaSetor");
								
								if (date_s != null)
									date = dt.parse(date_s);
								
								usr.setDtModificacao(date == null ? null : date);
								date_s = d.isNull("DataExpiracao") ? null : d.getString("DataExpiracao");
								
								if (date_s != null)
									date = dt.parse(date_s);
								
								usr.setDataExpiracao(date == null ? null : date);
								usr.setLoginWindows(d.isNull("LoginWindows") ? "" : d.getString("LoginWindows"));
								usr.setIndicativoUsuarioEstrangeiro(d.isNull("IndicativoUsuarioEstrangeiro") ? "" : d.getString("IndicativoUsuarioEstrangeiro"));
								usr.setCodigoCargo(d.isNull("CodigoCargo") ? "" : d.getString("CodigoCargo"));
								usr.setCodigoEmpresaContratante(d.isNull("CodigoEmpresaContratante") ? "" : d.getString("CodigoEmpresaContratante"));
								usr.setCodigoSetor(d.isNull("CodigoSetor") ? "" : d.getString("CodigoSetor"));
								
								context = "/buscaAcessosProjetoPorUsuario/" + cpf;
								jsonObject = sendGet(context, "");
								
								if (jsonObject != null) {
									for (String key1 : jsonObject.keySet()) {
										JSONObject d1 = jsonObject.getJSONObject(key1);
										codigoNivel = String.valueOf(d1.isNull("CodigoNivel") ? 0 : d1.getInt("CodigoNivel"));
										codigoProjeto = d1.isNull("CodigoProjeto") ? "" : d1.getString("CodigoProjeto");
										tipoNivel = d1.isNull("TipoNivel") ? "" : d1.getString("TipoNivel");
										
										usr.setEntitlementId(new JSONArray("AS-" + codigoProjeto + "-" + codigoNivel + "-" + tipoNivel));
										
										codigoNivel = ""; codigoProjeto = ""; tipoNivel = "";
									}
					            }
					            
						        userList.add(usr);
							}
						}
					}
					
					return;
				case "Entitlement":
					context = "/buscaniveisacessoporprojeto";
					jsonObject = sendGet(context, "");
					
					if (!type.equals("Group"))
			            break;
					
					if (jsonObject != null) {
						if (jsonObject.has("Error")) {
							ret = jsonObject.getString("Error");
						} else {
							for (String key: jsonObject.keySet()) {
								Entitlements ent = new Entitlements();
								JSONObject d = jsonObject.getJSONObject(key);
	
								codigoNivel = String.valueOf(d.isNull("CodigoNivel") ? 0 : d.getInt("CodigoNivel"));
								codigoProjeto = d.isNull("CodigoProjeto") ? "" : d.getString("CodigoProjeto");
								tipoNivel = d.isNull("TipoNivel") ? "" : d.getString("TipoNivel");
								descricaoNivel = d.isNull("DescricaoNivel") ? "" : util.sanitizeString(d.getString("DescricaoNivel"));
										
								ent.setEntitlementId("AS-" + codigoProjeto + "-" + codigoNivel + "-" + tipoNivel);
								ent.setEntitlementName(codigoNivel + "-" + descricaoNivel);
								ent.setDescription(d.isNull("ObservacaoNivel") ? "" : util.sanitizeString(d.getString("ObservacaoNivel")));
								ent.setPermissonType(codigoProjeto + "-" + tipoNivel);
								
								roleList.add(ent);
								
								codigoNivel = ""; codigoProjeto = ""; tipoNivel = ""; descricaoNivel = "";
							}
						}
					}
					
					return;
				default:
					logerror("Invalid entry type");
					throw new Exception("Invalid entry type");
			}
			
			if (!ret.equals("SUCCESS")) {
				logerror("return " + ret);
				throw new Exception("return " + ret);
			}
		}
		catch (Exception e) {
			logerror("Exception selectEntries: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	public synchronized Entry getNextEntry() throws Exception {
		Entry entry = null;
		
		try {
			switch (type) {
				case "User":
					if (!type.equals("User"))
			            break;
					
					if (userList != null && current < userList.size()) {
						Account var1 = (Account)userList.get(current);
						entry = retUser(var1);
				        current++;
					}
					
					return entry;
				case "Entitlement":
					if (!type.equals("Group"))
			            break;
					
					if (roleList != null && current < roleList.size()) {
						Entitlements var1 = (Entitlements)roleList.get(current);
						entry = retRole(var1);
				        current++;
					}
					
					return entry;
			}
			
			logerror(new String(getParam("entrytype")));
		    throw new Exception(new String(getParam("entrytype")));
		}
		catch (Exception e) {
			logerror("Exception getNextEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	private Entry retUser(Account var2) throws Exception {
	    Entry var1 = new Entry();
	    var1.setAttribute("eruid", var2.getCpf());
	    var1.setAttribute("ermatricula", var2.getMatricula());
	    var1.setAttribute("ernome", var2.getNome());
	    var1.setAttribute("eremail", var2.getEmail());
	    var1.setAttribute("ersituacao", var2.getSituacao());
	    var1.setAttribute("ercodigoCentroCusto", var2.getCodigoCentroCusto());
	    var1.setAttribute("ercodigoEmpresa", var2.getCodigoEmpresa());
	    var1.setAttribute("ernivelCargo", var2.getNivelCargo());
	    var1.setAttribute("ercargo", var2.getCargo());
	    var1.setAttribute("ercidade", var2.getCidade());
	    var1.setAttribute("ersetor", var2.getSetor());
	    var1.setAttribute("ercpfSuperior", var2.getCpfSuperior());
	    var1.setAttribute("erdtNascimento", var2.getDtNascimento());
	    var1.setAttribute("erdtAdmissao", var2.getDtAdmissao());
	    var1.setAttribute("erdtDesligamento", var2.getDtDesligamento());
	    var1.setAttribute("erdtInclusao", var2.getDtInclusao());
	    var1.setAttribute("erdtModificacao", var2.getDtModificacao());
	    var1.setAttribute("erloginWindows", var2.getLoginWindows());
	    var1.setAttribute("erindicativoUsuarioEstrangeiro", var2.getIndicativoUsuarioEstrangeiro());
	    var1.setAttribute("ercodigoCargo", var2.getCodigoCargo());
	    var1.setAttribute("ercodigoEmpresaContratante", var2.getCodigoEmpresaContratante());
	    var1.setAttribute("ercodigoSetor", var2.getCodigoSetor());
	    var1.setAttribute("erdataExpiracao", var2.getDataExpiracao());
	    
	    JSONArray jsArray = new JSONArray(var2.getEntitlementId());
		
        for (int i = 0; i < jsArray.length(); i++) {
        	var1.addAttributeValue("erentitlementId", jsArray.getString(i));
        }
	    
	    return var1;
	}
	
	private Entry retRole(Entitlements var2) throws Exception {
	    Entry var1 = new Entry();
	    var1.setAttribute("erentitlementId", var2.getEntitlementId());
	    var1.setAttribute("erentitlementName", var2.getEntitlementName());
	    var1.setAttribute("erdescription", var2.getDescription());
	    var1.setAttribute("erpermissonType", var2.getPermissonType());
	    return var1;
	}
	
	public void querySchema() throws Exception {
		loginfo("querySchema");
	}

	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception {
		JSONObject j1 = new JSONObject();
		String ret = "";
		
		try {
			String context = "/alterausuario";
			
			j1.put("CPF", entry.getString("eruid"));
			j1.put("Situacao", 1);
			
			ret = sendPost(context, j1.toString());
			
			if (!ret.equals("SUCCESS")) {
				throw new Exception(String.valueOf(ret));
			}
		}
		catch (Exception e) {
			logerror("Exception deleteEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	public void putEntry(Entry entry) throws Exception {
		String ret = "";
		
		try {
			ret = addAccount(entry);
			
			if (!ret.equals("SUCCESS")) {
				throw new Exception(String.valueOf(ret));
			}
		}
		catch (Exception e) {
			logerror("Exception putEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	public String addAccount(Entry entry) {
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		JSONObject j1 = new JSONObject();
		String ret = "";
		Date date = null;
		
		try {
			String context = "/adicionausuario";
			
			if (type.equals("User")) {
				j1.put("CPF", entry.getString("eruid"));
				j1.put("Matricula", entry.getString("ermatricula"));
				j1.put("Nome", entry.getString("ernome"));
				j1.put("Email", entry.getString("eremail"));
				j1.put("Situacao", 0);
				j1.put("CodigoCentroCusto", util.isNullOrEmpty(entry.getString("ercodigoCentroCusto")) ? null : Integer.parseInt(entry.getString("ercodigoCentroCusto")));
				j1.put("CodigoEmpresa", util.isNullOrEmpty(entry.getString("ercodigoEmpresa")) ? null : Integer.parseInt(entry.getString("ercodigoEmpresa")));
				j1.put("NivelCargo", entry.getString("ernivelCargo"));
				j1.put("Cargo", entry.getString("ercargo"));
				j1.put("Cidade", entry.getString("ercidade"));
				j1.put("Setor", entry.getString("ersetor"));
				j1.put("CpfSuperiorImediato", entry.getString("ercpfSuperior"));
				
				if (!util.isNullOrEmpty(entry.getString("erdtNascimento")))
					date = dt.parse(entry.getString("erdtNascimento"));
				
				j1.put("DataNascimento", date == null ? null : dt1.format(date));
				date = null;
				
				if (!util.isNullOrEmpty(entry.getString("erdtAdmissao")))
					date = dt.parse(entry.getString("erdtAdmissao"));
				
				j1.put("DataAdmissao", date == null ? null : dt1.format(date));
				date = null;
				
				if (!util.isNullOrEmpty(entry.getString("erdtInclusao")))
					date = dt.parse(entry.getString("erdtInclusao"));
				
				j1.put("DataInclusao", date == null ? null : dt1.format(date));
				date = null;

				if (!util.isNullOrEmpty(entry.getString("erdataExpiracao")))
					date = dt.parse(entry.getString("erdataExpiracao"));
				
				j1.put("DataExpiracao", date == null ? null : dt1.format(date));
				j1.put("LoginWindows", entry.getString("erloginWindows"));
				j1.put("IndicativoUsuarioEstrangeiro", entry.getString("erindicativoUsuarioEstrangeiro"));
				j1.put("CodigoCargo", entry.getString("ercodigoCargo"));
				j1.put("CodigoEmpresaContratante", entry.getString("ercodigoEmpresaContratante"));
				j1.put("CodigoSetor", entry.getString("ercodigoSetor"));

				loginfo("j1 " + j1.toString());
				
				ret = sendPost(context, j1.toString());
			}
		}
		catch (Exception e) {
			logerror("Exception addAccount: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return ret;
	}
	
	public void modEntry(Entry entry, SearchCriteria search, Entry old) throws Exception {
		logdebug("modEntry " + entry);
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		JSONObject j1 = null;
		String context = "", ret = "";
		Date date = null;
		
		try {
			switch (operation) {
				case "Suspend":
					j1 = new JSONObject();
					context = "/alterausuario";
					
					j1.put("CPF", entry.getString("eruid"));
					j1.put("Situacao", getStatus(entry.getString("eruid")));
					
					ret = sendPost(context, j1.toString());
						
					break;
				case "Restore":
					j1 = new JSONObject();
					context = "/alterausuario";
					
					j1.put("CPF", entry.getString("eruid"));
					j1.put("Situacao", 0);
					
					ret = sendPost(context, j1.toString());
					break;
				case "Modify":
					j1 = new JSONObject();
					
					if (!util.isNullOrEmpty(entry.getString("entitlementId"))) {
						logmsg("Operation: " + entry.getString("grpoperation"));
						
						if (entry.getString("grpoperation").equals("add")) {
							context = "/adicionaacesso";
					    } else {
					    	context = "/removeacesso";
					    }
						
						String[] values = entry.getString("entitlementId").split("-");

						j1.put("CPF", entry.getString("eruid"));
						j1.put("CodigoProjeto", values[1]);
						j1.put("CodigoNivel", Integer.parseInt(values[2]));
						j1.put("TipoNivel", Integer.parseInt(values[3]));
					} else {
						context = "/alterausuario";
						
						j1.put("CPF", entry.getString("eruid"));
						
						if (!util.isNullOrEmpty(entry.getString("ermatricula")))
							j1.put("Matricula", entry.getString("ermatricula"));
						
			            if (!util.isNullOrEmpty(entry.getString("ernome")))
			            	j1.put("Nome", entry.getString("ernome"));

			            if (!util.isNullOrEmpty(entry.getString("eremail")))
			            	j1.put("Email", entry.getString("eremail"));

			            if (!util.isNullOrEmpty(entry.getString("ercodigoCentroCusto")))
			            	j1.put("CodigoCentroCusto", Integer.parseInt(entry.getString("ercodigoCentroCusto")));

			            if (!util.isNullOrEmpty(entry.getString("ercodigoEmpresa")))
			            	j1.put("CodigoEmpresa", Integer.parseInt(entry.getString("ercodigoEmpresa")));

			            if (!util.isNullOrEmpty(entry.getString("ernivelCargo")))
			            	j1.put("NivelCargo", entry.getString("ernivelCargo"));

			            if (!util.isNullOrEmpty(entry.getString("ercargo")))
			            	j1.put("Cargo", entry.getString("ercargo"));

			            if (!util.isNullOrEmpty(entry.getString("ercidade")))
			            	j1.put("Cidade", entry.getString("ercidade"));

			            if (!util.isNullOrEmpty(entry.getString("ersetor")))
			            	j1.put("Setor", entry.getString("ersetor"));

			            if (!util.isNullOrEmpty(entry.getString("ercpfSuperior")))
			            	j1.put("CpfSuperiorImediato", entry.getString("ercpfSuperior"));

			            if (util.isNullOrEmpty(entry.getString("erdtNascimento"))) {
							date = dt.parse(entry.getString("erdtNascimento"));
							j1.put("DataNascimento", date != null ? null : dt1.format(date));
							date = null;
						}

						if (util.isNullOrEmpty(entry.getString("erdtAdmissao"))) {
							date = dt.parse(entry.getString("erdtAdmissao"));
							j1.put("DataAdmissao", date != null ? null : dt1.format(date));
							date = null;
						}

						if (util.isNullOrEmpty(entry.getString("erdtInclusao"))) {
							date = dt.parse(entry.getString("erdtInclusao"));
							j1.put("DataInclusao", date != null ? null : dt1.format(date));
							date = null;
						}

						if (util.isNullOrEmpty(entry.getString("erdataExpiracao"))) {
							date = dt.parse(entry.getString("erdataExpiracao"));
							j1.put("DataExpiracao", date != null ? null : dt1.format(date));
						}

			            if (!util.isNullOrEmpty(entry.getString("erloginWindows")))
			            	j1.put("LoginWindows", entry.getString("erloginWindows"));

			            if (!util.isNullOrEmpty(entry.getString("erindicativoUsuarioEstrangeiro")))
			            	j1.put("IndicativoUsuarioEstrangeiro", entry.getString("erindicativoUsuarioEstrangeiro"));

			            if (!util.isNullOrEmpty(entry.getString("ercodigoCargo")))
			            	j1.put("CodigoCargo", entry.getString("ercodigoCargo"));

			            if (!util.isNullOrEmpty(entry.getString("ercodigoEmpresaContratante")))
			            	j1.put("CodigoEmpresaContratante", entry.getString("ercodigoEmpresaContratante"));

			            if (!util.isNullOrEmpty(entry.getString("ercodigoSetor")))
			            	j1.put("CodigoSetor", entry.getString("ercodigoSetor"));
					}
					
					ret = sendPost(context, j1.toString());
					break;
				default:
					logerror("Invalid entry type");
					throw new Exception("Invalid entry type");
			}
	
			if (!ret.equals("SUCCESS")) {
				throw new Exception(String.valueOf(ret));
			}
		}
		catch (Exception e) {
			logerror("Exception modifyUser: " + ExceptionUtils.getStackTrace(e));
		}
	}
		
	public Entry queryReply(Entry entry) {
		return null;
	}
	
	private String getStatus(String eruid) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String ret = "";
		
		try {
			conn = Util.getConnection("com.ibm.db2.jcc.DB2Driver", jdbcurl, jdbcuser, jdbcpassword);
			
			stmt = conn.prepareStatement("SELECT C_CONTROLE_TIPO_BLOQUEIO "
					+ "FROM igacore.user_erc ue, igacore.PWDCFG pcfg, igacore.PWDMANAGEMENT pwd, igacore.person p "
					+ "WHERE ue.id = p.USER_ERC "
					+ "AND p.id = pwd.PERSON "
					+ "AND pwd.pwdcfg = pcfg.id "
					+ "AND pcfg.name = 'AS' "
					+ "AND pwd.code = '" + eruid + "'");
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				ret = rs.getString("C_CONTROLE_TIPO_BLOQUEIO");
			}
			
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			logerror("Exception sendPost: " + ExceptionUtils.getStackTrace(e));
			ret = e.getMessage();
	    }
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (Exception e) {
				logerror("Exception " + ExceptionUtils.getStackTrace(e));
				throw new Exception(e.getMessage());
			}
		}
		
		return ret;
	}
	
	/**
	 * Metodo para envio dos dados da conta (criacao, atualizacao)
	 * @return codigo HTTP do servico
	 * @return 200 para sucesso
	 * @param context: contexto para busca
	 * @param content: corpo da requisicao
	 */
	private String sendPost(String context, String content) throws Exception {
		String jsonResult = null;
		String ret = "SUCCESS";
		
		try {
			HttpPost var = new HttpPost(url.toString() + context);
			var.addHeader("Content-Type", "application/json; charset=utf-8");
			var.setEntity(new StringEntity(content));
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
				int responseCode = response.getStatusLine().getStatusCode();

				logdebug("URL: " + var.getURI());
				logdebug("Method: " + var.getMethod());
				logdebug("Status line: " + response.getStatusLine().toString());

				if (responseCode == 200) {
					jsonResult = EntityUtils.toString(response.getEntity());
					logdebug("Result: " + jsonResult);
				}
	        }
		}
		catch (Exception e) {
			logerror("Exception sendPost: " + ExceptionUtils.getStackTrace(e));
			ret = e.getMessage();
	    }
		
		return ret;
	}
	
	/**
	 * Metodo para busca de contas ou direitos
	 * @return JSONObject com os dados
	 * @param context: contexto para busca
	 * @param content: corpo da requisicao
	 */
	private JSONObject sendGet(String context, String content) throws Exception {
		JSONObject j1 = new JSONObject();
		
		try {	
			HttpGet var = new HttpGet(url.toString() + context);
			var.addHeader("Content-Type", "application/json; charset=utf-8");
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {

	        	logdebug("URL: " + var.getURI());
	        	logdebug("Method: " + var.getMethod());
	        	logdebug("Status line: " + response.getStatusLine().toString());
	        	logdebug("getEntity: " + response.getEntity());
				
				if (response.getEntity() != null) {
					String jsonResult = EntityUtils.toString(response.getEntity());
					j1 = new JSONObject(jsonResult);
				}
			}
		}
		catch (Exception e) {
			j1.put("Error", e.getMessage());
			logerror("Exception sendGet: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return j1;
	}
	
	/**
	 * Metodo para obter o token para conexao ao recurso
	 * Nao utilizado neste versao do conector
	 * @return token
	 * @param url: URL do sistema
	 * @param client_secret: senha do usuario
	 * @param grant_type: tipo de concessao
	 * @param client_id: id de conexao
	 */
	@SuppressWarnings("unused")
	private String getToken(String client_secret, String grant_type, String url, String client_id) {
		String token = "";
		
		try {
			HttpPost post = new HttpPost(url.toString());
			post.addHeader("Content-Type", "application/json; charset=utf-8");
	        post.addHeader("client_secret", client_secret);
	        post.addHeader("grant_type", grant_type);
	        post.addHeader("client_id", client_id);
	        post.setEntity(new StringEntity("{}"));
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(post)) {

	        	logdebug("URL: " + post.getURI());
	        	logdebug("Status line: " + response.getStatusLine().toString());

				if (response.getEntity() != null) {
					JSONObject jsonResult = new JSONObject(EntityUtils.toString(response.getEntity()));
					logdebug("Result: " + jsonResult.toString());
					token = jsonResult.getString("access_token");
				}
	        }
		}
		catch (Exception e) {
			logerror("Exception getToken: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return token;
	}
	
	private void loginfo(String paramString) {
		this.myLog.info(paramString);
	}
	
	private void logerror(String paramString) {
		this.myLog.error(paramString);
	}
	
	private void logdebug(String paramString) {
		this.myLog.debug(paramString);
	}
}
