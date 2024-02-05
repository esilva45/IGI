package br.com.asyg.connector.bmg.fasap.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import br.com.asyg.connector.bmg.fasap.model.Account;
import br.com.asyg.connector.bmg.fasap.model.CustWorkForceSoftwareVO;
import br.com.asyg.connector.bmg.fasap.model.DadosConexaoVO;
import br.com.asyg.connector.bmg.fasap.model.EmpEmploymentVO;
import br.com.asyg.connector.bmg.fasap.model.EmpJobVO;
import br.com.asyg.connector.bmg.fasap.model.PerPersonVO;
import br.com.asyg.connector.bmg.fasap.model.PerPersonalVO;

public class PreFeedDAO {
	private static Logger logger = Logger.getLogger("FASAPConnector");
	private DadosConexaoVO dadosConexaoVO = null;
	
	public PreFeedDAO(DadosConexaoVO dadosConexaoVO) {
		this.dadosConexaoVO = dadosConexaoVO;
	}
	
	public void updateIntegrou() throws Exception {
		Connection conn = this.getConexaoDB2();
		
		try {
			PreparedStatement stmt = conn.prepareStatement("UPDATE IGITOOLS.PRE_FEED_SAP_PERPERSONAL SET INTEGROU=1");
			stmt.execute();

			stmt = conn.prepareStatement("UPDATE IGITOOLS.PRE_FEED_SAP_CUSTWORKFORCE SET INTEGROU=1");
			stmt.execute();
			
			stmt = conn.prepareStatement("UPDATE IGITOOLS.PRE_FEED_SAP_EMPEMPLOYMENT SET INTEGROU=1");
			stmt.execute();
			
			stmt = conn.prepareStatement("UPDATE IGITOOLS.PRE_FEED_SAP_EMPJOB SET INTEGROU=1");
			stmt.execute();
			
			stmt = conn.prepareStatement("UPDATE IGITOOLS.PRE_FEED_SAP_PERPERSON SET INTEGROU=1");
			stmt.execute();
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception updateIntegrou: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public List<EmpJobVO> empJobsDAO() throws Exception {
		List<EmpJobVO> empJobs = new ArrayList<>();
		String sql = "SELECT ID, USER_ID, MATRICULA, DT_NASC, CPF, DT_VALIDADE_DOC, DT_EMISSAO_DOC, RG_NUM, DT_INICIO, DT_FIM, MOTIVO_EVENTO, STATUS_COLABORADOR, "
				+ "DT_ULT_MOD, LOGIN, DT_FIM_ATR_ORG, DT_INI_ATR_ORG, EMPRESA, GRUPO_EMPREGADO, SUB_GRUPO_EMPREGADO, CENTRO_CUSTO, ESTABELECIMENTO, CARGO, DEPARTAMENTO, "
				+ "DT_ULT_MOD_ATR_ORG, MATRICULA_GESTOR, CARGO_DESC, ESTABELECIMENTO_DESC, EMPRESA_DESC, DEPARTAMENTO_DESC,STATUS_COLABORADOR_DESC, MOTIVO_EVENTO_DESC, "
				+ "GRUPO_EMPREGADO_DESC, SUB_GRUPO_EMPREGADO_DESC, CENTRO_CUSTO_DESC FROM IGITOOLS.PRE_FEED_SAP_EMPJOB";
		Connection conn = this.getConexaoDB2();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				EmpJobVO empJob = new EmpJobVO();
				empJob.setId(rs.getInt("ID"));
				empJob.setUserId(rs.getString("USER_ID"));
				empJob.setMatricula(rs.getString("MATRICULA"));
				empJob.setDataNascimento(rs.getString("DT_NASC"));
				empJob.setCPF(rs.getString("CPF"));
				empJob.setDataValidadeDoc(rs.getString("DT_VALIDADE_DOC"));
				empJob.setDataEmissaoDoc(rs.getString("DT_EMISSAO_DOC"));
				empJob.setNumRG(rs.getString("RG_NUM"));
				empJob.setDataInicio(rs.getString("DT_INICIO"));
				empJob.setDataFim(rs.getString("DT_FIM"));
				empJob.setMotivoEvento(rs.getString("MOTIVO_EVENTO"));
				empJob.setStatusColaborador(rs.getString("STATUS_COLABORADOR"));
				empJob.setUltModifEm(rs.getString("DT_ULT_MOD"));
				empJob.setLogin(rs.getString("LOGIN"));
				empJob.setDataFimAtriOrg(rs.getString("DT_FIM_ATR_ORG"));
				empJob.setDataInicioAtriOrg(rs.getString("DT_INI_ATR_ORG"));
				empJob.setEmpresa(rs.getString("EMPRESA"));
				empJob.setGrupoEmpregado(rs.getString("GRUPO_EMPREGADO"));
				empJob.setSubGrupoEmpregado(rs.getString("SUB_GRUPO_EMPREGADO"));
				empJob.setCentroCusto(rs.getString("CENTRO_CUSTO"));
				empJob.setEstabelecimento(rs.getString("ESTABELECIMENTO"));
				empJob.setCargo(rs.getString("CARGO"));
				empJob.setDepartamento(rs.getString("DEPARTAMENTO"));
				empJob.setUltModifEmAtriOrg(rs.getString("DT_ULT_MOD_ATR_ORG"));
				empJob.setMatriculaGestor(rs.getString("MATRICULA_GESTOR"));
				empJob.setCargoDesc(rs.getString("CARGO_DESC"));
				empJob.setEstabelecimentoDesc(rs.getString("ESTABELECIMENTO_DESC"));
				empJob.setEmpresaDesc(rs.getString("EMPRESA_DESC"));
				empJob.setDepartamentoDesc(rs.getString("DEPARTAMENTO_DESC"));
				empJob.setStatusColaboradorDesc(rs.getString("STATUS_COLABORADOR_DESC"));
				empJob.setMotivoEventoDesc(rs.getString("MOTIVO_EVENTO_DESC"));
				empJob.setGrupoEmpregadoDesc(rs.getString("GRUPO_EMPREGADO_DESC"));
				empJob.setSubGrupoEmpregadoDesc(rs.getString("SUB_GRUPO_EMPREGADO_DESC"));
				empJob.setCentroCustoDesc(rs.getString("CENTRO_CUSTO_DESC"));
				empJobs.add(empJob);
			}
			
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception empJobsDAO: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
		
		return empJobs;
	}
	
	public void insertEmpJob(List<EmpJobVO> empJobs) throws Exception {
		String INSERT = "INSERT INTO IGITOOLS.PRE_FEED_SAP_EMPJOB "
				+ "(USER_ID, MATRICULA, DT_NASC, CPF, DT_VALIDADE_DOC, DT_EMISSAO_DOC, RG_NUM, DT_INICIO, DT_FIM, MOTIVO_EVENTO, STATUS_COLABORADOR, DT_ULT_MOD, LOGIN, "
				+ "DT_FIM_ATR_ORG, DT_INI_ATR_ORG, EMPRESA, GRUPO_EMPREGADO, SUB_GRUPO_EMPREGADO, CENTRO_CUSTO, ESTABELECIMENTO, CARGO, DEPARTAMENTO, DT_ULT_MOD_ATR_ORG, "
				+ "MATRICULA_GESTOR, INTEGROU, CARGO_DESC, ESTABELECIMENTO_DESC, EMPRESA_DESC, DEPARTAMENTO_DESC,STATUS_COLABORADOR_DESC, MOTIVO_EVENTO_DESC, GRUPO_EMPREGADO_DESC, "
				+ "SUB_GRUPO_EMPREGADO_DESC, CENTRO_CUSTO_DESC) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = this.getConexaoDB2();
		
		try {
			int count = 0;
			
			for (EmpJobVO empJob : empJobs) {
				PreparedStatement stmt = conn.prepareStatement(INSERT);
				stmt.setString(1, empJob.getUserId());
				stmt.setString(2, empJob.getMatricula());
				stmt.setString(3, empJob.getDataNascimento());
				stmt.setString(4, empJob.getCPF());
				stmt.setString(5, empJob.getDataValidadeDoc());
				stmt.setString(6, empJob.getDataEmissaoDoc());
				stmt.setString(7, empJob.getNumRG());
				stmt.setString(8, empJob.getDataInicio());
				stmt.setString(9, empJob.getDataFim());
				stmt.setString(10, empJob.getMotivoEvento());
				stmt.setString(11, empJob.getStatusColaborador());
				stmt.setString(12, empJob.getUltModifEm());
				stmt.setString(13, empJob.getLogin());
				stmt.setString(14, empJob.getDataFimAtriOrg());
				stmt.setString(15, empJob.getDataInicioAtriOrg());
				stmt.setString(16, empJob.getEmpresa());
				stmt.setString(17, empJob.getGrupoEmpregado());
				stmt.setString(18, empJob.getSubGrupoEmpregado());
				stmt.setString(19, empJob.getCentroCusto());
				stmt.setString(20, empJob.getEstabelecimento());
				stmt.setString(21, empJob.getCargo());
				stmt.setString(22, empJob.getDepartamento());
				stmt.setString(23, empJob.getUltModifEmAtriOrg());
				stmt.setString(24, empJob.getMatriculaGestor());
				stmt.setInt(25, empJob.getIntegrou());
				stmt.setString(26, empJob.getCargoDesc());
				stmt.setString(27, empJob.getEstabelecimentoDesc());
				stmt.setString(28, empJob.getEmpresaDesc());
				stmt.setString(29, empJob.getDepartamentoDesc());
				stmt.setString(30, empJob.getStatusColaboradorDesc());
				stmt.setString(31, empJob.getMotivoEventoDesc());
				stmt.setString(32, empJob.getGrupoEmpregadoDesc());
				stmt.setString(33, empJob.getSubGrupoEmpregadoDesc());
				stmt.setString(34, empJob.getCentroCustoDesc());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception insertEmpJob: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public void updateEmpJob(List<EmpJobVO> empJobs) throws Exception {
		String UPDATE = "UPDATE IGITOOLS.PRE_FEED_SAP_EMPJOB "
				+ "SET USER_ID=?, MATRICULA=?, DT_NASC=?, CPF=?, DT_VALIDADE_DOC=?, DT_EMISSAO_DOC=?, RG_NUM=?, DT_INICIO=?, DT_FIM=?, MOTIVO_EVENTO=?, STATUS_COLABORADOR=?, "
				+ "DT_ULT_MOD=?, LOGIN=?, DT_FIM_ATR_ORG=?, DT_INI_ATR_ORG=?, EMPRESA=?, GRUPO_EMPREGADO=?, SUB_GRUPO_EMPREGADO=?, CENTRO_CUSTO=?, ESTABELECIMENTO=?, CARGO=?, "
				+ "DEPARTAMENTO=?, DT_ULT_MOD_ATR_ORG=?, MATRICULA_GESTOR=?, INTEGROU=?, CARGO_DESC=?, ESTABELECIMENTO_DESC=?, EMPRESA_DESC=?, DEPARTAMENTO_DESC=?, STATUS_COLABORADOR_DESC=?, "
				+ "MOTIVO_EVENTO_DESC=?, GRUPO_EMPREGADO_DESC=?, SUB_GRUPO_EMPREGADO_DESC=?, CENTRO_CUSTO_DESC=? where id=?";
		Connection conn = this.getConexaoDB2();
		
		try {
			int count = 0;
			
			for (EmpJobVO empJob : empJobs) {
				PreparedStatement stmt = conn.prepareStatement(UPDATE);
				stmt.setString(1, empJob.getUserId());
				stmt.setString(2, empJob.getMatricula());
				stmt.setString(3, empJob.getDataNascimento());
				stmt.setString(4, empJob.getCPF());
				stmt.setString(5, empJob.getDataValidadeDoc());
				stmt.setString(6, empJob.getDataEmissaoDoc());
				stmt.setString(7, empJob.getNumRG());
				stmt.setString(8, empJob.getDataInicio());
				stmt.setString(9, empJob.getDataFim());
				stmt.setString(10, empJob.getMotivoEvento());
				stmt.setString(11, empJob.getStatusColaborador());
				stmt.setString(12, empJob.getUltModifEm());
				stmt.setString(13, empJob.getLogin());
				stmt.setString(14, empJob.getDataFimAtriOrg());
				stmt.setString(15, empJob.getDataInicioAtriOrg());
				stmt.setString(16, empJob.getEmpresa());
				stmt.setString(17, empJob.getGrupoEmpregado());
				stmt.setString(18, empJob.getSubGrupoEmpregado());
				stmt.setString(19, empJob.getCentroCusto());
				stmt.setString(20, empJob.getEstabelecimento());
				stmt.setString(21, empJob.getCargo());
				stmt.setString(22, empJob.getDepartamento());
				stmt.setString(23, empJob.getUltModifEmAtriOrg());
				stmt.setString(24, empJob.getMatriculaGestor());
				stmt.setInt(25, empJob.getIntegrou());
				stmt.setString(26, empJob.getCargoDesc());
				stmt.setString(27, empJob.getEstabelecimentoDesc());
				stmt.setString(28, empJob.getEmpresaDesc());
				stmt.setString(29, empJob.getDepartamentoDesc());
				stmt.setString(30, empJob.getStatusColaboradorDesc());
				stmt.setString(31, empJob.getMotivoEventoDesc());
				stmt.setString(32, empJob.getGrupoEmpregado());
				stmt.setString(33, empJob.getSubGrupoEmpregadoDesc());
				stmt.setString(34, empJob.getCentroCustoDesc());
				stmt.setInt(35, empJob.getId());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception updateEmpJob: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public List<PerPersonalVO> perPersonalsDAO() throws Exception {
		List<PerPersonalVO> perPersonals = new ArrayList<>();
		String sql = "SELECT ID, LASTNAME, FIRSTNAME, PREFEREDNAME, GENDER, DT_INICIO, DT_FIM, MATRICULA FROM IGITOOLS.PRE_FEED_SAP_PERPERSONAL";
		Connection conn = this.getConexaoDB2();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				PerPersonalVO perPersonal = new PerPersonalVO();
				perPersonal.setId(rs.getInt("ID"));
				perPersonal.setLastName(rs.getString("LASTNAME"));
				perPersonal.setFirstName(rs.getString("FIRSTNAME"));
				perPersonal.setPreferedName(rs.getString("PREFEREDNAME"));
				perPersonal.setGender(rs.getString("GENDER"));
				perPersonal.setStartDate(rs.getString("DT_INICIO"));
				perPersonal.setEndDate(rs.getString("DT_FIM"));
				perPersonal.setPersonIdExternal(rs.getString("MATRICULA"));
				perPersonals.add(perPersonal);
			}
			
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception perPersonalsDAO: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
		
		return perPersonals;
	}
	
	public List<PerPersonVO> perPersonsDAO() throws Exception {
		List<PerPersonVO> perPersons = new ArrayList<>();
		String sql = "SELECT ID, PERSONID, AREACODE, PHONENUMBER FROM IGITOOLS.PRE_FEED_SAP_PERPERSON";
		Connection conn = this.getConexaoDB2();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				PerPersonVO perPerson = new PerPersonVO();
				perPerson.setPersonId(rs.getString("PERSONID"));
				perPerson.setAreaCode(rs.getString("AREACODE"));
				perPerson.setPhoneNumber(rs.getString("PHONENUMBER"));
				perPerson.setId(rs.getInt("ID"));
				perPersons.add(perPerson);
			}
			
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception perPersonsDAO: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
		finally {
			this.fecharConexao(conn);
		}
		
		return perPersons;
	}
	
	public List<EmpEmploymentVO> empEmploymentDAO() throws Exception {
		List<EmpEmploymentVO> empEmployments = new ArrayList<>();
		String sql = "SELECT ID, PERSONID, NIVELCARGO FROM IGITOOLS.PRE_FEED_SAP_EMPEMPLOYMENT";
		Connection conn = this.getConexaoDB2();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				EmpEmploymentVO empEmployment = new EmpEmploymentVO();
				empEmployment.setPersonId(rs.getString("PERSONID"));
				empEmployment.setNivelCargo(rs.getString("NIVELCARGO"));
				empEmployment.setId(rs.getInt("ID"));
				empEmployments.add(empEmployment);
			}
			
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception empEmploymentDAO: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
		
		return empEmployments;
	}
	
	public void insertEmpEmployment(List<EmpEmploymentVO> empEmployments) throws Exception {
		String INSERT = "INSERT INTO IGITOOLS.PRE_FEED_SAP_EMPEMPLOYMENT(PERSONID,NIVELCARGO,DT_ULT_MOD,INTEGROU) VALUES(?,?,?,?)";
		Connection conn = this.getConexaoDB2();
		
		try {
			int count = 0;
			
			for (EmpEmploymentVO empEmployment : empEmployments) {
				PreparedStatement stmt = conn.prepareStatement(INSERT);
				stmt.setString(1, empEmployment.getPersonId());
				stmt.setString(2, empEmployment.getNivelCargo());
				stmt.setString(3, empEmployment.getDtAlteracao());
				stmt.setInt(4, empEmployment.getIntegrou());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
			
			for (EmpEmploymentVO empEmployment : empEmployments) {
				PreparedStatement stmt = conn.prepareStatement("UPDATE IGITOOLS.PRE_FEED_SAP_EMPJOB SET INTEGROU=0 WHERE MATRICULA=?");
				stmt.setString(1, empEmployment.getPersonId());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}					
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception insertEmpEmployment: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public void updateEmpEmployment(List<EmpEmploymentVO> empEmployments) throws Exception {
		String INSERT = "UPDATE IGITOOLS.PRE_FEED_SAP_EMPEMPLOYMENT SET PERSONID=?, NIVELCARGO=?, INTEGROU=?, DT_ULT_MOD=? WHERE ID=?";
		Connection conn = this.getConexaoDB2();
		
		try {
			int count = 0;
			
			for (EmpEmploymentVO empEmployment : empEmployments) {
				PreparedStatement stmt = conn.prepareStatement(INSERT);
				stmt.setString(1, empEmployment.getPersonId());
				stmt.setString(2, empEmployment.getNivelCargo());
				stmt.setInt(3, empEmployment.getIntegrou());
				stmt.setString(4, empEmployment.getDtAlteracao());
				stmt.setInt(5, empEmployment.getId());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
			
			for (EmpEmploymentVO empEmployment : empEmployments) {
				PreparedStatement stmt = conn.prepareStatement("UPDATE IGITOOLS.PRE_FEED_SAP_EMPJOB SET INTEGROU=0 WHERE MATRICULA=?");
				stmt.setString(1, empEmployment.getPersonId());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}	
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception updateEmpEmployment: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public void updatePerPerson(List<PerPersonVO> perPersons) throws Exception {
		String regex = "\\d+";
		String INSERT = "UPDATE IGITOOLS.PRE_FEED_SAP_PERPERSON SET PERSONID=?, AREACODE=?, PHONENUMBER=?, INTEGROU=?, DT_ULT_MOD=? WHERE ID=?";
		Connection conn = this.getConexaoDB2();
		String phone = "";
		boolean verify = false;
		
		try {
			int count = 0;
			
			for (PerPersonVO perPerson : perPersons) {
				phone = perPerson.getPhoneNumber();
				
				if (phone != null) {
					phone = phone.replaceAll("-", "").replaceAll(" ", "").trim();
					verify = phone.matches(regex);
		        }
				
				if (verify) {
					PreparedStatement stmt = conn.prepareStatement(INSERT);
					stmt.setString(1, perPerson.getPersonId());
					stmt.setString(2, perPerson.getAreaCode());
					stmt.setString(3, verify ? phone : "0");
					stmt.setInt(4, perPerson.getIntegrou());
					stmt.setString(5, perPerson.getDtAlteracao());
					stmt.setInt(6, perPerson.getId());
					stmt.execute();
				}
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
			
			for (PerPersonVO perPerson : perPersons) {
				PreparedStatement stmt = conn.prepareStatement("UPDATE IGITOOLS.PRE_FEED_SAP_EMPJOB SET INTEGROU=0 WHERE MATRICULA=?");
				stmt.setString(1, perPerson.getPersonId());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}	
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception updatePerPerson: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public void insertPerPerson(List<PerPersonVO> perPersons) throws Exception {
		String regex = "\\d+";
		String INSERT = "INSERT INTO IGITOOLS.PRE_FEED_SAP_PERPERSON(PERSONID,AREACODE,PHONENUMBER,DT_ULT_MOD,INTEGROU) VALUES(?,?,?,?,?)";
		Connection conn = this.getConexaoDB2();
		String phone = "";
		boolean verify = false;
		
		try {
			int count = 0;
			
			for (PerPersonVO perPerson : perPersons) {
				phone = perPerson.getPhoneNumber();
				
				if (phone != null) {
					phone = phone.replaceAll("-", "").replaceAll(" ", "").trim();
					verify = phone.matches(regex);
		        }
				
				if (verify) {
					PreparedStatement stmt = conn.prepareStatement(INSERT);
					stmt.setString(1, perPerson.getPersonId());
					stmt.setString(2, perPerson.getAreaCode());
					stmt.setString(3, verify ? phone : "0");
					stmt.setString(4, perPerson.getDtAlteracao());
					stmt.setInt(5, perPerson.getIntegrou());
					stmt.execute();
				}
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
			
			for (PerPersonVO perPerson : perPersons) {
				PreparedStatement stmt = conn.prepareStatement("UPDATE IGITOOLS.PRE_FEED_SAP_EMPJOB SET INTEGROU=0 WHERE MATRICULA=?");
				stmt.setString(1, perPerson.getPersonId());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}	
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception insertPerPerson: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public void insertPerPersonal(List<PerPersonalVO> perPersonals) throws Exception {
		String INSERT = "INSERT INTO IGITOOLS.PRE_FEED_SAP_PERPERSONAL (LASTNAME,FIRSTNAME,PREFEREDNAME,GENDER,DT_INICIO,DT_FIM,MATRICULA,INTEGROU,MATRICULA_ATIVA) VALUES(?,?,?,?,?,?,?,?,?)";
		Connection conn = this.getConexaoDB2();
		
		try {
			int count = 0;
			
			for (PerPersonalVO perPersonal : perPersonals) {
				PreparedStatement stmt = conn.prepareStatement(INSERT);
				stmt.setString(1, perPersonal.getLastName());
				stmt.setString(2, perPersonal.getFirstName());
				stmt.setString(3, perPersonal.getPreferedName());
				stmt.setString(4, perPersonal.getGender());
				stmt.setString(5, perPersonal.getStartDate());
				stmt.setString(6, perPersonal.getEndDate());
				stmt.setString(7, perPersonal.getPersonIdExternal());
				stmt.setInt(8, perPersonal.getIntegrou());
				stmt.setString(9, perPersonal.getValidPersonalId());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception insertPerPersonal: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public void updatePerPersonal(List<PerPersonalVO> perPersonals) throws Exception {
		String UPDATE = "UPDATE IGITOOLS.PRE_FEED_SAP_PERPERSONAL SET LASTNAME=?, FIRSTNAME=?, PREFEREDNAME=?, GENDER=?, DT_INICIO=?, DT_FIM=?, MATRICULA=?, INTEGROU=?, MATRICULA_ATIVA=? WHERE ID=?";
		Connection conn = this.getConexaoDB2();
		
		try {
			int count = 0;
			
			for (PerPersonalVO perPersonal : perPersonals) {
				PreparedStatement stmt = conn.prepareStatement(UPDATE);
				stmt.setString(1, perPersonal.getLastName());
				stmt.setString(2, perPersonal.getFirstName());
				stmt.setString(3, perPersonal.getPreferedName());
				stmt.setString(4, perPersonal.getGender());
				stmt.setString(5, perPersonal.getStartDate());
				stmt.setString(6, perPersonal.getEndDate());
				stmt.setString(7, perPersonal.getPersonIdExternal());
				stmt.setInt(8, perPersonal.getIntegrou());
				stmt.setString(9, perPersonal.getValidPersonalId());
				stmt.setInt(10, perPersonal.getId());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception updatePerPersonal: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public List<CustWorkForceSoftwareVO> custWorkForceSoftwares() throws Exception {
		List<CustWorkForceSoftwareVO> custWorkForceSoftwareVOs = new ArrayList<>();
		String sql = "SELECT ID, USER_ID, DT_INICIO, DT_FIM, TIPO_AFASTAMENTO, DIAS_AFASTAMENTO, DT_ULT_MOD, TIPO_AFASTAMENTO_DESC, STATUS FROM IGITOOLS.PRE_FEED_SAP_CUSTWORKFORCE";
		Connection conn = this.getConexaoDB2();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				CustWorkForceSoftwareVO custWorkForceSoftware = new CustWorkForceSoftwareVO();
				custWorkForceSoftware.setId(rs.getInt("ID"));
				custWorkForceSoftware.setExternalName(rs.getString("USER_ID"));
				custWorkForceSoftware.setDateStart(rs.getString("DT_INICIO"));
				custWorkForceSoftware.setDateEnd(rs.getString("DT_FIM"));
				custWorkForceSoftware.setAbsenceType(rs.getString("TIPO_AFASTAMENTO"));
				custWorkForceSoftware.setAbsencesDays(rs.getString("DIAS_AFASTAMENTO"));
				custWorkForceSoftware.setLastModDate(rs.getString("DT_ULT_MOD"));
				custWorkForceSoftware.setAbsenceTypeDescription(rs.getString("TIPO_AFASTAMENTO_DESC"));
				custWorkForceSoftware.setStatus(rs.getString("STATUS"));
				custWorkForceSoftwareVOs.add(custWorkForceSoftware);
			}
			
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception custWorkForceSoftwares: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		} 
		finally {
			this.fecharConexao(conn);
		}
		
		return custWorkForceSoftwareVOs;
	}
	
	public void insertCustWorkForce(List<CustWorkForceSoftwareVO> custWorkForceSoftwares) throws Exception {
		String INSERT = "INSERT INTO IGITOOLS.PRE_FEED_SAP_CUSTWORKFORCE (USER_ID,DT_INICIO,DT_FIM,TIPO_AFASTAMENTO,DIAS_AFASTAMENTO,DT_ULT_MOD,STATUS,INTEGROU,TIPO_AFASTAMENTO_DESC) VALUES(?,?,?,?,?,?,?,?,?)";
		Connection conn = this.getConexaoDB2();
		
		try {
			int count = 0;
			
			for (CustWorkForceSoftwareVO custWorkForceSoftware : custWorkForceSoftwares) {
				PreparedStatement stmt = conn.prepareStatement(INSERT);
				stmt.setString(1, custWorkForceSoftware.getExternalName());
				stmt.setString(2, custWorkForceSoftware.getDateStart());
				stmt.setString(3, custWorkForceSoftware.getDateEnd());
				stmt.setString(4, custWorkForceSoftware.getAbsenceType());
				stmt.setString(5, custWorkForceSoftware.getAbsencesDays());
				stmt.setString(6, custWorkForceSoftware.getLastModDate());
				stmt.setString(7, custWorkForceSoftware.getStatus());
				stmt.setInt(8, custWorkForceSoftware.getIntegrou());
				stmt.setString(9, custWorkForceSoftware.getAbsenceTypeDescription());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception insertCustWorkForce: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public void updateCustWorkForce(List<CustWorkForceSoftwareVO> custWorkForceSoftwares) throws Exception {
		String UPDATE = "UPDATE IGITOOLS.PRE_FEED_SAP_CUSTWORKFORCE SET USER_ID=?, DT_INICIO=?, DT_FIM=?, TIPO_AFASTAMENTO=?, DIAS_AFASTAMENTO=?, DT_ULT_MOD=?, STATUS=?, INTEGROU=?, TIPO_AFASTAMENTO_DESC=? WHERE ID=?";
		Connection conn = this.getConexaoDB2();
		
		try {
			int count = 0;
			
			for (CustWorkForceSoftwareVO custWorkForceSoftware : custWorkForceSoftwares) {
				PreparedStatement stmt = conn.prepareStatement(UPDATE);
				stmt.setString(1, custWorkForceSoftware.getExternalName());
				stmt.setString(2, custWorkForceSoftware.getDateStart());
				stmt.setString(3, custWorkForceSoftware.getDateEnd());
				stmt.setString(4, custWorkForceSoftware.getAbsenceType());
				stmt.setString(5, custWorkForceSoftware.getAbsencesDays());
				stmt.setString(6, custWorkForceSoftware.getLastModDate());
				stmt.setString(7, custWorkForceSoftware.getStatus());
				stmt.setInt(8, custWorkForceSoftware.getIntegrou());
				stmt.setString(9, custWorkForceSoftware.getAbsenceTypeDescription());
				stmt.setInt(10, custWorkForceSoftware.getId());
				stmt.execute();
				
				if (count % 200 == 0) {
					conn.close();
					conn = this.getConexaoDB2();
				}
				
				count++;
			}
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception updateCustWorkForce: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		finally {
			this.fecharConexao(conn);
		}
	}
	
	public List<Account> retFeed() throws Exception {
		List<Account> list = new ArrayList<>();
		Connection conn = this.getConexaoDB2();

		try {			
			String query = "SELECT USER_ID, MATRICULA, LOGIN, FIRSTNAME, LASTNAME, PREFEREDNAME, GENDER, DT_INI_PERPERSONAL, DT_FIM_PERPERSONAL, DT_ULT_MOD_EMPJOB, CPF, DT_NASC, "
					+ "RG_NUM, DT_EMISSAO_DOC, DT_VALIDADE_DOC, STATUS_COLABORADOR, DT_INI_EMPJOB, DT_FIM_EMPJOB, MOTIVO_EVENTO, DT_INI_ATR_ORG, DT_FIM_ATR_ORG, EMPRESA, EMPRESA_DESC, "
					+ "ESTABELECIMENTO, ESTABELECIMENTO_DESC, DEPARTAMENTO, DEPARTAMENTO_DESC, CARGO, CARGO_DESC, GRUPO_EMPREGADO, SUB_GRUPO_EMPREGADO, CENTRO_CUSTO, MATRICULA_GESTOR, "
					+ "DT_ULT_MOD_ATR_ORG, STATUS_COLABORADOR_DESC, MOTIVO_EVENTO_DESC, GRUPO_EMPREGADO_DESC, SUB_GRUPO_EMPREGADO_DESC, CENTRO_CUSTO_DESC, TIPO_AFASTAMENTO, DIAS_AFASTAMENTO, "
					+ "DT_INI_AFAST, DT_FIM_AFAST, TIPO_AFASTAMENTO_DESC, DT_ULT_MOD_AFAST, CUST_STATUS, '' as PHONENUMBER, NIVELCARGO "
					+ "FROM IGITOOLS.VW_PRE_FEED_SAP_FULL WHERE 1=2";
			
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				Account usr = new Account();
				usr.setErUserId(rs.getString("USER_ID") != null ? rs.getString("USER_ID") : "");
				usr.setErMatricula(rs.getString("MATRICULA") != null ? rs.getString("MATRICULA") : "");
				usr.setErLogin(rs.getString("LOGIN") != null ? rs.getString("LOGIN") : "");
				usr.setErFirstName(rs.getString("FIRSTNAME") != null ? rs.getString("FIRSTNAME") : "");
				usr.setErLastName(rs.getString("LASTNAME") != null ? rs.getString("LASTNAME") : "");
				usr.setErPreferedName(rs.getString("PREFEREDNAME") != null ? rs.getString("PREFEREDNAME") : "");
				usr.setErGender(rs.getString("GENDER") != null ? rs.getString("GENDER") : "");
				usr.setErDtIniPerpersonal(rs.getString("DT_INI_PERPERSONAL") != null ? rs.getString("DT_INI_PERPERSONAL") : "");
				usr.setErDtFimPerpersonal(rs.getString("DT_FIM_PERPERSONAL") != null ? rs.getString("DT_FIM_PERPERSONAL") : "");
				usr.setErDtUltModEmpjob(rs.getString("DT_ULT_MOD_EMPJOB") != null ? rs.getString("DT_ULT_MOD_EMPJOB") : "");
				usr.setErCpf(rs.getString("CPF") != null ? rs.getString("CPF") : "");
				usr.setErDtNasc(rs.getString("DT_NASC") != null ? rs.getString("DT_NASC") : "");
				usr.setErRgNum(rs.getString("RG_NUM") != null ? rs.getString("RG_NUM") : "");
				usr.setErDtEmissaoDoc(rs.getString("DT_EMISSAO_DOC") != null ? rs.getString("DT_EMISSAO_DOC") : "");
				usr.setErDtValidadeDoc(rs.getString("DT_VALIDADE_DOC") != null ? rs.getString("DT_VALIDADE_DOC") : "");
				usr.setErStatusColaborador(rs.getString("STATUS_COLABORADOR") != null ? rs.getString("STATUS_COLABORADOR") : "");
				usr.setErDtIniEmpjob(rs.getString("DT_INI_EMPJOB") != null ? rs.getString("DT_INI_EMPJOB") : "");
				usr.setErDtFimEmpjob(rs.getString("DT_FIM_EMPJOB") != null ? rs.getString("DT_FIM_EMPJOB") : "");
				usr.setErMotivoEvento(rs.getString("MOTIVO_EVENTO") != null ? rs.getString("MOTIVO_EVENTO") : "");
				usr.setErDtIniItrOrg(rs.getString("DT_INI_ATR_ORG") != null ? rs.getString("DT_INI_ATR_ORG") : "");
				usr.setErDtFimAtrOrg(rs.getString("DT_FIM_ATR_ORG") != null ? rs.getString("DT_FIM_ATR_ORG") : "");
				usr.setErEmpresa(rs.getString("EMPRESA") != null ? rs.getString("EMPRESA") : "");
				usr.setErEmpresaDesc(rs.getString("EMPRESA_DESC") != null ? rs.getString("EMPRESA_DESC") : "");
				usr.setErEstabelecimento(rs.getString("ESTABELECIMENTO") != null ? rs.getString("ESTABELECIMENTO") : "");
				usr.setErEstabelecimentoDesc(rs.getString("ESTABELECIMENTO_DESC") != null ? rs.getString("ESTABELECIMENTO_DESC") : "");
				usr.setErDepartamento(rs.getString("DEPARTAMENTO") != null ? rs.getString("DEPARTAMENTO") : "");
				usr.setErDepartamentoDesc(rs.getString("DEPARTAMENTO_DESC") != null ? rs.getString("DEPARTAMENTO_DESC") : "");
				usr.setErCargo(rs.getString("CARGO") != null ? rs.getString("CARGO") : "");
				usr.setErCargoDesc(rs.getString("CARGO_DESC") != null ? rs.getString("CARGO_DESC") : "");
				usr.setErGrupoEmpregado(rs.getString("GRUPO_EMPREGADO") != null ? rs.getString("GRUPO_EMPREGADO") : "");
				usr.setErSubGrupoEmpregado(rs.getString("SUB_GRUPO_EMPREGADO") != null ? rs.getString("SUB_GRUPO_EMPREGADO") : "");
				usr.setErCentroCusto(rs.getString("CENTRO_CUSTO") != null ? rs.getString("CENTRO_CUSTO") : "");
				usr.setErMatriculaGestor(rs.getString("MATRICULA_GESTOR") != null ? rs.getString("MATRICULA_GESTOR") : "");
				usr.setErDtUltModAtrOrg(rs.getString("DT_ULT_MOD_ATR_ORG") != null ? rs.getString("DT_ULT_MOD_ATR_ORG") : "");
				usr.setErStatusColaboradorDesc(rs.getString("STATUS_COLABORADOR_DESC") != null ? rs.getString("STATUS_COLABORADOR_DESC") : "");
				usr.setErMotivoEventoDesc(rs.getString("MOTIVO_EVENTO_DESC") != null ? rs.getString("MOTIVO_EVENTO_DESC") : "");
				usr.setErGrupoEmpregadoDesc(rs.getString("GRUPO_EMPREGADO_DESC") != null ? rs.getString("GRUPO_EMPREGADO_DESC") : "");
				usr.setErSubGrupoEmpregadoDesc(rs.getString("SUB_GRUPO_EMPREGADO_DESC") != null ? rs.getString("SUB_GRUPO_EMPREGADO_DESC") : "");
				usr.setErCentroCustoDesc(rs.getString("CENTRO_CUSTO_DESC") != null ? rs.getString("CENTRO_CUSTO_DESC") : "");
				usr.setErTipoAfastamento(rs.getString("TIPO_AFASTAMENTO") != null ? rs.getString("TIPO_AFASTAMENTO") : "");
				usr.setErDiasAfastamento(rs.getString("DIAS_AFASTAMENTO") != null ? rs.getString("DIAS_AFASTAMENTO") : "");
				usr.setErDtIniAfast(rs.getString("DT_INI_AFAST") != null ? rs.getString("DT_INI_AFAST") : "");
				usr.setErDtFimAfast(rs.getString("DT_FIM_AFAST") != null ? rs.getString("DT_FIM_AFAST") : "");
				usr.setErTipoAfastamentoDesc(rs.getString("TIPO_AFASTAMENTO_DESC") != null ? rs.getString("TIPO_AFASTAMENTO_DESC") : "");
				usr.setErDtUltModAfast(rs.getString("DT_ULT_MOD_AFAST") != null ? rs.getString("DT_ULT_MOD_AFAST") : "");
				usr.setErCustStatus(rs.getString("CUST_STATUS") != null ? rs.getString("CUST_STATUS") : "");
				usr.setErPhoneNumber(rs.getString("PHONENUMBER") != null ? rs.getString("PHONENUMBER") : "");
				usr.setErNivelCargo(rs.getString("NIVELCARGO") != null ? rs.getString("NIVELCARGO") : "");
				list.add(usr);
			}
			
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			logger.error("[PerPersonals] Exception retFeed: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
		finally {
			this.fecharConexao(conn);
		}
		
		return list;
	}

	private Connection getConexaoDB2() throws Exception {
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
			logger.error("[PerPersonals] Exception getConexaoDB2: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
		catch (SQLException e) {
			logger.error("[PerPersonals] Exception getConexaoDB2: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param connection
	 */
	private void fecharConexao(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			}
			catch (SQLException e) {
				logger.error("[PerPersonals] Exception fecharConexao: " + ExceptionUtils.getStackTrace(e));
			}
		}
	}
}
