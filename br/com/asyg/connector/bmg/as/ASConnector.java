package br.com.asyg.connector.bmg.as;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
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
	private List<Object> perList = new ArrayList<>();
	private List<Object> resList = new ArrayList<>();
	private String url, type, operation, jdbcurl, jdbcuser, jdbcpassword, uid, filepath;
	//private String clientId, clientSecret, token;
	private static Util util = new Util();
	private int current;
	
	/**
	 * In the constructor you will usually set the name of your Connector (using the "setName(...)" 
	 * method) and define what modes - Iterator, Lookup, AddOnly, Server, Delta etc
	 */
	public ASConnector() {
		setName("AS Connector");
		setModes(new String[] { "Iterator", "AddOnly", "Update", "Lookup", "Delete" });
	}
	
	/**
	 * Method is called by the AssemblyLine after it has finished cycling and before it terminates.
	 */
	public void terminate() throws Exception {
		loginfo("Terminate AS Connector");
		
		/*
		this.clientId = null;
		this.clientSecret = null;
		this.token = null;
		*/
		this.url = null;
		this.jdbcurl = null;
		this.jdbcpassword = null;
		this.jdbcuser = null;
		this.filepath = null;
		super.terminate();
	}
	
	/**
	 * This method is called by the AssemblyLine before it starts cycling. 
	 * In general anybody who creates and uses a Connector programmatically should call "initialize(...)" 
	 * after constructing the Connector and before calling any other method
	 */
	public void initialize(Object o) throws Exception {
		loginfo("Initialize AS Connector");
		
		/*
		this.clientId = getParam("clientId");
		this.clientSecret = getParam("clientSecret");
		this.token = getToken(clientSecret, "client_credentials", url, clientId);
		*/
		this.type = getParam("type");
		this.url = getParam("url");
		this.jdbcurl = getParam("jdbcurl");
		this.jdbcpassword = getParam("jdbcpassword");
		this.jdbcuser = getParam("jdbcuser");
		this.operation = getParam("operation");
		this.filepath = getParam("filePath");
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
	
	/**
	 * Required for Iterator mode. This method is called only when the Connector is used in Iterator mode, after it has been initialized
	 */
	public void selectEntries() throws Exception {
		loginfo("Entering selectEntries() method");
		String cpf = "", date_s = "", codigoNivel = "", codigoProjeto = "", tipoNivel = "", descricaoNivel = "", ret = "SUCCESS", test = "";
		this.current = 0;
		Date date = null;
		JSONArray jarray = new JSONArray(), jaccess = new JSONArray(), tmp1 = new JSONArray(), tmp2 = new JSONArray();
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		try {
			String[] arrProjeto = readFile(filepath);
			
			switch (type) {
				case "User":
					jarray = sendGet("/buscausuarios", "");
					
					if (!type.equals("User"))
			            break;
					
					if (!jarray.isEmpty()) {
						if (jarray.getJSONObject(0).has("Error")) {
							ret = jarray.getJSONObject(0).getString("Error");
						} else {
							for (int x = 0; x < jarray.length(); x++) {
								Account usr = new Account();
								JSONObject d = jarray.getJSONObject(x);
								
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
								usr.setIndicativoUsuarioEstrangeiro(d.isNull("IndicativoUsuarioEstrangeiro") ? null : d.getInt("IndicativoUsuarioEstrangeiro"));
								usr.setCodigoCargo(d.isNull("CodigoCargo") ? null : d.getInt("CodigoCargo"));
								usr.setCodigoEmpresaContratante(d.isNull("CodigoEmpresaContratante") ? null : d.getLong("CodigoEmpresaContratante"));
								usr.setCodigoSetor(d.isNull("CodigoSetor") ? null : d.getInt("CodigoSetor"));
								
								jaccess = new JSONArray();
								tmp1 = new JSONArray();
								tmp2 = new JSONArray();
								jaccess = sendGet("/buscaAcessosProjetoPorUsuario/" + cpf, "");
								
								if (!jaccess.isEmpty()) {
									for (int z = 0; z < jaccess.length(); z++) {
										codigoNivel = ""; codigoProjeto = ""; tipoNivel = "";
										JSONObject d1 = jaccess.getJSONObject(z);
										codigoNivel = String.valueOf(d1.isNull("CodigoNivel") ? 0 : d1.getInt("CodigoNivel"));
										codigoProjeto = d1.isNull("CodigoProjeto") ? "" : d1.getString("CodigoProjeto");
										tipoNivel = d1.isNull("TipoNivel") ? "" : d1.getString("TipoNivel");
										
										if (tipoNivel.equals("P")) {
											tmp1.put("AS-" + codigoProjeto + "-" + codigoNivel + "-" + tipoNivel);
										} else {
											tmp2.put("AS-" + codigoProjeto + "-" + codigoNivel + "-" + tipoNivel);
										}
									}
					            }
								
								usr.setPermissiveId(tmp1);
								usr.setRestrictiveId(tmp2);
					            
						        userList.add(usr);
							}
						}
					}
					
					return;
				case "Permissive":
					jarray = sendGet("/buscaniveisacessoporprojeto", "");
					
					if (!type.equals("Permissive"))
			            break;
					
					if (!jarray.isEmpty()) {
						if (jarray.getJSONObject(0).has("Error")) {
							ret = jarray.getJSONObject(0).getString("Error");
						} else {
							for (int x = 0; x < jarray.length(); x++) {
								codigoNivel = ""; codigoProjeto = ""; tipoNivel = ""; descricaoNivel = "";
								Entitlements ent = new Entitlements();
								JSONObject d = jarray.getJSONObject(x);
								
								codigoNivel = String.valueOf(d.isNull("CodigoNivel") ? 0 : d.getInt("CodigoNivel"));
								codigoProjeto = d.isNull("CodigoProjeto") ? "" : d.getString("CodigoProjeto");
								tipoNivel = d.isNull("TipoNivel") ? "" : d.getString("TipoNivel");
								descricaoNivel = d.isNull("DescricaoNivel") ? "" : d.getString("DescricaoNivel");
								
								ent.setEntitlementId("AS-" + codigoProjeto + "-" + codigoNivel + "-" + tipoNivel);
								ent.setEntitlementName(codigoNivel + "-" + descricaoNivel);
								ent.setDescription(d.isNull("ObservacaoNivel") ? "" : d.getString("ObservacaoNivel"));
								ent.setPermissonType(codigoProjeto + "-" + tipoNivel);
								
								test = "AS-" + codigoProjeto;
								boolean contains = Arrays.stream(arrProjeto).anyMatch(test::equals);
								
								if (contains) {
									if (tipoNivel.equals("P")) {
										perList.add(ent);
									}
								}
							}
						}
					}
					
					return;
				case "Restrictive":
					jarray = sendGet("/buscaniveisacessoporprojeto", "");
					
					if (!type.equals("Restrictive"))
			            break;
					
					if (!jarray.isEmpty()) {
						if (jarray.getJSONObject(0).has("Error")) {
							ret = jarray.getJSONObject(0).getString("Error");
						} else {
							for (int x = 0; x < jarray.length(); x++) {
								codigoNivel = ""; codigoProjeto = ""; tipoNivel = ""; descricaoNivel = "";
								
								Entitlements ent = new Entitlements();
								JSONObject d = jarray.getJSONObject(x);
								
								codigoNivel = String.valueOf(d.isNull("CodigoNivel") ? 0 : d.getInt("CodigoNivel"));
								codigoProjeto = d.isNull("CodigoProjeto") ? "" : d.getString("CodigoProjeto");
								tipoNivel = d.isNull("TipoNivel") ? "" : d.getString("TipoNivel");
								descricaoNivel = d.isNull("DescricaoNivel") ? "" : d.getString("DescricaoNivel");
								
								ent.setEntitlementId("AS-" + codigoProjeto + "-" + codigoNivel + "-" + tipoNivel);
								ent.setEntitlementName(codigoNivel + "-" + descricaoNivel);
								ent.setDescription(d.isNull("ObservacaoNivel") ? "" : d.getString("ObservacaoNivel"));
								ent.setPermissonType(codigoProjeto + "-" + tipoNivel);
								
								test = "AS-" + codigoProjeto;
								boolean contains = Arrays.stream(arrProjeto).anyMatch(test::equals);
								
								if (contains) {
									if (tipoNivel.equals("R")) {
										resList.add(ent);
									}
								}
							}
						}
					}
					
					return;
				default:
					logerror("Invalid entry type " + type);
					throw new Exception("Invalid entry type " + type);
			}
			
			if (!ret.equals("SUCCESS")) {
				logerror("return " + ret);
				throw new Exception(ret);
			}
		}
		catch (Exception e) {
			logerror("Exception selectEntries: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	/**
	 * Required for Iterator mode. This is the method called on each AssemblyLine's iteration when the Connector is in Iterator mode.
	 * It is expected to return a single Entry that feeds the rest of the AssemblyLine
	 */
	public synchronized Entry getNextEntry() throws Exception {
		loginfo("Entering getNextEntry() method");
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
				case "Permissive":
					if (!type.equals("Permissive"))
			            break;
					
					if (perList != null && current < perList.size()) {
						Entitlements var1 = (Entitlements)perList.get(current);
						entry = retPermissive(var1);
				        current++;
					}
					
					return entry;
				case "Restrictive":
					if (!type.equals("Restrictive"))
			            break;
					
					if (resList != null && current < resList.size()) {
						Entitlements var1 = (Entitlements)resList.get(current);
						entry = retRestrictive(var1);
				        current++;
					}
					
					return entry;
				default:
					logerror("Invalid entry type " + type);
					throw new Exception("Invalid entry type " + type);
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
		loginfo("Entering retUser() method");
	    Entry var1 = new Entry();
	    var1.setAttribute("eruid", var2.getCpf());
	    var1.setAttribute("ermatricula", var2.getMatricula());
	    var1.setAttribute("ernome", var2.getNome());
	    var1.setAttribute("eremail", var2.getEmail());
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
	    var1.setAttribute("eraccountstatus", var2.getSituacao());
	    
	    JSONArray jsPermissive = new JSONArray(var2.getPermissiveId());
		
        for (int i = 0; i < jsPermissive.length(); i++) {
        	var1.addAttributeValue("erpermissiveId", jsPermissive.getString(i));
        }
        
        JSONArray jsRestrictive = new JSONArray(var2.getRestrictiveId());
		
        for (int i = 0; i < jsRestrictive.length(); i++) {
        	var1.addAttributeValue("errestrictiveId", jsRestrictive.getString(i));
        }
	    
	    return var1;
	}
	
	private Entry retPermissive(Entitlements var2) throws Exception {
		loginfo("Entering retPermissive() method");
	    Entry var1 = new Entry();
	    var1.setAttribute("erpermissiveId", var2.getEntitlementId());
	    var1.setAttribute("erpermissiveName", var2.getEntitlementName());
	    var1.setAttribute("erpermissiveDescription", var2.getDescription());
	    var1.setAttribute("erpermissiveType", var2.getPermissonType());
	    return var1;
	}
	
	private Entry retRestrictive(Entitlements var2) throws Exception {
		loginfo("Entering retRestrictive() method");
	    Entry var1 = new Entry();
	    var1.setAttribute("errestrictiveId", var2.getEntitlementId());
	    var1.setAttribute("errestrictiveName", var2.getEntitlementName());
	    var1.setAttribute("errestrictiveDescription", var2.getDescription());
	    var1.setAttribute("errestrictiveType", var2.getPermissonType());
	    return var1;
	}
	
	public void querySchema() throws Exception {
		loginfo("querySchema");
	}
	
	/**
	 * Required for Lookup, Update and Delete modes. 
	 * It is called once on each AssemblyLine iteration when the Connector performs a Lookup operation
	 */
	public Entry findEntry(SearchCriteria paramSearchCriteria) throws Exception {
		loginfo("Entering findEntry() method");
		Entry entry = new Entry();
		
		if (type.equals("Test")) {
			String ret = testService();
			
			if (ret.equals("site_down")) {
				logerror("return " + ret);
				throw new Exception(String.valueOf(ret));
			}
			
			entry = new Entry();
		} else {
			entry = new Entry();
			entry.setAttribute("eruid", paramSearchCriteria.getFirstCriteriaValue());
			uid = paramSearchCriteria.getFirstCriteriaValue();
		}
		
		return entry;
	}
	
	/**
	 * Required for Delete mode
	 */
	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception {
		loginfo("Entering deleteEntry() method");
		JSONObject j1 = new JSONObject();
		
		try {
			j1.put("CPF", entry.getString("eruid"));
			j1.put("Situacao", 1);
			
			JSONObject jsonResult = sendPut("/alterausuario", j1.toString());
			
			if (jsonResult.has("Error")) {
				throw new Exception(String.valueOf(jsonResult.getString("Error")));
			}
		}
		catch (Exception e) {
			logerror("Exception deleteEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	/**
	 * Required for AddOnly and Update modes. It is called once on each AssemblyLine iteration when the Connector is used in AddOnly mode
	 */
	public void putEntry(Entry entry) throws Exception {
		loginfo("Entering putEntry() method");
		
		try {
			String ret = addAccount(entry, 0, entry.getString("eruid"));
			
			if (!ret.equals("SUCCESS")) {
				logerror("Exception putEntry " + ret);
				throw new Exception(String.valueOf(ret));
			}
		}
		catch (Exception e) {
			logerror("Exception putEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	/**
	 * Metodo para criar a conta no sistema
	 * @return codigo do status
	 * @param entry: array com os dados da conta
	 * @param status: status da conta
	 */
	public String addAccount(Entry entry, int status, String cpf) {
		loginfo("Entering addAccount() method");
		JSONObject j1 = new JSONObject();
		String ret = "SUCCESS", date = null;
		
		try {
			if (type.equals("User")) {
				j1.put("CPF", cpf);
				j1.put("Matricula", entry.getString("ermatricula"));
				j1.put("Nome", entry.getString("ernome"));
				j1.put("Email", entry.getString("eremail"));
				j1.put("Situacao", status);
				j1.put("CodigoCentroCusto", util.isNullOrEmpty(entry.getString("ercodigoCentroCusto")) ? JSONObject.NULL : Integer.parseInt(entry.getString("ercodigoCentroCusto")));
				j1.put("CodigoEmpresa", util.isNullOrEmpty(entry.getString("ercodigoEmpresa")) ? JSONObject.NULL : Integer.parseInt(entry.getString("ercodigoEmpresa")));
				j1.put("NivelCargo", entry.getString("ernivelCargo"));
				j1.put("Cargo", entry.getString("ercargo"));
				j1.put("Cidade", entry.getString("ercidade"));
				j1.put("Setor", entry.getString("ersetor"));
				j1.put("CpfSuperiorImediato", entry.getString("ercpfSuperior"));
				
				date = util.dateFormat(entry.getString("erdtNascimento"));
				j1.put("DataNascimento", date == null ? JSONObject.NULL : date);
				
				date = util.dateFormat(entry.getString("erdtAdmissao"));
				j1.put("DataAdmissao", date == null ? JSONObject.NULL : date);
				
				date = util.dateFormat(entry.getString("erdtInclusao"));
				j1.put("DataInclusao", date == null ? JSONObject.NULL : date);
				
				date = util.dateFormat(entry.getString("erdataExpiracao"));
				j1.put("DataExpiracao", date == null ? JSONObject.NULL : date);
				
				j1.put("LoginWindows", entry.getString("erloginWindows"));
				j1.put("IndicativoUsuarioEstrangeiro", util.isNullOrEmpty(entry.getString("erindicativoUsuarioEstrangeiro")) ? JSONObject.NULL : Integer.parseInt(entry.getString("erindicativoUsuarioEstrangeiro")));
				j1.put("CodigoCargo", util.isNullOrEmpty(entry.getString("ercodigoCargo")) ? JSONObject.NULL : Integer.parseInt(entry.getString("ercodigoCargo")));
				j1.put("CodigoEmpresaContratante", util.isNullOrEmpty(entry.getString("ercodigoEmpresaContratante")) ? JSONObject.NULL : Long.parseLong(entry.getString("ercodigoEmpresaContratante")));
				j1.put("CodigoSetor", util.isNullOrEmpty(entry.getString("ercodigoSetor")) ? JSONObject.NULL : Integer.parseInt(entry.getString("ercodigoSetor")));
				
				JSONObject jsonResult = sendPost("/adicionausuario", j1.toString());
				
				if (jsonResult.has("Error")) {
					ret = String.valueOf(jsonResult.getString("Error"));
				}
			}
		}
		catch (Exception e) {
			ret = e.getMessage();
			logerror("Exception addAccount: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return ret;
	}
	
	/**
	 * Required for Update mode
	 */
	public void modEntry(Entry entry, SearchCriteria search, Entry old) throws Exception {
		loginfo("modEntry " + operation + ", entry " + entry + ", old " + old);
		JSONObject j1 = null;
		JSONObject jsonResult = new JSONObject();
		String context = "", date = null, ret = "SUCCESS";
		boolean exist = false;
		
		try {
			logmsg("getOperation " + entry.getOperation());
			logmsg("getOperation " + old.getOperation());
			logmsg("uid " + uid);
			
			switch (operation.toLowerCase()) {
				case "suspend":
					j1 = new JSONObject();
					j1.put("CPF", uid);
					j1.put("Situacao",  Integer.parseInt(getStatus(uid)));
					
					exist = searchAccouunt("/buscausuario/" + uid, "");
					
					if (!exist) {
						ret = addAccount(entry, Integer.parseInt(getStatus(uid)), uid);

						if (ret.equals("SUCCESS")) {
							jsonResult.put("Success", "OK");
						} else {
							jsonResult.put("Error", ret);
						}
					} else {
						jsonResult = sendPut("/alterausuario", j1.toString());
						
						if (jsonResult.has("Error")) {
							throw new Exception(String.valueOf(jsonResult.getString("Error")));
						}
					}
					
					break;
				case "restore":
					j1 = new JSONObject();
					j1.put("CPF", uid);
					j1.put("Situacao", 0);
					
					exist = searchAccouunt("/buscausuario/" + uid, "");
					
					if (!exist) {
						ret = addAccount(entry, 0, uid);

						if (ret.equals("SUCCESS")) {
							jsonResult.put("Success", "OK");
						} else {
							jsonResult.put("Error", ret);
						}
					} else {
						jsonResult = sendPut("/alterausuario", j1.toString());
					}
					
					if (jsonResult.has("Error")) {
						throw new Exception(String.valueOf(jsonResult.getString("Error")));
					}
					
					break;
				case "modify":
					j1 = new JSONObject();
					
					exist = searchAccouunt("/buscausuario/" + uid, "");
					
					if (!util.isNullOrEmpty(entry.getString("errestrictiveId")) || !util.isNullOrEmpty(entry.getString("erpermissiveId"))) {
						String[] values = null;
						logmsg("Operation: " + entry.getString("grpoperation"));
						
						if (entry.getString("grpoperation").equals("add")) {
							if (!exist) {
								ret = addAccount(entry, Integer.parseInt(getStatus(uid)), uid);

								if (ret.equals("SUCCESS")) {
									jsonResult.put("Success", "OK");
								} else {
									jsonResult.put("Error", ret);
								}
							}
							
							context = "/adicionaacesso";
					    } else {
					    	context = "/removeacesso";
					    }
						
						if (!util.isNullOrEmpty(entry.getString("errestrictiveId"))) {
							values = entry.getString("errestrictiveId").split("-");
						} else {
							values = entry.getString("erpermissiveId").split("-");
						}
						
						j1.put("cpf", uid);
						j1.put("codigoProjeto", values[1]);
						j1.put("nivelAcesso",values[2]);
						
						jsonResult = sendPost(context, j1.toString());
					} else {
						if (!exist) {
							ret = addAccount(entry, Integer.parseInt(getStatus(uid)), uid);

							if (ret.equals("SUCCESS")) {
								jsonResult.put("Success", "OK");
							} else {
								jsonResult.put("Error", ret);
							}
						} else {
							j1.put("CPF", uid);
							j1.put("Matricula", entry.getString("ermatricula"));
			            	j1.put("Nome", entry.getString("ernome"));
			            	j1.put("Email", entry.getString("eremail"));
			            	j1.put("CodigoCentroCusto", Integer.parseInt(entry.getString("ercodigoCentroCusto")));
			            	j1.put("CodigoEmpresa", Integer.parseInt(entry.getString("ercodigoEmpresa")));
			            	j1.put("NivelCargo", entry.getString("ernivelCargo"));
			            	j1.put("Cargo", entry.getString("ercargo"));
			            	j1.put("Cidade", entry.getString("ercidade"));
			            	j1.put("Setor", entry.getString("ersetor"));
			            	j1.put("CpfSuperiorImediato", entry.getString("ercpfSuperior"));
							
			            	date = util.dateFormat(entry.getString("erdtNascimento"));
							j1.put("DataNascimento", date != null ? JSONObject.NULL : date);
							
							date = util.dateFormat(entry.getString("erdtAdmissao"));
							j1.put("DataAdmissao", date != null ? JSONObject.NULL : date);
							
							date = util.dateFormat(entry.getString("erdtInclusao"));
							j1.put("DataInclusao", date != null ? JSONObject.NULL : date);
							
							date = util.dateFormat(entry.getString("erdataExpiracao"));
							j1.put("DataExpiracao", date != null ? JSONObject.NULL : date);
							
			            	j1.put("LoginWindows", entry.getString("erloginWindows"));
			            	j1.put("IndicativoUsuarioEstrangeiro", entry.getString("erindicativoUsuarioEstrangeiro"));
			            	j1.put("CodigoCargo", entry.getString("ercodigoCargo"));
			            	j1.put("CodigoEmpresaContratante", entry.getString("ercodigoEmpresaContratante"));
			            	j1.put("CodigoSetor", entry.getString("ercodigoSetor"));
							
							jsonResult = sendPut("/alterausuario", j1.toString());
						}
					}
					if (jsonResult.has("Error")) {
						throw new Exception(String.valueOf(jsonResult.getString("Error")));
					}
					
					break;
				default:
					logerror("Invalid entry type");
					throw new Exception("Invalid entry type");
			}
		}
		catch (Exception e) {
			logerror("Exception modifyUser: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * Required for CallReply mode. It is called once on each AssemblyLine iteration when the Connector is used in CallReply mode
	 */
	public Entry queryReply(Entry entry) {
		return null;
	}
	
	/**
	 * Metodo para obter o status no IGI
	 * @return codigo do status
	 * @param eruid: ID da conta (CPF)
	 */
	private String getStatus(String eruid) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String ret = "";
		
		try {
			conn = Util.getConnection("com.ibm.db2.jcc.DB2Driver", jdbcurl, jdbcuser, jdbcpassword);
			
			String query = "SELECT pwd.ATTR2 "
					+ "FROM igacore.user_erc ue, igacore.PWDCFG pcfg, igacore.PWDMANAGEMENT pwd, igacore.person p "
					+ "WHERE ue.id = p.USER_ERC "
					+ "AND p.id = pwd.PERSON "
					+ "AND pwd.pwdcfg = pcfg.id "
					+ "AND pcfg.name = 'AS' "
					+ "AND pwd.code = '" + eruid + "'";
			
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				ret = rs.getString("ATTR2").substring(0, 1);
			}
			
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			logerror("Exception " + ExceptionUtils.getStackTrace(e));
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
	 * Metodo para envio dos dados da conta (criacao)
	 * @return codigo HTTP do servico
	 * @return 200 para sucesso
	 * @param context: contexto para busca
	 * @param content: corpo da requisicao
	 */
	@SuppressWarnings("resource")
	private JSONObject sendPost(String context, String content) throws Exception {
		JSONObject jsonResult = new JSONObject();
		
		try {
			URL path = new URL(url + context);
			
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(content);
			writer.flush();
			writer.close();
			
			logdebug("Body: " + httpConn.getOutputStream().toString());
			
			httpConn.getOutputStream().close();
			
			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			int responseCode = httpConn.getResponseCode();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			
			if (responseCode >= 200 && responseCode <= 299) {
				jsonResult.put("Success", "OK");
			} else {
				String error = s.hasNext() ? s.next() : "";
				logerror(error);
				
				if (error.contains("ativos com o mesmo CPF")) {
					jsonResult = sendPut("/alterausuario", content);
				} else {
					jsonResult.put("Error", error);
				}
			}
		}
		catch (Exception e) {
			logerror("Exception sendPost: " + ExceptionUtils.getStackTrace(e));
			jsonResult.put("Error", e.getMessage());
	    }
		
		return jsonResult;
	}
	
	/**
	 * Metodo para envio dos dados da conta (atualizacao)
	 * @return codigo HTTP do servico
	 * @return 204 para sucesso
	 * @param context: contexto para busca
	 * @param content: corpo da requisicao
	 */
	@SuppressWarnings("resource")
	private JSONObject sendPut(String context, String content) throws Exception {
		JSONObject jsonResult = new JSONObject();
		
		try {
			URL path = new URL(url + context);
			
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("PUT");
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(content);
			writer.flush();
			writer.close();
			
			loginfo("Body: " + httpConn.getOutputStream().toString());
			
			httpConn.getOutputStream().close();
			
			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			int responseCode = httpConn.getResponseCode();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			
			loginfo("sendPut " + responseCode);
			
			if (responseCode >= 200 && responseCode <= 299) {
				jsonResult.put("Success", "OK");
			} else {
				String error = s.hasNext() ? s.next() : "";
				logerror(error);
				jsonResult.put("Error", error);
			}
		}
		catch (Exception e) {
			logerror("Exception sendPut: " + ExceptionUtils.getStackTrace(e));
			jsonResult.put("Error", e.getMessage());
	    }
		
		return jsonResult;
	}
	
	/**
	 * Metodo para busca de contas ou direitos
	 * @return JSONObject com os dados
	 * @param context: contexto para busca
	 * @param content: corpo da requisicao
	 */
	private JSONArray sendGet(String context, String content) throws Exception {
		JSONArray jsonResult = new JSONArray();
		
		try {
			URL u = new URL(url + context);
			HttpURLConnection httpConn = (HttpURLConnection) u.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			httpConn.setRequestProperty("Accept", "application/json");
			httpConn.setUseCaches(false);
			httpConn.setAllowUserInteraction(false);
			httpConn.connect();
	        
	        int responseCode = httpConn.getResponseCode();
	        
	        if (responseCode >= 200 && responseCode <= 299) {
	        	BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                
                br.close();
                jsonResult = new JSONArray(sb.toString());
	        }
		}
		catch (Exception e) {
			JSONObject tmp = new JSONObject();
			jsonResult.put(tmp.put("Error", e.getMessage()));
			
			logerror("Exception sendGet: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return jsonResult;
	}
	
	/**
	 * Metodo para verificar se a conta existe
	 * @return JSONObject com os dados
	 * @param context: contexto para busca
	 * @param content: corpo da requisicao
	 */
	private boolean searchAccouunt(String context, String content) throws Exception {
		JSONArray jsonResult = new JSONArray();
		boolean exist = false;
		
		try {
			URL u = new URL(url + context);
			HttpURLConnection httpConn = (HttpURLConnection) u.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			httpConn.setRequestProperty("Accept", "application/json");
			httpConn.setUseCaches(false);
			httpConn.setAllowUserInteraction(false);
			httpConn.connect();
	        
	        int responseCode = httpConn.getResponseCode();
	        
	        if (responseCode >= 200 && responseCode <= 299) {
	        	exist = true;
	        }
		}
		catch (Exception e) {
			JSONObject tmp = new JSONObject();
			jsonResult.put(tmp.put("Error", e.getMessage()));
			
			logerror("Exception searchAccouunt: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return exist;
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
			post.addHeader("Content-Type", "application/json");
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
					loginfo("Result: " + jsonResult.toString());
					token = jsonResult.getString("access_token");
				}
	        }
		}
		catch (Exception e) {
			logerror("Exception getToken: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return token;
	}
	
	/**
	 * Metodo para obter o ID dos sistemas
	 * Nao utilizado neste versao do conector
	 */
	@SuppressWarnings("unused")
	private String[] getSistemas() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<String> listOfStrings = new ArrayList<String>();
		String[] array = null;
		
		try {
			conn = Util.getConnection("com.ibm.db2.jcc.DB2Driver", jdbcurl, jdbcuser, jdbcpassword);
			
			stmt = conn.prepareStatement("SELECT NAME FROM IGITOOLS.SA_SISTEMAS");
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				listOfStrings.add(rs.getString("NAME"));
			}
			
			array = listOfStrings.toArray(new String[0]);
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			logerror("Exception getSistemas: " + ExceptionUtils.getStackTrace(e));
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
		
		return array;
	}
	
	/**
	 * Metodo para testar conexao com o sistema
	 */
	private String testService() throws Exception {
		String ret  = "site_up";
		
		try {
			URL path = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) path.openConnection();
			conn.setRequestMethod("GET");
			
			loginfo(String.format("Fetching %s ...", url));
			
			int responseCode = conn.getResponseCode();
			
			if (responseCode == 200) {
				loginfo(String.format("Site is up, content length = %s", conn.getHeaderField("content-length")));
			} else {
				loginfo(String.format("Site is up, but returns non-ok status = %d", responseCode));
			}
		}
		catch (java.net.UnknownHostException e) {
			ret = "site_down";
			logerror("Site is down");
		}
		
		return ret;
	}
    
	/**
	 * Metodo para obter o ID dos sistemas
	 * Nao utilizado neste versao do conector
	 * @param path: caminho do arquivo
	 */
	private String[] readFile(String path) throws Exception {
		String[] array = null;
		
		try {
			if (path.equals("")) {
				logerror("File reading failed, path is null, using path '/opt/IBM/TDI/V7.2/sistemas.txt'");
				path = "/opt/IBM/TDI/V7.2/sistemas.txt";
            }
			
			List<String> listOfStrings = new ArrayList<String>();
		    BufferedReader bf = new BufferedReader(new FileReader(path));
		    
		    String line = bf.readLine();
		    
		    while (line != null) {
		        listOfStrings.add(line);
		        line = bf.readLine();
		    }
		    
		    bf.close();
		    
		    array = listOfStrings.toArray(new String[0]);
		}
		catch (Exception e) {
			logmsg("Fail readPrivateKey: " + ExceptionUtils.getStackTrace(e));
		}
		
		return array;
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
