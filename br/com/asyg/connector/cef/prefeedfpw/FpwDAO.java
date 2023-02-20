package br.com.asyg.connector.cef.prefeedfpw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.asyg.connector.cef.prefeedfpw.model.User;

public class FpwDAO {
	private Connection conn = null;

	public FpwDAO(Connection conn_fpw) {
		this.conn = conn_fpw;
	}

	public List<String> listFuPersonIDs(String instancia, String fpwtable) throws Exception {
		List<String> listaPessoas = new ArrayList<>();
		String sql = "SELECT fupessoaid FROM " + instancia + "." + fpwtable + " where fupessoaid like '0000%' group by fupessoaid ";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				listaPessoas.add(rs.getString("fupessoaid"));
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			throw new Exception("[FpwDAO] message: Erro SqlServer: " + e.getLocalizedMessage());
		}

		return listaPessoas;
	}

	public List<User> checksPersonFPW(String fuPessoaID, String QUERY, boolean doisParametros, Connection conn, Integer dataOcorrencia) throws Exception {
		List<User> listaPessoas = new ArrayList<>();

		try {
			PreparedStatement stmt = conn.prepareStatement(QUERY);
			stmt.setString(1, fuPessoaID);

			if (doisParametros) {
				if (dataOcorrencia == null) {
					stmt.setString(2, fuPessoaID);
				} else {
					stmt.setInt(2, dataOcorrencia);
				}
			}

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				User pessoaVO = new User();

				Integer fUMatFunc = (Integer) rs.getObject("fUMatFunc");
				pessoaVO.setFUMatFunc(fUMatFunc);

				pessoaVO.setFUNomFunc(rs.getString("fUNomFunc"));
				pessoaVO.setFUSexFunc(rs.getString("fUSexFunc"));

				Integer fUDtNasc = (Integer) rs.getObject("fUDtNasc");
				pessoaVO.setFUDtNasc(fUDtNasc);

				Integer fUCodSitu = (Integer) rs.getObject("fUCodSitu");
				pessoaVO.setFUCodSitu(fUCodSitu);

				Integer fUDtIniSit = (Integer) rs.getObject("fUDtIniSit");
				pessoaVO.setFUDtIniSit(fUDtIniSit);

				Integer fUDtAdmis = (Integer) rs.getObject("fUDtAdmis");
				pessoaVO.setFUDtAdmis(fUDtAdmis);
				pessoaVO.setFUCodLot(rs.getFloat("fUCodLot"));

				Integer fUCodCargo = (Integer) rs.getObject("fUCodCargo");
				pessoaVO.setFUCodCargo(fUCodCargo);

				pessoaVO.setFUIndBateP(rs.getString("fUIndBateP"));
				pessoaVO.setFUIndIRRF(rs.getString("fUIndIRRF"));
				pessoaVO.setFUCPF(rs.getBigDecimal("fUCPF"));
				pessoaVO.setFUOutDcTip(rs.getString("fUOutDcTip"));
				pessoaVO.setFUOutDcNum(rs.getFloat("fUOutDcNum"));
				pessoaVO.setFUCentrCus(rs.getString("fUCentrCus"));
				pessoaVO.setFUEmail(rs.getString("fUEmail"));
				pessoaVO.setFUCelular(rs.getString("fUCelular"));
				pessoaVO.setFUCodGrpHie(rs.getString("fUCodGrpHie"));
				pessoaVO.setFUEmail2(rs.getString("fUEmail2"));

				Integer fUTipoNecEsp = (Integer) rs.getObject("fUTipoNecEsp");
				pessoaVO.setFUTipoNecEsp(fUTipoNecEsp);
				pessoaVO.setFUPessoaID(rs.getString("fUPessoaID"));

				Integer fUCODEMP = (Integer) rs.getObject("fUCODEMP");
				pessoaVO.setFUCODEMP(fUCODEMP);
				pessoaVO.setGHCODGRPHIE(rs.getString("gHCODGRPHIE"));
				pessoaVO.setGHNUMERONIVELHIERARQUICO(rs.getString("gHNUMERONIVELHIERARQUICO"));
				pessoaVO.setGHDESC(rs.getString("gHDESC"));
				pessoaVO.setGHCODGRPHIERARQUICOSUPERIOR(rs.getString("gHCODGRPHIERARQUICOSUPERIOR"));
				pessoaVO.setCADESCARGO(rs.getString("cADESCARGO"));

				Integer cARGCODHIE = (Integer) rs.getObject("cARGCODHIE");
				pessoaVO.setCARGCODHIE(cARGCODHIE);
				pessoaVO.setLODESCLOT(rs.getString("lODESCLOT"));
				pessoaVO.setAEVALOR(rs.getString("aEVALOR"));
				pessoaVO.setGHDESCRESUM(rs.getString("gHDESCRESUM"));

				Integer oFMatFunc = (Integer) rs.getObject("oFMatFunc");
				pessoaVO.setOFMatFunc(oFMatFunc);

				Integer oFDtIniOco = (Integer) rs.getObject("oFDtIniOco");
				pessoaVO.setOFDtIniOco(oFDtIniOco);

				Integer oFDtFinOco = (Integer) rs.getObject("oFDtFinOco");
				pessoaVO.setOFDtFinOco(oFDtFinOco);

				Integer oFNumDiaAx = (Integer) rs.getObject("oFNumDiaAx");
				pessoaVO.setOFNumDiaAx(oFNumDiaAx);

				Integer oFNumDiaFe = (Integer) rs.getObject("oFNumDiaFe");
				pessoaVO.setOFNumDiaFe(oFNumDiaFe);

				Integer oFCODOCORR = (Integer) rs.getObject("oFCODOCORR");
				pessoaVO.setOFCODOCORR(oFCODOCORR);
				pessoaVO.setOPNOMOPER(rs.getString("OPNOMOPER"));
				pessoaVO.setGESTOR(rs.getString("GESTOR"));
				pessoaVO.setMAT_GESTOR(rs.getString("MAT_GESTOR"));
				pessoaVO.setHierDesHie(rs.getString("HIERDESHIE"));
				pessoaVO.setHierCodeHie(rs.getInt("HIERCODHIE"));

				listaPessoas.add(pessoaVO);
			}

			rs.close();
			stmt.close();
		} 
		catch (SQLException e) {
			throw new Exception("[FpwDAO] message: Erro SqlServer: " + e.getLocalizedMessage());
		}

		return listaPessoas;
	}
}
