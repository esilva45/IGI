package br.com.asyg.connector.bmg.fasap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sf.SAMLAssertionGen.SAMLAssertionGenerator;

import br.com.asyg.connector.bmg.fasap.model.CustWorkForceSoftwareVO;
import br.com.asyg.connector.bmg.fasap.model.DadosConexaoVO;
import br.com.asyg.connector.bmg.fasap.model.EmpEmploymentVO;
import br.com.asyg.connector.bmg.fasap.model.EmpJobVO;
import br.com.asyg.connector.bmg.fasap.model.EmploymentNavVO;
import br.com.asyg.connector.bmg.fasap.model.PerPersonVO;
import br.com.asyg.connector.bmg.fasap.model.PerPersonalVO;

@SuppressWarnings({ "deprecation" })
public class PreFeedSAP {
	private static Logger logger = Logger.getLogger("FASAPConnector");
	private DadosConexaoVO dadosConexaoVO;
	private Util utilsSAP = new Util();
	public boolean DEBUG = false;
	
	public PreFeedSAP() { }
	
	public PreFeedSAP(DadosConexaoVO dadosConexaoVO) {
		this.dadosConexaoVO = dadosConexaoVO;
	}
	
	public String gerarToken() throws Exception {
		String token = "";
		String resultado = "";
		JSONObject jsonObj = null;
		
		try {
			String assertion = SAMLAssertionGenerator.getSAMLAssertion(dadosConexaoVO.getUrlSAP() + "/oauth/token",
					dadosConexaoVO.getClientIdSAP(), dadosConexaoVO.getUserIdSAP(), dadosConexaoVO.getUserIdSAP(),
					dadosConexaoVO.getPrivateKeySAP());
			
			dadosConexaoVO.setAssertion(assertion);
			
			String url = dadosConexaoVO.getUrlSAP() + "/oauth/token";
			
			HttpClient client = this.createTlsV2HttpClient();
			HttpPost request = new HttpPost(url);
			
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("client_id", dadosConexaoVO.getClientIdSAP()));
			nvps.add(new BasicNameValuePair("grant_type", dadosConexaoVO.getGrantTypeSAP()));
			nvps.add(new BasicNameValuePair("company_id", dadosConexaoVO.getCompanyIdSAP()));
			nvps.add(new BasicNameValuePair("assertion", dadosConexaoVO.getAssertion()));
			
			request.setEntity(new UrlEncodedFormEntity(nvps, dadosConexaoVO.getCodificacaoJSON()));
			HttpResponse response = client.execute(request);
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), dadosConexaoVO.getCodificacaoJSON()));
			String line = "";
			
			while ((line = rd.readLine()) != null) {
				resultado = resultado + line;
			}
			
			jsonObj = new JSONObject(resultado);
			
			if (!jsonObj.isNull("access_token")) {
				token = String.valueOf(jsonObj.get("access_token"));
			} else {
				token = "erro";
				throw new Exception(String.valueOf(jsonObj.get("errorMessage")));
			}
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception gerarToken: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
		
		return token;
	}
	
	public JSONObject listarUrl(String urlString) throws Exception {
		JSONObject jsonObj = null;
		
		try {
			HttpGet var = new HttpGet(urlString);
			var.addHeader("Accept", "application/json");
			var.addHeader("Content-Type", "application/json; charset=UTF-8");
			var.addHeader("Authorization", "Bearer " + dadosConexaoVO.getTokenSAP());
			
			try (CloseableHttpClient client = HttpClientBuilder.create()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
				.build();
				CloseableHttpResponse response = client.execute(var)) {
				
				int responseCode = response.getStatusLine().getStatusCode();
	        	
	        	if (responseCode == 200) {
	        		jsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
				} else {
					logger.error("[PreFeedSAP] listarUrl: " + response.getStatusLine().toString() + ":" + EntityUtils.toString(response.getEntity()));
				}
	        	
				return jsonObj;
			}
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception listarUrl: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
	}
	
	public List<CustWorkForceSoftwareVO> listCustWorkForceSoftware() throws Exception {
		List<CustWorkForceSoftwareVO> custWorkForceSoftwareVOList = new ArrayList<>();
		String tipoPaginacao = "paging=snapshot&customPageSize";
		String filtro = "";
		JSONObject jsonObj = null;
		
		try {
			String urlString = dadosConexaoVO.getUrlSAP()
					+ "/odata/v2/cust_WorkForceSoftware?$format=json&%24expand=externalNameNav,cust_AbsenceTypeNav&%24select=externalName,cust_EndAbsence,cust_StartAbsence,cust_AbsenceType,"
					+ "cust_AbsencesDays2,lastModifiedDateTime,cust_AbsenceTypeNav%2Flabel_pt_BR,mdfSystemStatus&"
					+ tipoPaginacao + "=" + dadosConexaoVO.getPagination() + filtro + "&$format=json"
					+ utilsSAP.fromDate();
			
			HttpGet var = new HttpGet(urlString);
			var.addHeader("Accept", "application/json");
			var.addHeader("Content-Type", "application/json; charset=UTF-8");
			var.addHeader("Authorization", "Bearer " + dadosConexaoVO.getTokenSAP());
			
			try (CloseableHttpClient client = HttpClientBuilder.create()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
				.build();
				CloseableHttpResponse response = client.execute(var)) {
				
				int responseCode = response.getStatusLine().getStatusCode();
	        	
	        	if (responseCode == 200) {
	        		jsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
				} else {
					logger.error("[PreFeedSAP] listCustWorkForceSoftware: " + response.getStatusLine().toString() + ":" + EntityUtils.toString(response.getEntity()));
				}
	        	
	        	if (!jsonObj.isNull("d")) {
					custWorkForceSoftwareVOList.addAll(this.returnListJSONCustWorkForceSoftware(jsonObj));
				} else {
					throw new Exception(String.valueOf(jsonObj.get("errorMessage")));
				}
				
				if ("S".equals(dadosConexaoVO.getAllowMsg())) {
					logger.debug("[PreFeedSAP] Total de lista custWorkForceSoftware: " + custWorkForceSoftwareVOList.size());
				}
				
				return custWorkForceSoftwareVOList;
			}
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception listCustWorkForceSoftware: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
	}
	
	public List<CustWorkForceSoftwareVO> returnListJSONCustWorkForceSoftware(JSONObject jsonObj) throws Exception {
		List<CustWorkForceSoftwareVO> custWorkForceSoftwareVOList = new ArrayList<>();
		String campo = "Campo não mapeado";
		
		try {
			String next = null;
			
			if (!jsonObj.getJSONObject("d").isNull("__next")) {
				next = jsonObj.getJSONObject("d").get("__next").toString();
			}
			
			if (!jsonObj.getJSONObject("d").isNull("results")) {
				JSONArray resultadoArray = jsonObj.getJSONObject("d").getJSONArray("results");
				
				for (int i = 0; i < resultadoArray.length(); i++) {
					CustWorkForceSoftwareVO custWorkForceSoftwareVO = new CustWorkForceSoftwareVO();
					
					if (resultadoArray.getJSONObject(i).has("externalName")) {
						if (!resultadoArray.getJSONObject(i).isNull("externalName")) {
							campo = "externalName";
							custWorkForceSoftwareVO.setExternalName(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("externalName")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("endDate")) {
						if (!resultadoArray.getJSONObject(i).isNull("lastModifiedDateTime")) {
							campo = "lastModifiedDateTime";
							custWorkForceSoftwareVO.setLastModDate(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getString("lastModifiedDateTime")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("cust_AbsencesDays2")) {
						if (!resultadoArray.getJSONObject(i).isNull("cust_AbsencesDays2")) {
							campo = "cust_AbsencesDays2";
							custWorkForceSoftwareVO.setAbsencesDays(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("cust_AbsencesDays2")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("cust_AbsenceType")) {
						if (!resultadoArray.getJSONObject(i).isNull("cust_AbsenceType")) {
							campo = "cust_AbsenceType";
							custWorkForceSoftwareVO.setAbsenceType(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("cust_AbsenceType")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("cust_EndAbsence")) {
						if (!resultadoArray.getJSONObject(i).isNull("cust_EndAbsence")) {
							campo = "cust_EndAbsence";
							custWorkForceSoftwareVO.setDateEnd(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getString("cust_EndAbsence")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("cust_StartAbsence")) {
						if (!resultadoArray.getJSONObject(i).isNull("cust_StartAbsence")) {
							campo = "cust_StartAbsence";
							custWorkForceSoftwareVO.setDateStart(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getString("cust_StartAbsence")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("mdfSystemStatus")) {
						if (!resultadoArray.getJSONObject(i).isNull("mdfSystemStatus")) {
							campo = "mdfSystemStatus";
							custWorkForceSoftwareVO.setStatus(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("mdfSystemStatus")));
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("cust_AbsenceTypeNav")) {
						if (!resultadoArray.getJSONObject(i).getJSONObject("cust_AbsenceTypeNav").isNull("label_pt_BR")) {
							campo = "cust_AbsenceTypeNav/label_pt_BR";
							
							if ("S".equals(dadosConexaoVO.getAllowMsg())) {
								logger.debug("[PreFeedSAP] CustWorkForceSoftwareVO Matricula: " + custWorkForceSoftwareVO.getExternalName());
								logger.debug("[PreFeedSAP] CustWorkForceSoftwareVO Tipo afastamento: " + resultadoArray.getJSONObject(i).getJSONObject("cust_AbsenceTypeNav").getString("label_pt_BR"));
							}
							
							custWorkForceSoftwareVO.setAbsenceTypeDescription(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("cust_AbsenceTypeNav").getString("label_pt_BR")));
						}
					}
					
					custWorkForceSoftwareVOList.add(custWorkForceSoftwareVO);
				}
				
				if (next != null) {
					if ("S".equals(dadosConexaoVO.getAllowMsg())) {
						logger.debug("[PreFeedSAP] Tamanho lista custWorkForceSoftwareVOList: " + custWorkForceSoftwareVOList.size());
					}
					custWorkForceSoftwareVOList.addAll(this.returnListJSONCustWorkForceSoftware(this.listarUrl(next)));
				}
			}
			
			return custWorkForceSoftwareVOList;
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception returnListJSONCustWorkForceSoftware: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(campo + ": " + e.toString());
		}
	}
	
	public List<PerPersonalVO> listPerPersonal() throws Exception {
		List<PerPersonalVO> perPersonalVOList = new ArrayList<>();
		String tipoPaginacao = "paging=snapshot&customPageSize";
		String filtro = "";
		JSONObject jsonObj = null;
		
		try {
			String urlString = dadosConexaoVO.getUrlSAP()
					+ "/odata/v2/PerPersonal/?%24format=json&%24expand=personNav,personNav%2FemploymentNav,personNav%2FemploymentNav%2FjobInfoNav&"
					+ tipoPaginacao + "=" + dadosConexaoVO.getPagination() + filtro + utilsSAP.fromDate();
			
			HttpGet var = new HttpGet(urlString);
			var.addHeader("Accept", "application/json");
			var.addHeader("Content-Type", "application/json; charset=UTF-8");
			var.addHeader("Authorization", "Bearer " + dadosConexaoVO.getTokenSAP());
			
			try (CloseableHttpClient client = HttpClientBuilder.create()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
				.build();
				CloseableHttpResponse response = client.execute(var)) {
				
				int responseCode = response.getStatusLine().getStatusCode();
	        	
	        	if (responseCode == 200) {
	        		jsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
				} else {
					logger.error("[PreFeedSAP] listPerPersonal: " + response.getStatusLine().toString() + ":" + EntityUtils.toString(response.getEntity()));
				}
	        	
	        	if (!jsonObj.isNull("d")) {
					perPersonalVOList.addAll(this.returnListJSONPerPersonal(jsonObj));
				} else {
					throw new Exception(String.valueOf(jsonObj.get("errorMessage")));
				}
				
				if ("S".equals(dadosConexaoVO.getAllowMsg())) {
					logger.debug("[PreFeedSAP] Total de lista PerPersonal: " + perPersonalVOList.size());
				}
				
				return perPersonalVOList;
			}
		}
		catch (Exception e) {
			logger.error("Exception listPerPersonal: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
	}
	
	public List<PerPersonalVO> returnListJSONPerPersonal(JSONObject jsonObj) throws Exception {
		List<PerPersonalVO> perPersonalVOList = new ArrayList<>();
		String campo = "Campo não mapeado";
		
		try {
			String next = null;
			
			if (!jsonObj.getJSONObject("d").isNull("__next")) {
				next = jsonObj.getJSONObject("d").get("__next").toString();
			}
			
			if (!jsonObj.getJSONObject("d").isNull("results")) {
				JSONArray resultadoArray = jsonObj.getJSONObject("d").getJSONArray("results");
				
				for (int i = 0; i < resultadoArray.length(); i++) {
					PerPersonalVO perPersonalVO = new PerPersonalVO();
					
					if (resultadoArray.getJSONObject(i).has("personIdExternal")) {
						if (!resultadoArray.getJSONObject(i).isNull("personIdExternal")) {
							campo = "personIdExternal";
							perPersonalVO.setPersonIdExternal(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("personIdExternal")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("firstName")) {
						if (!resultadoArray.getJSONObject(i).isNull("firstName")) {
							campo = "firstName";
							perPersonalVO.setFirstName(utilsSAP.returnStringFromJson(utilsSAP.formatName(resultadoArray.getJSONObject(i).getString("firstName"))));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("preferredName")) {
						if (!resultadoArray.getJSONObject(i).isNull("preferredName")) {
							campo = "preferredName";
							perPersonalVO.setPreferedName(utilsSAP.returnStringFromJson(utilsSAP.formatName(resultadoArray.getJSONObject(i).getString("preferredName"))));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("lastName")) {
						if (!resultadoArray.getJSONObject(i).isNull("lastName")) {
							campo = "lastName";
							perPersonalVO.setLastName(utilsSAP.returnStringFromJson(utilsSAP.formatName(resultadoArray.getJSONObject(i).getString("lastName"))));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("gender")) {
						if (!resultadoArray.getJSONObject(i).isNull("gender")) {
							campo = "gender";
							perPersonalVO.setGender(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("gender")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("endDate")) {
						if (!resultadoArray.getJSONObject(i).isNull("endDate")) {
							campo = "endDate";
							perPersonalVO.setEndDate(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getString("endDate")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("startDate")) {
						if (!resultadoArray.getJSONObject(i).isNull("startDate")) {
							campo = "startDate";
							perPersonalVO.setStartDate(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getString("startDate")));
						}
					}
					
					List<EmploymentNavVO> employmentNavs = new ArrayList<>();
					List<EmploymentNavVO> employmentNavAtivos = new ArrayList<>();
					int countAtivos = 0;
					
					if (!resultadoArray.getJSONObject(i).isNull("personNav")) {
						if (!resultadoArray.getJSONObject(i).getJSONObject("personNav").isNull("employmentNav")) {
							campo = "employmentNav";
							JSONArray documentosArray = resultadoArray.getJSONObject(i).getJSONObject("personNav").getJSONObject("employmentNav").getJSONArray("results");
							
							for (int j = 0; j < documentosArray.length(); j++) {
								EmploymentNavVO employmentNavVO = new EmploymentNavVO();
								
								if (documentosArray.getJSONObject(j).has("personIdExternal")) {
									if (!documentosArray.getJSONObject(j).isNull("personIdExternal")) {
										campo = "employmentNav/personIdExternal";
										perPersonalVO.setPersonIdExternal(utilsSAP.returnStringFromJson(documentosArray.getJSONObject(j).getString("personIdExternal")));
									}
								}
								
								if (documentosArray.getJSONObject(j).has("customString22")) {
									if (!documentosArray.getJSONObject(j).isNull("customString22")) {
										campo = "employmentNav/customString22";
										employmentNavVO.setValidPerPersonal(utilsSAP.returnStringFromJson(documentosArray.getJSONObject(j).getString("customString22")));
									}
								}
								
								if (documentosArray.getJSONObject(j).has("startDate")) {
									if (!documentosArray.getJSONObject(j).isNull("startDate")) {
										campo = "employmentNav/startDate";
										employmentNavVO.setStartDate(utilsSAP.formatDateJson(documentosArray.getJSONObject(j).getString("startDate")));
									}
								}
								
								if (documentosArray.getJSONObject(j).has("endDate")) {
									if (!documentosArray.getJSONObject(j).isNull("endDate")) {
										campo = "employmentNav/endDate";
										
										try {
											employmentNavVO.setEndDate(utilsSAP.formatDateJson(documentosArray.getJSONObject(j).getString("endDate")));
										}
										catch (Exception ex) {
											logger.error("[PreFeedSAP] Exception returnListJSONPerPersonal: " + ExceptionUtils.getStackTrace(ex));
											logger.error("[PreFeedSAP] returnListJSONPerPersonal Erro no endDate: " + employmentNavVO.getPerPersonal());
										}
									} else {
										employmentNavAtivos.add(employmentNavVO);
										countAtivos++;
									}
								}
								
								employmentNavs.add(employmentNavVO);
							}
							
							if (!employmentNavs.isEmpty()) {
								if (countAtivos > 1) {
									perPersonalVO.setValidPersonalId(utilsSAP.validActivePerPersonalId(employmentNavs));
								} else {
									perPersonalVO.setValidPersonalId(utilsSAP.validPerPersonalId(employmentNavs));
								}
							}
						}
					}
					
					perPersonalVOList.add(perPersonalVO);
				}
				
				if (next != null) {
					if ("S".equals(dadosConexaoVO.getAllowMsg())) {
						logger.debug("[PreFeedSAP] Tamanho lista perPersonalVOList: " + perPersonalVOList.size());
					}
					
					perPersonalVOList.addAll(this.returnListJSONPerPersonal(this.listarUrl(next)));
				}
			}
			
			return perPersonalVOList;
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception returnListJSONPerPersonal: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(campo + ": " + e.toString());
		}
	}
	
	public List<PerPersonVO> listPerPerson() throws Exception {
		List<PerPersonVO> perPersonVOList = new ArrayList<>();
		String tipoPaginacao = "paging=snapshot&customPageSize";
		//String tipoPaginacao = "customPageSize";
		String filtro = "&$filter=phoneNav%2FisPrimary%20eq%20true";
		JSONObject jsonObj = null;
		
		try {
			String urlString = dadosConexaoVO.getUrlSAP()
				+ "/odata/v2/PerPerson?%24format=json&%24select=personIdExternal,phoneNav%2FlastModifiedOn,phoneNav%2FareaCode,phoneNav%2FphoneNumber&%24expand=phoneNav"
				+ utilsSAP.fromDate() + filtro + "&" + tipoPaginacao + "=" + dadosConexaoVO.getPagination();
			
			HttpGet var = new HttpGet(urlString);
			var.addHeader("Accept", "application/json");
			var.addHeader("Content-Type", "application/json; charset=UTF-8");
			var.addHeader("Authorization", "Bearer " + dadosConexaoVO.getTokenSAP());
			
			try (CloseableHttpClient client = HttpClientBuilder.create()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
				.build();
				CloseableHttpResponse response = client.execute(var)) {
				
				int responseCode = response.getStatusLine().getStatusCode();
	        	
	        	if (responseCode == 200) {
	        		jsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
				} else {
					logger.error("[PreFeedSAP] listPerPerson: " + response.getStatusLine().toString() + ":" + EntityUtils.toString(response.getEntity()));
				}
	        	
	        	if (!jsonObj.isNull("d")) {
					perPersonVOList.addAll(this.returnListJSONPerPerson(jsonObj));
				} else {
					throw new Exception(String.valueOf(jsonObj.get("errorMessage")));
				}
				
				if ("S".equals(dadosConexaoVO.getAllowMsg())) {
					logger.debug("[PreFeedSAP] Total de lista PerPerson: " + perPersonVOList.size());
				}
				
				return perPersonVOList;
			}
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception listPerPerson: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
	}
	
	public List<PerPersonVO> returnListJSONPerPerson(JSONObject jsonObj) throws Exception {
		List<PerPersonVO> perPersonVOList = new ArrayList<>();
		String campo = "Campo não mapeado", phone = "";
		
		try {
			String next = null;
			
			if (!jsonObj.getJSONObject("d").isNull("__next")) {
				next = jsonObj.getJSONObject("d").get("__next").toString();
			}
			
			if (!jsonObj.getJSONObject("d").isNull("results")) {
				JSONArray resultadoArray = jsonObj.getJSONObject("d").getJSONArray("results");

				for (int i = 0; i < resultadoArray.length(); i++) {
					PerPersonVO perPersonVO = new PerPersonVO();
					
					if (resultadoArray.getJSONObject(i).has("personIdExternal")) {
						if (!resultadoArray.getJSONObject(i).isNull("personIdExternal")) {
							campo = "personIdExternal";
							perPersonVO.setPersonId(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("personIdExternal")));
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("phoneNav")) {
						JSONArray documentosArray = resultadoArray.getJSONObject(i).getJSONObject("phoneNav").getJSONArray("results");
						
						for (int j = 0; j < documentosArray.length(); j++) {
							if (documentosArray.getJSONObject(j).has("areaCode")) {
								if (!documentosArray.getJSONObject(j).isNull("areaCode")) {
									campo = "phoneNav/areaCode";
									perPersonVO.setAreaCode(utilsSAP.returnStringFromJson(documentosArray.getJSONObject(j).getString("areaCode")));
								}
							}
							
							if (documentosArray.getJSONObject(j).has("lastModifiedOn")) {
								if (!documentosArray.getJSONObject(j).isNull("lastModifiedOn")) {
									campo = "phoneNav/lastModifiedOn";
									perPersonVO.setDtAlteracao(utilsSAP.formatDateJson(documentosArray.getJSONObject(j).getString("lastModifiedOn")));
								}
							}
							
							if (documentosArray.getJSONObject(j).has("phoneNumber")) {
								if (!documentosArray.getJSONObject(j).isNull("phoneNumber")) {
									campo = "phoneNav/phoneNumber";
									phone = utilsSAP.returnStringFromJson(documentosArray.getJSONObject(j).getString("phoneNumber"));
									perPersonVO.setPhoneNumber(phone.replaceAll("-", "").replaceAll(" ", "").trim());
								}
							}
						}
					}
					
					perPersonVOList.add(perPersonVO);
				}
				
				if (next != null) {
					if ("S".equals(dadosConexaoVO.getAllowMsg())) {
						System.out.println("[PreFeedSAP] Tamanho lista perPersonVOList: " + perPersonVOList.size());
					}
					
					perPersonVOList.addAll(this.returnListJSONPerPerson(this.listarUrl(next)));
				}
			}
			
			return perPersonVOList;
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception returnListJSONPerPerson: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(campo + ": " + e.toString());
		}
	}
	
	public List<EmpEmploymentVO> listEmpEmployment() throws Exception {
		List<EmpEmploymentVO> empEmploymentVOList = new ArrayList<>();
		String tipoPaginacao = "paging=snapshot&customPageSize";
		//String tipoPaginacao = "customPageSize";
		String filtro = "&$filter=endDate%20eq%20null";
		JSONObject jsonObj = null;
		
		try {
			String urlString = dadosConexaoVO.getUrlSAP()
					+ "/odata/v2/EmpEmployment?$format=json&"
					+ "$expand=jobInfoNav%2FjobCodeNav%2Fcust_JobLevelNav&$select=personIdExternal,jobInfoNav%2FjobCodeNav%2FlastModifiedOn,jobInfoNav%2FjobCodeNav%2Fcust_JobLevelNav%2Flabel_defaultValue"
					+ utilsSAP.fromDate() + filtro + "&" + tipoPaginacao + "=" + dadosConexaoVO.getPagination();
			
			HttpGet var = new HttpGet(urlString);
			var.addHeader("Accept", "application/json");
			var.addHeader("Content-Type", "application/json; charset=UTF-8");
			var.addHeader("Authorization", "Bearer " + dadosConexaoVO.getTokenSAP());
			
			try (CloseableHttpClient client = HttpClientBuilder.create()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
				.build();
				CloseableHttpResponse response = client.execute(var)) {
				
				int responseCode = response.getStatusLine().getStatusCode();
	        	
	        	if (responseCode == 200) {
	        		jsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
				} else {
					logger.error("[PreFeedSAP] listEmpEmployment: " + response.getStatusLine().toString() + ":" + EntityUtils.toString(response.getEntity()));
				}
	        	
	        	if (!jsonObj.isNull("d")) {
	        		empEmploymentVOList.addAll(this.returnListJSONEmpEmploymentn(jsonObj));
				} else {
					throw new Exception(String.valueOf(jsonObj.get("errorMessage")));
				}
				
				if ("S".equals(dadosConexaoVO.getAllowMsg())) {
					logger.debug("[PreFeedSAP] Total de lista EmpEmployment: " + empEmploymentVOList.size());
				}
				
				return empEmploymentVOList;
			}
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception listEmpEmployment: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
	}
	
	public List<EmpEmploymentVO> returnListJSONEmpEmploymentn(JSONObject jsonObj) throws Exception {
		List<EmpEmploymentVO> empEmploymentVOList = new ArrayList<>();
		String campo = "Campo não mapeado";
		
		try {
			String next = null;
			
			if (!jsonObj.getJSONObject("d").isNull("__next")) {
				next = jsonObj.getJSONObject("d").get("__next").toString();
			}
			
			if (!jsonObj.getJSONObject("d").isNull("results")) {
				JSONArray resultadoArray = jsonObj.getJSONObject("d").getJSONArray("results");
				
				for (int i = 0; i < resultadoArray.length(); i++) {
					EmpEmploymentVO empEmploymentVO = new EmpEmploymentVO();
					
					if (resultadoArray.getJSONObject(i).has("personIdExternal")) {
						if (!resultadoArray.getJSONObject(i).isNull("personIdExternal")) {
							campo = "personIdExternal";
							empEmploymentVO.setPersonId(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("personIdExternal")));
						}
					}
					
					if (!resultadoArray.getJSONObject(i).getJSONObject("jobInfoNav").isNull("results")) {
						JSONArray documentosArray = resultadoArray.getJSONObject(i).getJSONObject("jobInfoNav").getJSONArray("results");
						
						for (int j = 0; j < documentosArray.length(); j++) {
							if (documentosArray.getJSONObject(j).has("jobCodeNav")) {
								if (!documentosArray.getJSONObject(j).isNull("jobCodeNav")) {
									campo = "jobCodeNav/cust_JobLevelNav";
									empEmploymentVO.setNivelCargo(utilsSAP.returnStringFromJson(documentosArray.getJSONObject(j).getJSONObject("jobCodeNav").getJSONObject("cust_JobLevelNav").getString("label_defaultValue")));
									
									campo = "jobCodeNav/lastModifiedOn";
									empEmploymentVO.setDtAlteracao(utilsSAP.formatDateJson(documentosArray.getJSONObject(j).getJSONObject("jobCodeNav").getString("lastModifiedOn")));
								}
							}
						}
					}
					
					if ("S".equals(dadosConexaoVO.getAllowMsg())) {
						logger.debug("[PreFeedSAP] returnListJSONEmpEmploymentn NivelCargo [" + empEmploymentVO.getNivelCargo() + "] DtAlteracao [" + empEmploymentVO.getDtAlteracao() + "]");
					}
					
					empEmploymentVOList.add(empEmploymentVO);
				}
				
				if (next != null) {
					if ("S".equals(dadosConexaoVO.getAllowMsg())) {
						logger.debug("[PreFeedSAP] Tamanho lista empEmploymentVOList: " + empEmploymentVOList.size());
					}
					
					empEmploymentVOList.addAll(this.returnListJSONEmpEmploymentn(this.listarUrl(next)));
				}
			}
			
			return empEmploymentVOList;
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception returnListJSONEmpEmploymentn: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(campo + ": " + e.toString());
		}
	}
	
	public List<EmpJobVO> listEmpJob() throws Exception {
		List<EmpJobVO> empJobVOList = new ArrayList<>();
		String tipoPaginacao = "paging=snapshot&customPageSize";
		//String tipoPaginacao = "customPageSize";
		String filtro = "";
		JSONObject jsonObj = null;
		
		try {
			String urlString = dadosConexaoVO.getUrlSAP()
					+ "/odata/v2/EmpJob?%24format=json&%24expand=costCenterNav,employmentTypeNav%2FpicklistLabels,employeeClassNav%2FpicklistLabels,emplStatusNav%2FpicklistLabels,employmentNav%2F"
					+ "personNav%2FnationalIdNav,employmentNav%2FempWorkPermitNav%2FdocumentTypeNav,employmentNav%2FjobInfoNav%2FcostCenterNav,employmentNav%2FjobInfoNav%2FlocationNav,employmentNav%2F"
					+ "jobInfoNav%2FcostCenterNav,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2FcityNav%2FpicklistLabels,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2F"
					+ "countryNav%2FpicklistLabels,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2FstateNav%2FpicklistLabels,employmentNav%2FjobInfoNav%2FcompanyNav,userNav%2F"
					+ "personKeyNav,eventReasonNav,customString4Nav,managerEmploymentNav,companyNav,locationNav,departmentNav,jobCodeNav&%24select=userId,eventReason,emplStatus,lastModifiedOn,"
					+ "customString4,location,startDate,endDate,company,employeeClass,employmentType,costCenter,position,jobCode,department,employmentNav%2FcustomString22,employmentNav%2F"
					+ "endDate,employmentNav%2FstartDate,userNav%2FdateOfBirth,userNav%2Fusername,userNav%2FpersonKeyNav%2FpersonIdExternal,eventReasonNav%2Fevent,eventReasonNav%2F"
					+ "name,employmentNav%2FjobInfoNav%2FcostCenterNav%2FexternalCode,employmentNav%2FjobInfoNav%2FcostCenterNav%2Fdescription_defaultValue,employmentNav%2FjobInfoNav%2F"
					+ "locationNav%2FexternalCode,employmentNav%2FjobInfoNav%2FlocationNav%2Fname,employmentNav%2FjobInfoNav%2FcompanyNav%2FexternalCode,employmentNav%2FjobInfoNav%2FcompanyNav%2F"
					+ "description,employmentNav%2FjobInfoNav%2FcompanyNav%2Fname,customString4Nav%2FexternalCode,customString4Nav%2Fname,employmentNav%2FpersonNav%2FnationalIdNav%2FnationalId,"
					+ "employmentNav%2FempWorkPermitNav%2FdocumentTypeNav%2FexternalCode,employmentNav%2FempWorkPermitNav%2FexpirationDate,employmentNav%2FempWorkPermitNav%2FissueDate,employmentNav%2F"
					+ "empWorkPermitNav%2FdocumentNumber,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2FcityNav%2FexternalCode,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2F"
					+ "cityNav%2FpicklistLabels%2Flabel,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2Faddress1,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2F"
					+ "address2,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2FcountryNav%2FpicklistLabels%2Flabel,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2F"
					+ "stateNav%2FpicklistLabels%2Flabel,employmentNav%2FjobInfoNav%2FlocationNav%2FaddressNavDEFLT%2FzipCode,managerEmploymentNav%2FcustomString22,companyNav%2FexternalCode,companyNav%2F"
					+ "name,departmentNav%2FexternalCode,departmentNav%2Fname,locationNav%2FexternalCode,locationNav%2Fname,locationNav%2FcustomString1,jobCodeNav%2FexternalCode,jobCodeNav%2Fname,jobCodeNav%2F"
					+ "description,emplStatusNav%2FpicklistLabels%2FoptionId,emplStatusNav%2FpicklistLabels%2Flocale,emplStatusNav%2FpicklistLabels%2Flabel,eventReasonNav%2Fname,employeeClassNav%2F"
					+ "picklistLabels%2FoptionId,employeeClassNav%2FpicklistLabels%2Flocale,employeeClassNav%2FpicklistLabels%2Flabel,employmentTypeNav%2FpicklistLabels%2FoptionId,employmentTypeNav%2F"
					+ "picklistLabels%2Flocale,employmentTypeNav%2FpicklistLabels%2Flabel,costCenterNav%2Fname"
					+ utilsSAP.fromDate() + filtro + "&" + tipoPaginacao + "=" + dadosConexaoVO.getPagination();
			
			HttpGet var = new HttpGet(urlString);
			var.addHeader("Accept", "application/json");
			var.addHeader("Content-Type", "application/json; charset=UTF-8");
			var.addHeader("Authorization", "Bearer " + dadosConexaoVO.getTokenSAP());
			
			try (CloseableHttpClient client = HttpClientBuilder.create()
				.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
				.build();
				CloseableHttpResponse response = client.execute(var)) {
				
				int responseCode = response.getStatusLine().getStatusCode();
	        	
	        	if (responseCode == 200) {
	        		jsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
				} else {
					logger.error("[PreFeedSAP] listEmpJob: " + response.getStatusLine().toString() + ":" + EntityUtils.toString(response.getEntity()));
				}
	        	
	        	if (!jsonObj.isNull("d")) {
					empJobVOList.addAll(this.returnListJSONEmpJob(jsonObj));
				} else {
					throw new Exception(String.valueOf(jsonObj.get("errorMessage")));
				}
				
				if ("S".equals(dadosConexaoVO.getAllowMsg())) {
					logger.debug("[PreFeedSAP] Total de lista EmpJob: " + empJobVOList.size());
				}
				
				return empJobVOList;
			}
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception listEmpJob: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
	}
	
	public List<EmpJobVO> returnListJSONEmpJob(JSONObject jsonObj) throws Exception {
		List<EmpJobVO> empJobVOList = new ArrayList<>();
		String campo = "Campo não mapeado";
		
		try {
			String next = null;
			
			if (!jsonObj.getJSONObject("d").isNull("__next")) {
				next = jsonObj.getJSONObject("d").get("__next").toString();
			}
			
			if (!jsonObj.getJSONObject("d").isNull("results")) {
				JSONArray resultadoArray = jsonObj.getJSONObject("d").getJSONArray("results");
				
				for (int i = 0; i < resultadoArray.length(); i++) {
					EmpJobVO empJobVO = new EmpJobVO();
					logger.debug("[PreFeedSAP] ResultadoArray: " + resultadoArray.toString());
					
					if (resultadoArray.getJSONObject(i).has("userId")) {
						if (!resultadoArray.getJSONObject(i).isNull("userId")) {
							campo = "userId";
							empJobVO.setUserId(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("userId")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("userNav")) {
						if (!resultadoArray.getJSONObject(i).isNull("userNav")) {
							if (resultadoArray.getJSONObject(i).getJSONObject("userNav").has("dateOfBirth")) {
								if (!resultadoArray.getJSONObject(i).getJSONObject("userNav").isNull("dateOfBirth")) {
									campo = "userNav/dateOfBirth";
									empJobVO.setDataNascimento(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getJSONObject("userNav").getString("dateOfBirth")));
								}
							}
							
							if (resultadoArray.getJSONObject(i).getJSONObject("userNav").has("username")) {
								if (!resultadoArray.getJSONObject(i).getJSONObject("userNav").isNull("username")) {
									campo = "userNav/username";
									empJobVO.setLogin(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("userNav").getString("username")));
								}
							}
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("employmentNav")) {
						if (!resultadoArray.getJSONObject(i).isNull("employmentNav")) {
							
							if (resultadoArray.getJSONObject(i).getJSONObject("employmentNav").has("customString22")) {
								if (!resultadoArray.getJSONObject(i).getJSONObject("employmentNav").isNull("customString22")) {
									campo = "employmentNav/customString22";
									empJobVO.setMatricula(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("employmentNav").getString("customString22")));
								}
							}
							
							if (!resultadoArray.getJSONObject(i).getJSONObject("employmentNav").getJSONObject("personNav").getJSONObject("nationalIdNav").isNull("results")) {
								campo = "employmentNav/personNav/nationalIdNav/results";
								JSONArray documentosArray = resultadoArray.getJSONObject(i).getJSONObject("employmentNav").getJSONObject("personNav").getJSONObject("nationalIdNav").getJSONArray("results");
								
								for (int j = 0; j < documentosArray.length(); j++) {
									campo = "employmentNav/personNav/nationalIdNav/results/nationalId";
									empJobVO.setCPF(utilsSAP.returnStringFromJson(documentosArray.getJSONObject(j).getString("nationalId")));
								}
							}
						
							if (!resultadoArray.getJSONObject(i).getJSONObject("employmentNav").getJSONObject("empWorkPermitNav").isNull("results")) {
								campo = "employmentNav/empWorkPermitNav/results/";
								JSONArray documentosArray = resultadoArray.getJSONObject(i).getJSONObject("employmentNav").getJSONObject("empWorkPermitNav").getJSONArray("results");
								
								for (int j = 0; j < documentosArray.length(); j++) {
									campo = "employmentNav/empWorkPermitNav/results/documentTypeNav/externalCode";
									
									if ("RG".equals((String) documentosArray.getJSONObject(j).getJSONObject("documentTypeNav").get("externalCode"))) {
										if (documentosArray.getJSONObject(j).has("expirationDate")) {
											if (!documentosArray.getJSONObject(j).isNull("expirationDate")) {
												campo = "employmentNav/empWorkPermitNav/results/documentTypeNav/externalCode/expirationDate";
												empJobVO.setDataValidadeDoc(utilsSAP.formatDateJson(documentosArray.getJSONObject(j).getString("expirationDate")));
											}
										}
										
										if (documentosArray.getJSONObject(j).has("issueDate")) {
											if (!documentosArray.getJSONObject(j).isNull("issueDate")) {
												campo = "employmentNav/empWorkPermitNav/results/documentTypeNav/externalCode/issueDate";
												empJobVO.setDataEmissaoDoc(utilsSAP.formatDateJson(documentosArray.getJSONObject(j).getString("issueDate")));
											}
										}
										
										if (documentosArray.getJSONObject(j).has("documentNumber")) {
											if (!documentosArray.getJSONObject(j).isNull("documentNumber")) {
												campo = "employmentNav/empWorkPermitNav/results/documentTypeNav/externalCode/documentNumber";
												empJobVO.setNumRG(utilsSAP.returnStringFromJson(documentosArray.getJSONObject(j).getString("documentNumber")));
											}
										}
									}
								}
							}
							
							if (resultadoArray.getJSONObject(i).getJSONObject("employmentNav").has("startDate")) {
								if (!resultadoArray.getJSONObject(i).getJSONObject("employmentNav").isNull("startDate")) {
									campo = "employmentNav/startDate";
									empJobVO.setDataInicio(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getJSONObject("employmentNav").getString("startDate")));
								}
							}
							
							if (resultadoArray.getJSONObject(i).getJSONObject("employmentNav").has("endDate")) {
								if (!resultadoArray.getJSONObject(i).getJSONObject("employmentNav").isNull("endDate")) {
									campo = "employmentNav/endDate";
									empJobVO.setDataFim(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getJSONObject("employmentNav").getString("endDate")));
								}
							}
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("eventReasonNav")) {
						if (!resultadoArray.getJSONObject(i).isNull("eventReasonNav")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("eventReasonNav").isNull("event")) {
								campo = "eventReasonNav/event";
								empJobVO.setMotivoEvento(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("eventReasonNav").getString("event")));
							}
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("emplStatus")) {
						if (!resultadoArray.getJSONObject(i).isNull("emplStatus")) {
							campo = "emplStatus";
							empJobVO.setStatusColaborador(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("emplStatus")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("lastModifiedOn")) {
						if (!resultadoArray.getJSONObject(i).isNull("lastModifiedOn")) {
							campo = "lastModifiedOn";
							empJobVO.setUltModifEmAtriOrg(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getString("lastModifiedOn")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("startDate")) {
						if (!resultadoArray.getJSONObject(i).isNull("startDate")) {
							campo = "startDate";
							empJobVO.setDataInicioAtriOrg(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getString("startDate")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("endDate")) {
						if (!resultadoArray.getJSONObject(i).isNull("endDate")) {
							campo = "endDate";
							empJobVO.setDataFimAtriOrg(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getString("endDate")));
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("companyNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("companyNav").has("externalCode")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("companyNav").isNull("externalCode")) {
								campo = "companyNav/externalCode";
								empJobVO.setEmpresa(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("companyNav").getString("externalCode")));
							}
						}
						
						if (resultadoArray.getJSONObject(i).getJSONObject("companyNav").has("name")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("companyNav").isNull("name")) {
								campo = "companyNav/name";
								empJobVO.setEmpresaDesc(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("companyNav").getString("name")));
							}
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("employeeClass")) {
						if (!resultadoArray.getJSONObject(i).isNull("employeeClass")) {
							campo = "employeeClass";
							empJobVO.setGrupoEmpregado(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("employeeClass")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("employmentType")) {
						if (!resultadoArray.getJSONObject(i).isNull("employmentType")) {
							campo = "employmentType";
							empJobVO.setSubGrupoEmpregado(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("employmentType")));
						}
					}
					
					if (resultadoArray.getJSONObject(i).has("costCenter")) {
						if (!resultadoArray.getJSONObject(i).isNull("costCenter")) {
							campo = "costCenter";
							empJobVO.setCentroCusto(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getString("costCenter")));
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("locationNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("locationNav").has("customString1")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("locationNav").isNull("customString1")) {
								campo = "locationNav/customString1";
								empJobVO.setEstabelecimento(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("locationNav").getString("customString1")));
							}
						}
						
						if (resultadoArray.getJSONObject(i).getJSONObject("locationNav").has("name")) {						
							if (!resultadoArray.getJSONObject(i).getJSONObject("locationNav").isNull("name")) {
								campo = "locationNav/name";
								empJobVO.setEstabelecimentoDesc(utilsSAP.removeSpaces(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("locationNav").getString("name"))));
							}
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("departmentNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("departmentNav").has("externalCode")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("departmentNav").isNull("externalCode")) {
								campo = "departmentNav/externalCode";
								empJobVO.setDepartamento(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("departmentNav").getString("externalCode")));
							}
						}
						
						if (resultadoArray.getJSONObject(i).getJSONObject("departmentNav").has("name")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("departmentNav").isNull("name")) {
								campo = "departmentNav/name";
								empJobVO.setDepartamentoDesc(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("departmentNav").getString("name")));
							}
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("jobCodeNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("jobCodeNav").has("externalCode")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("jobCodeNav").isNull("externalCode")) {
								campo = "jobCodeNav/externalCode";
								empJobVO.setCargo(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("jobCodeNav").getString("externalCode")));
							}
						}
						
						if (resultadoArray.getJSONObject(i).getJSONObject("jobCodeNav").has("name")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("jobCodeNav").isNull("name")) {
								campo = "jobCodeNav/name";
								empJobVO.setCargoDesc(utilsSAP.removeSpaces(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("jobCodeNav").getString("name"))));
							}
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("lastModifiedOn")) {
						campo = "lastModifiedOn";
						empJobVO.setUltModifEm(utilsSAP.formatDateJson(resultadoArray.getJSONObject(i).getString("lastModifiedOn")));
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("managerEmploymentNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("managerEmploymentNav").has("customString22")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("managerEmploymentNav").isNull("customString22")) {
								campo = "managerEmploymentNav/customString22";
								
								if ("S".equals(dadosConexaoVO.getAllowMsg())) {
									logger.debug("EmpJOB Matricula: " + empJobVO.getMatricula());
									logger.debug("EmpJOB Matricula gestor: " + resultadoArray.getJSONObject(i).getJSONObject("managerEmploymentNav").getString("customString22"));
								}
								
								empJobVO.setMatriculaGestor(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("managerEmploymentNav").getString("customString22")));
							}
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("emplStatusNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("emplStatusNav").has("picklistLabels")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("emplStatusNav").isNull("picklistLabels")) {
								campo = "emplStatusNav/picklistLabels/results";
								
								if ("S".equals(dadosConexaoVO.getAllowMsg())) {
									logger.debug("[PreFeedSAP] EmpJOB Matricula: " + empJobVO.getMatricula());
									logger.debug("[PreFeedSAP] EmpJOB Status Colaborador: " + utilsSAP.pickListLabels(resultadoArray.getJSONObject(i).getJSONObject("emplStatusNav").getJSONObject("picklistLabels").getJSONArray("results")));
								}
								
								empJobVO.setStatusColaboradorDesc(utilsSAP.returnStringFromJson(utilsSAP.pickListLabels(resultadoArray.getJSONObject(i).getJSONObject("emplStatusNav").getJSONObject("picklistLabels").getJSONArray("results"))));
							}
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("eventReasonNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("eventReasonNav").has("name")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("eventReasonNav").isNull("name")) {
								campo = "eventReasonNav/name";
								
								if ("S".equals(dadosConexaoVO.getAllowMsg())) {
									logger.debug("[PreFeedSAP] EmpJOB Matricula: " + empJobVO.getMatricula());
									logger.debug("[PreFeedSAP] EmpJOB Motivo Evento: " + resultadoArray.getJSONObject(i).getJSONObject("eventReasonNav").getString("name"));
								}
								
								empJobVO.setMotivoEventoDesc(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("eventReasonNav").getString("name")));
							}
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("employeeClassNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("employeeClassNav").has("picklistLabels")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("employeeClassNav").isNull("picklistLabels")) {
								campo = "employeeClassNav/picklistLabels/results";
								
								if ("S".equals(dadosConexaoVO.getAllowMsg())) {
									logger.debug("[PreFeedSAP] EmpJOB Matricula: " + empJobVO.getMatricula());
									logger.debug("[PreFeedSAP] EmpJOB Grupo Empregado: " + utilsSAP.pickListLabels(resultadoArray.getJSONObject(i).getJSONObject("employeeClassNav").getJSONObject("picklistLabels").getJSONArray("results")));
								}
								
								empJobVO.setGrupoEmpregadoDesc(utilsSAP.returnStringFromJson(utilsSAP.pickListLabels(resultadoArray.getJSONObject(i).getJSONObject("employeeClassNav").getJSONObject("picklistLabels").getJSONArray("results"))));
							}
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("employmentTypeNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("employmentTypeNav").has("picklistLabels")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("employmentTypeNav").isNull("picklistLabels")) {
								campo = "employmentTypeNav/picklistLabels/results";
								
								if ("S".equals(dadosConexaoVO.getAllowMsg())) {
									logger.debug("[PreFeedSAP] EmpJOB Matricula: " + empJobVO.getMatricula());
									logger.debug("[PreFeedSAP] EmpJOB SubGrupo Empregado: " + utilsSAP.pickListLabels(resultadoArray.getJSONObject(i).getJSONObject("employmentTypeNav").getJSONObject("picklistLabels").getJSONArray("results")));
								}
								
								empJobVO.setSubGrupoEmpregadoDesc(utilsSAP.returnStringFromJson(utilsSAP.pickListLabels(resultadoArray.getJSONObject(i).getJSONObject("employmentTypeNav").getJSONObject("picklistLabels").getJSONArray("results"))));
							}
						}
					}
					
					if (!resultadoArray.getJSONObject(i).isNull("costCenterNav")) {
						if (resultadoArray.getJSONObject(i).getJSONObject("costCenterNav").has("description_defaultValue")) {
							if (!resultadoArray.getJSONObject(i).getJSONObject("costCenterNav").isNull("description_defaultValue")) {
								campo = "costCenterNav/description_defaultValue";
								
								if ("S".equals(dadosConexaoVO.getAllowMsg())) {
									logger.debug("[PreFeedSAP] EmpJOB Matricula: " + empJobVO.getMatricula());
									logger.debug("[PreFeedSAP] EmpJOB Centro de Custo: " + resultadoArray.getJSONObject(i).getJSONObject("costCenterNav").getString("description_defaultValue"));
								}
								
								empJobVO.setCentroCustoDesc(utilsSAP.returnStringFromJson(resultadoArray.getJSONObject(i).getJSONObject("costCenterNav").getString("description_defaultValue")));
							}
						}
					}
					
					if (empJobVO.getCPF() != null) {
						empJobVOList.add(empJobVO);
					}
				}
				
				if (next != null) {
					if ("S".equals(dadosConexaoVO.getAllowMsg())) {
						logger.debug("[PreFeedSAP] Tamanho lista empJobVOList: " + empJobVOList.size());
					}
					
					empJobVOList.addAll(this.returnListJSONEmpJob(this.listarUrl(next)));
				}
			}
			
			return empJobVOList;
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception returnListJSONEmpJob: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(campo + ": " + e.toString());
		}
	}
	
	@SuppressWarnings("unused")
	public void addParameter(String type, String parameter1, String parameter2) throws Exception {
		List<CustWorkForceSoftwareVO> custWorkForceSoftwareVOList = new ArrayList<>();
		URL url = new URL(dadosConexaoVO.getUrlSAP() + "/odata/v2/upsert");
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Authorization", "Bearer " + this.gerarToken());
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		
		try {
			String usuarioJson = "";
			
			if ("user".equalsIgnoreCase(type)) {
				usuarioJson = this.newUser(parameter1, parameter2);
			} else if ("email".equalsIgnoreCase(type)) {
				usuarioJson = this.newEmail(parameter1, parameter2);
			}
			
			if ("S".equals(dadosConexaoVO.getAllowMsg())) {
				logger.debug(usuarioJson);
			}
			
			OutputStream os = conn.getOutputStream();
			os.write(usuarioJson.getBytes(dadosConexaoVO.getCodificacaoJSON()));
			os.close();
			
			if ("S".equals(dadosConexaoVO.getAllowMsg())) {
				logger.debug("[PreFeedSAP] Add " + type + ": " + usuarioJson);
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), dadosConexaoVO.getCodificacaoJSON()));
			String resultado = "";
			
			JSONObject jsonObj = null;
			String line = "";
			
			while ((line = in.readLine()) != null) {
				resultado = resultado + line;
			}
			
			in.close();
			
			if ("S".equals(dadosConexaoVO.getAllowMsg())) {
				logger.debug("[PreFeedSAP] addParameter resultado: " + resultado);
			}
			
			jsonObj = new JSONObject(resultado);
			
			if (!jsonObj.isNull("d")) {
				
			} else {
				throw new Exception(String.valueOf(jsonObj.get("errorMessage")));
			}
		}
		catch (Exception e) {
			logger.error("[PreFeedSAP] Exception addParameter: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
	}
	
	public String newUser(String userId, String username) throws Exception {
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObjectUser = new JSONObject();
		
		try {
			jsonObjectUser.put("uri", "User('" + userId + "')");
			jsonObjectUser.put("type", "SFOData.User");
			jsonObject.put("__metadata", jsonObjectUser);
			jsonObject.put("username", username);
		}
		catch (JSONException e) {
			logger.error("[PreFeedSAP] Exception newUser: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
		
		return jsonObject.toString();
	}
	
	public String newEmail(String personIdExternal, String email) throws Exception {
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObjectUser = new JSONObject();
		
		try {
			jsonObjectUser.put("uri", "PerEmail(emailType='33267',personIdExternal='" + personIdExternal + "')");
			jsonObjectUser.put("type", "SFOData.PerEmail");
			jsonObject.put("__metadata", jsonObjectUser);
			jsonObject.put("emailAddress", email);
		}
		catch (JSONException e) {
			logger.error("[PreFeedSAP] Exception newEmail: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
		
		return jsonObject.toString();
	}
	
	public HttpClient createTlsV2HttpClient() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		SSLContext sslContext = SSLContexts.createSystemDefault();
		SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1.1", "TLSv1.2" },null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		org.apache.http.config.Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", f).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder.setConnectionRequestTimeout(600000000);
		HttpClientBuilder httpCustom = HttpClients.custom();
		httpCustom.setSSLSocketFactory(f);
		httpCustom.setConnectionManager(cm);
		httpCustom.setDefaultRequestConfig(requestBuilder.build());
		CloseableHttpClient client = httpCustom.build();
		return client;
	}
}
