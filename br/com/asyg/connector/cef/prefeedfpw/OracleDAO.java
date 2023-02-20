package br.com.asyg.connector.cef.prefeedfpw;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.asyg.connector.cef.prefeedfpw.model.User;

public class OracleDAO {
	private Connection conn = null;

	public OracleDAO(Connection conn_igi) {
		this.conn = conn_igi;
	}

	public List<String> listEnrollmentPersonPreFeed(String mattable) throws Exception {
		List<String> listaPessoas = new ArrayList<>();
		String sql = "select FUPESSOAID from  " + mattable;

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				listaPessoas.add(rs.getString("FUPESSOAID"));
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			throw new Exception("[OracleDAO] message: Erro SqlServer: " + e.getLocalizedMessage());
		}

		return listaPessoas;
	}

	public List<User> listPersonPreFeed(String prefeedtable) throws Exception {
		List<User> listaPessoasPreFeed = new ArrayList<User>();

		String sql = "SELECT  FUMatFunc, FUNomFunc, FUSexFunc, FUDtNasc, FUCodSitu, FUDtIniSit, FUDtAdmis, FUCodLot, FUCodCargo, FUIndBateP, FUIndIRRF, FUCPF, FUOutDcTip, FUOutDcNum,FUCentrCus, "
				+ "FUEmail, FUCelular, FUCodGrpHie, FUEmail2, FUTipoNecEsp, FUPessoaID, FUCODEMP, GHCODGRPHIE, GHNUMERONIVELHIERARQUICO, GHDESC, GHCODGRPHIERARQUICOSUPERIOR,CADESCARGO, "
				+ "CARGCODHIE, LODESCLOT, AEVALOR, GHDESCRESUM, OFMatFunc, OFDtIniOco, OFDtFinOco, OFNumDiaAx, OFNumDiaFe, OFCODOCORR, OPNOMOPER, DTH_ALTERACAO_FUNCIONARIO,GESTOR, MAT_GESTOR, HIERDESHIE, HIERCODEHIE "
				+ "FROM " + prefeedtable;

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				User pessoaVO = new User();

				pessoaVO.setFUMatFunc(this.intOrNull(rs.getObject("fUMatFunc")));
				pessoaVO.setFUNomFunc(rs.getString("fUNomFunc"));
				pessoaVO.setFUSexFunc(rs.getString("fUSexFunc"));
				pessoaVO.setFUDtNasc(this.intOrNull(rs.getObject("fUDtNasc")));
				pessoaVO.setFUCodSitu(this.intOrNull(rs.getObject("fUCodSitu")));
				pessoaVO.setFUDtIniSit(this.intOrNull(rs.getObject("fUDtIniSit")));
				pessoaVO.setFUDtAdmis(this.intOrNull(rs.getObject("fUDtAdmis")));
				pessoaVO.setFUCodLot(rs.getFloat("fUCodLot"));
				pessoaVO.setFUCodCargo(this.intOrNull(rs.getObject("fUCodCargo")));
				pessoaVO.setFUIndBateP(rs.getString("fUIndBateP"));
				pessoaVO.setFUIndIRRF(rs.getString("fUIndIRRF"));
				pessoaVO.setFUCPF(rs.getBigDecimal("fUCPF"));
				pessoaVO.setFUOutDcTip(rs.getString("fUOutDcTip"));
				pessoaVO.setFUOutDcNum(rs.getFloat("fUOutDcNum"));
				pessoaVO.setFUCentrCus(rs.getString("fUCentrCus"));

				String email = rs.getString("fUEmail");

				if (email == null)
					email = "";

				pessoaVO.setFUEmail(email);

				String fuCelular = rs.getString("fUCelular");

				if (fuCelular == null)
					fuCelular = "";

				pessoaVO.setFUCelular(fuCelular);
				pessoaVO.setFUCodGrpHie(rs.getString("fUCodGrpHie"));
				pessoaVO.setFUEmail2(rs.getString("fUEmail2"));
				pessoaVO.setFUTipoNecEsp(this.intOrNull(rs.getObject("fUTipoNecEsp")));
				pessoaVO.setFUPessoaID(rs.getString("fUPessoaID"));
				pessoaVO.setFUCODEMP(this.intOrNull(rs.getObject("fUCODEMP")));
				pessoaVO.setGHCODGRPHIE(rs.getString("gHCODGRPHIE"));
				pessoaVO.setGHNUMERONIVELHIERARQUICO(rs.getString("gHNUMERONIVELHIERARQUICO"));
				pessoaVO.setGHDESC(rs.getString("gHDESC"));
				pessoaVO.setGHCODGRPHIERARQUICOSUPERIOR(rs.getString("gHCODGRPHIERARQUICOSUPERIOR"));
				pessoaVO.setCADESCARGO(rs.getString("cADESCARGO"));
				pessoaVO.setCARGCODHIE(this.intOrNull(rs.getObject("cARGCODHIE")));
				pessoaVO.setLODESCLOT(rs.getString("lODESCLOT"));
				pessoaVO.setAEVALOR(rs.getString("aEVALOR"));
				pessoaVO.setGHDESCRESUM(rs.getString("gHDESCRESUM"));
				pessoaVO.setOFMatFunc(this.intOrNull(rs.getObject("oFMatFunc")));
				pessoaVO.setOFDtIniOco(this.intOrNull(rs.getObject("oFDtIniOco")));
				pessoaVO.setOFDtFinOco(this.intOrNull(rs.getObject("oFDtFinOco")));
				pessoaVO.setOFNumDiaAx(this.intOrNull(rs.getObject("oFNumDiaAx")));
				pessoaVO.setOFNumDiaFe(this.intOrNull(rs.getObject("oFNumDiaFe")));
				pessoaVO.setOFCODOCORR(this.intOrNull(rs.getObject("oFCODOCORR")));
				pessoaVO.setOPNOMOPER(rs.getString("OPNOMOPER"));
				pessoaVO.setGESTOR(rs.getString("GESTOR"));
				pessoaVO.setMAT_GESTOR(rs.getString("MAT_GESTOR"));
				pessoaVO.setHierDesHie(rs.getString("HIERDESHIE"));
				pessoaVO.setHierCodeHie(rs.getInt("HIERCODEHIE"));

				listaPessoasPreFeed.add(pessoaVO);
			}

			rs.close();
			stmt.close();
		} 
		catch (Exception e) {
			throw new Exception("[OracleDAO] message: Erro Oracle: " + e.getLocalizedMessage());
		}

		return listaPessoasPreFeed;
	}

	public void insertPersonId(String mattable, String pessoaId) throws Exception {
		String INSERT = "insert into " + mattable + "(FUPESSOAID) values (?)";

		try {
			PreparedStatement stmt = conn.prepareStatement(INSERT);

			stmt.setString(1, pessoaId);
			stmt.executeUpdate();
			stmt.close();
		} 
		catch (Exception e) {
			System.out.println("[OracleDAO] message: Erro: " + e.getLocalizedMessage());
			throw new Exception(e.getMessage());
		}
	}

	public Integer calculateEventWeight(String ocorrencias, String pesotable) {
		Integer retorno = null;
		String sql = "select OFCODOCORR from " + pesotable + " where OFCODOCORR in (" + ocorrencias + ") order by peso desc";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			// somente a primeira
			if (rs.next()) {
				retorno = rs.getInt("OFCODOCORR");
			}

			rs.close();
			stmt.close();
		} 
		catch (Exception e) {
			System.out.println("[OracleDAO] message: Erro: " + e.getMessage());
		}
		return retorno;
	}

	public void insertPerson(String prefeedtable, User pessoaVO) throws Exception {
		String INSERT = "insert into " + prefeedtable + "(FUMatFunc, FUNomFunc, FUSexFunc, FUDtNasc, FUCodSitu, FUDtIniSit, FUDtAdmis, FUCodLot, FUCodCargo, FUIndBateP, FUIndIRRF, FUCPF, FUOutDcTip, FUOutDcNum,FUCentrCus, "
				+ "FUEmail, FUCelular, FUCodGrpHie, FUEmail2, FUTipoNecEsp, FUPessoaID, FUCODEMP, GHCODGRPHIE, GHNUMERONIVELHIERARQUICO, GHDESC, GHCODGRPHIERARQUICOSUPERIOR,CADESCARGO, "
				+ "CARGCODHIE, LODESCLOT, AEVALOR, GHDESCRESUM, OFMatFunc, OFDtIniOco, OFDtFinOco, OFNumDiaAx, OFNumDiaFe, OFCODOCORR, OPNOMOPER ,GESTOR, MAT_GESTOR, HIERDESHIE, HIERCODEHIE) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

		try {
			PreparedStatement stmt = conn.prepareStatement(INSERT);

			if (pessoaVO.getFUMatFunc() != null)
				stmt.setInt(1, pessoaVO.getFUMatFunc());
			else
				stmt.setNull(1, java.sql.Types.INTEGER);

			stmt.setString(2, pessoaVO.getFUNomFunc());
			stmt.setString(3, pessoaVO.getFUSexFunc());

			if (pessoaVO.getFUDtNasc() != null)
				stmt.setInt(4, pessoaVO.getFUDtNasc());
			else
				stmt.setNull(4, java.sql.Types.INTEGER);

			if (pessoaVO.getFUCodSitu() != null)
				stmt.setInt(5, pessoaVO.getFUCodSitu());
			else
				stmt.setNull(5, java.sql.Types.INTEGER);

			if (pessoaVO.getFUDtIniSit() != null)
				stmt.setInt(6, pessoaVO.getFUDtIniSit());
			else
				stmt.setNull(6, java.sql.Types.INTEGER);

			if (pessoaVO.getFUDtAdmis() != null)
				stmt.setInt(7, pessoaVO.getFUDtAdmis());
			else
				stmt.setNull(7, java.sql.Types.INTEGER);

			stmt.setDouble(8, pessoaVO.getFUCodLot());

			if (pessoaVO.getFUCodCargo() != null)
				stmt.setInt(9, pessoaVO.getFUCodCargo());
			else
				stmt.setNull(9, java.sql.Types.INTEGER);

			stmt.setString(10, pessoaVO.getFUIndBateP());
			stmt.setString(11, pessoaVO.getFUIndIRRF());
			stmt.setBigDecimal(12, pessoaVO.getFUCPF());
			stmt.setString(13, pessoaVO.getFUOutDcTip());
			stmt.setFloat(14, pessoaVO.getFUOutDcNum());
			stmt.setString(15, pessoaVO.getFUCentrCus());
			stmt.setString(16, pessoaVO.getFUEmail());
			stmt.setString(17, pessoaVO.getFUCelular());
			stmt.setString(18, pessoaVO.getFUCodGrpHie());
			stmt.setString(19, pessoaVO.getFUEmail2());

			if (pessoaVO.getFUTipoNecEsp() != null)
				stmt.setInt(20, pessoaVO.getFUTipoNecEsp());
			else
				stmt.setNull(20, java.sql.Types.INTEGER);

			stmt.setString(21, pessoaVO.getFUPessoaID());

			if (pessoaVO.getFUCODEMP() != null)
				stmt.setInt(22, pessoaVO.getFUCODEMP());
			else
				stmt.setNull(22, java.sql.Types.INTEGER);

			stmt.setString(23, pessoaVO.getGHCODGRPHIE());
			stmt.setString(24, pessoaVO.getGHNUMERONIVELHIERARQUICO());
			stmt.setString(25, pessoaVO.getGHDESC());
			stmt.setString(26, pessoaVO.getGHCODGRPHIERARQUICOSUPERIOR());
			stmt.setString(27, pessoaVO.getCADESCARGO());

			if (pessoaVO.getCARGCODHIE() != null)
				stmt.setInt(28, pessoaVO.getCARGCODHIE());
			else
				stmt.setNull(28, java.sql.Types.INTEGER);

			stmt.setString(29, pessoaVO.getLODESCLOT());
			stmt.setString(30, pessoaVO.getAEVALOR());
			stmt.setString(31, pessoaVO.getGHDESCRESUM());

			if (pessoaVO.getOFMatFunc() != null)
				stmt.setInt(32, pessoaVO.getOFMatFunc());
			else
				stmt.setNull(32, java.sql.Types.INTEGER);

			if (pessoaVO.getOFDtIniOco() != null)
				stmt.setInt(33, pessoaVO.getOFDtIniOco());
			else
				stmt.setNull(33, java.sql.Types.INTEGER);

			if (pessoaVO.getOFDtFinOco() != null)
				stmt.setInt(34, pessoaVO.getOFDtFinOco());
			else
				stmt.setNull(34, java.sql.Types.INTEGER);

			if (pessoaVO.getOFNumDiaAx() != null)
				stmt.setInt(35, pessoaVO.getOFNumDiaAx());
			else
				stmt.setNull(35, java.sql.Types.INTEGER);

			if (pessoaVO.getOFNumDiaFe() != null)
				stmt.setInt(36, pessoaVO.getOFNumDiaFe());
			else
				stmt.setNull(36, java.sql.Types.INTEGER);

			if (pessoaVO.getOFCODOCORR() != null)
				stmt.setInt(37, pessoaVO.getOFCODOCORR());
			else
				stmt.setNull(37, java.sql.Types.INTEGER);

			stmt.setString(38, pessoaVO.getOPNOMOPER());
			stmt.setString(39, pessoaVO.getGESTOR());
			stmt.setString(40, pessoaVO.getMAT_GESTOR());

			stmt.setString(41, pessoaVO.getHierDesHie());

			if (pessoaVO.getHierCodeHie() != null) {
				stmt.setInt(42, pessoaVO.getHierCodeHie());
			} else {
				stmt.setNull(42, java.sql.Types.INTEGER);
			}

			stmt.executeUpdate();
			stmt.close();
		} 
		catch (SQLException e) {
			throw new Exception("[OracleDAO] message: Erro: " + e.getLocalizedMessage());
		}
	}

	public void updatePerson(String prefeedtable, User pessoaVO) throws Exception {
		String INSERT = "update " + prefeedtable + " set FUMatFunc = ?,   FUNomFunc = ?,   FUSexFunc = ? ,   FUDtNasc = ? ,   FUCodSitu = ? ,   FUDtIniSit = ? ,   FUDtAdmis = ? ,   FUCodLot = ? , "
				+ "FUCodCargo = ? ,   FUIndBateP = ? ,   FUIndIRRF = ? ,   FUCPF = ? ,   FUOutDcTip = ? ,   FUOutDcNum = ?, FUCentrCus = ?,  "
				+ "FUEmail = ?,  FUCelular = ?,  FUCodGrpHie = ?,  FUEmail2 = ?,  FUTipoNecEsp = ?,  FUPessoaID = ?,  FUCODEMP = ?,  GHCODGRPHIE = ?,   "
				+ "GHNUMERONIVELHIERARQUICO = ?,  GHDESC = ?,  GHCODGRPHIERARQUICOSUPERIOR = ?, CADESCARGO = ?,  "
				+ "CARGCODHIE = ?,  LODESCLOT = ?,  AEVALOR = ?,  GHDESCRESUM = ?,  OFMatFunc = ?,  OFDtIniOco = ?,  OFDtFinOco = ?,   "
				+ "OFNumDiaAx = ?,  OFNumDiaFe = ?,  OFCODOCORR = ?,  OPNOMOPER  = ?, GESTOR = ?,  MAT_GESTOR = ?, HIERDESHIE = ?, HIERCODEHIE = ?, alterado = sysdate where FUPessoaID =  ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(INSERT);

			if (pessoaVO.getFUMatFunc() != null)
				stmt.setInt(1, pessoaVO.getFUMatFunc());
			else
				stmt.setNull(1, java.sql.Types.INTEGER);

			stmt.setString(2, pessoaVO.getFUNomFunc());
			stmt.setString(3, pessoaVO.getFUSexFunc());

			if (pessoaVO.getFUDtNasc() != null)
				stmt.setInt(4, pessoaVO.getFUDtNasc());
			else
				stmt.setNull(4, java.sql.Types.INTEGER);

			if (pessoaVO.getFUCodSitu() != null)
				stmt.setInt(5, pessoaVO.getFUCodSitu());
			else
				stmt.setNull(5, java.sql.Types.INTEGER);

			if (pessoaVO.getFUDtIniSit() != null)
				stmt.setInt(6, pessoaVO.getFUDtIniSit());
			else
				stmt.setNull(6, java.sql.Types.INTEGER);

			if (pessoaVO.getFUDtAdmis() != null)
				stmt.setInt(7, pessoaVO.getFUDtAdmis());
			else
				stmt.setNull(7, java.sql.Types.INTEGER);

			stmt.setDouble(8, pessoaVO.getFUCodLot());

			if (pessoaVO.getFUCodCargo() != null)
				stmt.setInt(9, pessoaVO.getFUCodCargo());
			else
				stmt.setNull(9, java.sql.Types.INTEGER);

			stmt.setString(10, pessoaVO.getFUIndBateP());
			stmt.setString(11, pessoaVO.getFUIndIRRF());
			stmt.setBigDecimal(12, pessoaVO.getFUCPF());
			stmt.setString(13, pessoaVO.getFUOutDcTip());
			stmt.setFloat(14, pessoaVO.getFUOutDcNum());
			stmt.setString(15, pessoaVO.getFUCentrCus());
			stmt.setString(16, pessoaVO.getFUEmail());
			stmt.setString(17, pessoaVO.getFUCelular());
			stmt.setString(18, pessoaVO.getFUCodGrpHie());
			stmt.setString(19, pessoaVO.getFUEmail2());

			if (pessoaVO.getFUTipoNecEsp() != null)
				stmt.setInt(20, pessoaVO.getFUTipoNecEsp());
			else
				stmt.setNull(20, java.sql.Types.INTEGER);

			stmt.setString(21, pessoaVO.getFUPessoaID());

			if (pessoaVO.getFUCODEMP() != null)
				stmt.setInt(22, pessoaVO.getFUCODEMP());
			else
				stmt.setNull(22, java.sql.Types.INTEGER);

			stmt.setString(23, pessoaVO.getGHCODGRPHIE());
			stmt.setString(24, pessoaVO.getGHNUMERONIVELHIERARQUICO());
			stmt.setString(25, pessoaVO.getGHDESC());
			stmt.setString(26, pessoaVO.getGHCODGRPHIERARQUICOSUPERIOR());
			stmt.setString(27, pessoaVO.getCADESCARGO());

			if (pessoaVO.getCARGCODHIE() != null)
				stmt.setInt(28, pessoaVO.getCARGCODHIE());
			else
				stmt.setNull(28, java.sql.Types.INTEGER);

			stmt.setString(29, pessoaVO.getLODESCLOT());
			stmt.setString(30, pessoaVO.getAEVALOR());
			stmt.setString(31, pessoaVO.getGHDESCRESUM());

			if (pessoaVO.getOFMatFunc() != null)
				stmt.setInt(32, pessoaVO.getOFMatFunc());
			else
				stmt.setNull(32, java.sql.Types.INTEGER);

			if (pessoaVO.getOFDtIniOco() != null)
				stmt.setInt(33, pessoaVO.getOFDtIniOco());
			else
				stmt.setNull(33, java.sql.Types.INTEGER);

			if (pessoaVO.getOFDtFinOco() != null)
				stmt.setInt(34, pessoaVO.getOFDtFinOco());
			else
				stmt.setNull(34, java.sql.Types.INTEGER);

			if (pessoaVO.getOFNumDiaAx() != null)
				stmt.setInt(35, pessoaVO.getOFNumDiaAx());
			else
				stmt.setNull(35, java.sql.Types.INTEGER);

			if (pessoaVO.getOFNumDiaFe() != null)
				stmt.setInt(36, pessoaVO.getOFNumDiaFe());
			else
				stmt.setNull(36, java.sql.Types.INTEGER);

			if (pessoaVO.getOFCODOCORR() != null)
				stmt.setInt(37, pessoaVO.getOFCODOCORR());
			else
				stmt.setNull(37, java.sql.Types.INTEGER);

			stmt.setString(38, pessoaVO.getOPNOMOPER());
			stmt.setString(39, pessoaVO.getGESTOR());
			stmt.setString(40, pessoaVO.getMAT_GESTOR());

			stmt.setString(41, pessoaVO.getHierDesHie());

			if (pessoaVO.getHierCodeHie() != null) {
				stmt.setInt(42, pessoaVO.getHierCodeHie());
			} else {
				stmt.setNull(42, java.sql.Types.INTEGER);
			}

			stmt.setString(43, pessoaVO.getFUPessoaID());

			stmt.executeUpdate();
			stmt.close();
		} 
		catch (Exception e) {
			throw new Exception("[OracleDAO] message: Erro: " + e.getLocalizedMessage());
		}
	}

	private Integer intOrNull(Object valor) {
		Integer retorno = null;

		if (valor instanceof BigDecimal) {
			BigDecimal retornoBD = (BigDecimal) valor;
			retorno = retornoBD.intValue();
		}

		if (valor instanceof Integer) {
			retorno = (Integer) valor;
		}

		return retorno;
	}
}
