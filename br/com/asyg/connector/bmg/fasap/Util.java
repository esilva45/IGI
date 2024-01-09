package br.com.asyg.connector.bmg.fasap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import br.com.asyg.connector.bmg.fasap.model.DadosConexaoVO;
import br.com.asyg.connector.bmg.fasap.model.EmpJobVO;
import br.com.asyg.connector.bmg.fasap.model.EmploymentNavVO;

public class Util {
	private static Logger logger = Logger.getLogger("FASAPConnector");
	private DadosConexaoVO dadosConexaoVO;
	
	public Util() { }
	
	public Util(DadosConexaoVO dadosConexaoVO) {
		this.dadosConexaoVO = dadosConexaoVO;
	}
	
	public java.sql.Date asSqlDate(String date) {
        try {
            java.util.Date dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);
            return new java.sql.Date(dt.getTime());
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
	
	public String formatName(String name) throws Exception {
		ArrayList<String> exception = new ArrayList<String>();
		name = name.trim();
		String[] nameSplited = name.split(" ");
		String nameFormatted = "";
		
		exception.add("da");
		exception.add("e");
		exception.add("de");
		exception.add("das");
		exception.add("des");
		exception.add("dos");
		exception.add("do");
		exception.add("du");
		exception.add("a");
		
		try {
			for (int i = 0; i < nameSplited.length; i++) {
				if (exception.contains(nameSplited[i].toLowerCase().trim())) {
					nameFormatted = nameFormatted + " " + nameSplited[i].toLowerCase();
				} else if (nameSplited[i].length() > 0) {
					nameFormatted = nameFormatted + " " + nameSplited[i].substring(0, 1).toUpperCase()
							+ nameSplited[i].substring(1, nameSplited[i].length()).toLowerCase();
				}
			}
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
		
		return nameFormatted.trim();
	}
	
	// regra criada para remover espacos extras e caracteres de espaco
	public String removeSpaces(String description) throws Exception {
		try {
			return (description != null ? description.trim().replaceAll(" +", " ") : null);
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
	}
	
	public String formatDateJson(String jsonDate) throws Exception {
		String stDate = null;
		
		try {
			if (!StringUtils.isEmpty(jsonDate) && jsonDate.contains("/Date(")) {
				Long in = Long.parseLong(jsonDate.replace("/Date(", "").replace(")/", "").replace("+0000", ""));
				Timestamp stamp = new Timestamp(in);
				Date date = new Date(stamp.getTime());
				
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(date);
				gc.add(Calendar.HOUR, 3);
				
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				stDate = sdf.format(gc.getTime());
			}
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
		
		return stDate;
	}
	
	public String returnStringFromJson(String value) throws Exception {
		try {
			return (StringUtils.isEmpty(value) ? null : value);
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
	}
	
	public String fromDate() throws Exception {
		try {
			Date today = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(today);
			return "&fromDate=" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) < 9 ? "0" : "")
					+ (c.get(Calendar.MONTH) + 1) + "-" + (c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
					+ c.get(Calendar.DAY_OF_MONTH);
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
	}
	
	public static Date formatDate(String in) throws Exception {
		Date date = null;
		
		try {
			if (in != null) {
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				date = sdf.parse(in);
			}
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
		return date;
	}
	
	@SuppressWarnings("static-access")
	public String validPerPersonalId(List<EmploymentNavVO> employmentNavs) throws Exception {
		EmploymentNavVO valid = employmentNavs.get(0);
		Date finalDate = this.formatDate(valid.getEndDate());
		
		try {
			for (EmploymentNavVO employmentNav : employmentNavs) {
				if (StringUtils.isEmpty(employmentNav.getEndDate())) {
					return employmentNav.getValidPerPersonal();
				}
				
				finalDate = this.formatDate(employmentNav.getEndDate());
				
				if (finalDate.after(this.formatDate(valid.getEndDate()))) {
					valid = employmentNav;
				}
			}
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
		
		return valid.getValidPerPersonal();
	}
	
	@SuppressWarnings("static-access")
	public String validActivePerPersonalId(List<EmploymentNavVO> employmentNavs) throws Exception {
		EmploymentNavVO valid = employmentNavs.get(0);
		Date startDate = this.formatDate(valid.getStartDate());
		
		try {
			for (EmploymentNavVO employmentNav : employmentNavs) {
				startDate = this.formatDate(employmentNav.getStartDate());
				
				if (startDate.after(this.formatDate(valid.getStartDate()))) {
					valid = employmentNav;
				}
			}
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
		
		return valid.getValidPerPersonal();
	}
	
	public void fecharConexao(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Connection getConexaoDB2() throws Exception {
		Connection connection = null;
		
		try {
			String driverName = "com.ibm.db2.jcc.DB2Driver";
			Class.forName(driverName);
			String url = dadosConexaoVO.getUrlIGI();
			String username = dadosConexaoVO.getUserIGI();
			String password = dadosConexaoVO.getPassIGI();
			connection = DriverManager.getConnection(url, username, password);
			return connection;
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("[PreFeedSinergy] message: Erro DB2: " + e.getLocalizedMessage());
			logger.error("O driver expecificado não foi encontrado.");
			return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.toString());
		}
	}
	
	public String pickListLabels(JSONArray jsonArray) throws JSONException, Exception {
		try {
			String labelReturn = null;
			
			for (int j = 0; j < jsonArray.length(); j++) {
				if (!jsonArray.getJSONObject(j).isNull("locale")) {
					if ("pt_br".equalsIgnoreCase(jsonArray.getJSONObject(j).getString("locale"))) {
						if (!jsonArray.getJSONObject(j).isNull("label")) {
							labelReturn = jsonArray.getJSONObject(j).getString("label");
						}
					}
				}
			}
			
			return labelReturn;
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public List<EmpJobVO> validEmpJobVO(List<EmpJobVO> nonValidUsuariosVO) throws Exception {
		String posicao = "";
		
		try {
			List<EmpJobVO> validUsuariosVO = new ArrayList<>();
			HashMap<String, List<EmpJobVO>> userMapToBeCleared = new HashMap();
			posicao = "remover os CPFs nulos";
			List<EmpJobVO> empJobsList = nonValidUsuariosVO.stream().filter(x -> x.getCPF() != null).collect(Collectors.toList());
			
			posicao = "CPFs nulos removidos e vai executar sort";
			Collections.sort(empJobsList);
			posicao = "executou sort";
			
			for (EmpJobVO usuarioVO : empJobsList) {
				List<EmpJobVO> usuariosVO = new ArrayList<>();
				
				if (userMapToBeCleared.get(usuarioVO.getCPF()) != null) {
					if (!userMapToBeCleared.get(usuarioVO.getCPF()).isEmpty()) {
						usuariosVO = userMapToBeCleared.get(usuarioVO.getCPF());
					}
				}
				
				usuariosVO.add(usuarioVO);
				userMapToBeCleared.put(usuarioVO.getCPF(), usuariosVO);
			}
			
			posicao = "map feito e vai verificar o registro único";
			
			for (String cpf : userMapToBeCleared.keySet()) {
				List<EmpJobVO> usuariosVO = userMapToBeCleared.get(cpf);
				
				if (usuariosVO.size() == 1) {
					validUsuariosVO.add(usuariosVO.get(0));
				} else {
					validUsuariosVO.add(this.ifMoreThanOneUser(usuariosVO));
				}
			}
			
			return validUsuariosVO;
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
	}
	
	public EmpJobVO ifMoreThanOneUser(List<EmpJobVO> usuariosVO) throws Exception {
		EmpJobVO usuarioVOAnt = new EmpJobVO();
		EmpJobVO validUsuarioVO = new EmpJobVO();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		for (EmpJobVO usuarioVO : usuariosVO) {
			if (usuarioVOAnt.getStatusColaboradorDesc() == null) {
				if ("Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						|| "Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						|| "Ferias".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						|| "Férias".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						|| "Licença".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						|| "Licenca".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						|| "Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))) {
					validUsuarioVO = usuarioVO;
				}
			} else {
				if (("Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						&& "Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Férias".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Ferias".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Licença".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Licenca".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))) {
					validUsuarioVO = usuarioVOAnt;
				}
				
				if (("Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						&& "Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Férias".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Ferias".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Licença".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Licenca".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))) {
					validUsuarioVO = usuarioVO;
				}
				
				if (("Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						&& "Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Férias".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Ferias".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Licença".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Licenca".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Bloqueado".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Férias".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Ferias".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Licença".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))
						|| ("Ativo".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
								&& "Licenca".equalsIgnoreCase(this.removeSpaces(usuarioVOAnt.getStatusColaboradorDesc())))) {
					try {
						if (usuarioVO.getUltModifEm() == null) {
							if (usuarioVOAnt.getUltModifEm() == null) {
								validUsuarioVO = usuarioVO;
							} else {
								validUsuarioVO = usuarioVOAnt;
							}
						} else if (usuarioVO.getUltModifEm() != null && usuarioVOAnt.getUltModifEm() != null) {
							Date lastModDate = dateFormat.parse(usuarioVO.getUltModifEm());
							Date lastModDateAnt = dateFormat.parse(usuarioVOAnt.getUltModifEm());

							if (lastModDateAnt.before(lastModDate)) {
								validUsuarioVO = usuarioVO;
							}
						}
					}
					catch (ParseException e) {
						throw new Exception(e.toString());
					}
				}
				
				if ("Demitido".equalsIgnoreCase(this.removeSpaces(usuarioVO.getStatusColaboradorDesc()))
						&& "Demitido".equalsIgnoreCase(this.removeSpaces(validUsuarioVO.getStatusColaboradorDesc()))) {
					try {
						if (usuarioVO.getUltModifEm() == null) {
							if (usuarioVOAnt.getUltModifEm() == null) {
								validUsuarioVO = usuarioVO;
							} else {
								validUsuarioVO = usuarioVOAnt;
							}
						} else if (usuarioVO.getUltModifEm() != null && usuarioVOAnt.getUltModifEm() != null) {
							Date lastModDate = dateFormat.parse(usuarioVO.getUltModifEm());
							Date lastModDateAnt = dateFormat.parse(usuarioVOAnt.getUltModifEm());
							if (lastModDateAnt.before(lastModDate)) {
								validUsuarioVO = usuarioVO;
							}
						}
					}
					catch (ParseException e) {
						throw new Exception(e.toString());
					}
				}
			}
			usuarioVOAnt = usuarioVO;
		}
		
		return validUsuarioVO;
	}
}
