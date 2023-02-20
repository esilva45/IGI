package br.com.asyg.connector.cef.prefeedfpw;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.List;

import br.com.asyg.connector.cef.prefeedfpw.model.User;
import br.com.asyg.connector.exceptions.ConnectorException;

public class Utils {
	
	public final static Connection getConnection(String driver, String url, String username, String password) throws Exception, ClassNotFoundException {

		try {
    		Class.forName(driver);
    		return DriverManager.getConnection(url, username, password);            
        } 
		catch (Exception e) {
			System.out.println("Exception Connection: " + e);
			throw new ConnectorException("Exception Connection: " + e);
		}
	}
	
	public static boolean temDesligamento(List<User> pessoas) {
		Integer desligamento = 1004;

		for (User pessoaVO : pessoas) {
			if (desligamento.equals(pessoaVO.getOFCODOCORR())) {
				return true;
			}
		}

		return false;
	}
	
	public static User ocorrenciaValida(String pesotable, List<User> pessoas, OracleDAO feedDAO) {
		User pessoaRetorno = null;
		// Aqui calcula o peso das ocorrencias

		String ocorrencias = "";

		for (User pessoaVO : pessoas) {
			if (pessoaVO.getOFCODOCORR() != null) {
				ocorrencias = ocorrencias + pessoaVO.getOFCODOCORR() + ",";
			}
		}

		ocorrencias = ocorrencias.substring(0, ocorrencias.length() - 1);

		Integer ocorrenciaPesoMaior = feedDAO.calculateEventWeight(ocorrencias, pesotable);

		for (User pessoaVO : pessoas) {
			if (pessoaVO.getOFCODOCORR() != null) {
				if (pessoaVO.getOFCODOCORR().equals(ocorrenciaPesoMaior)) {
					pessoaRetorno = pessoaVO;
				}
			}
		}

		return pessoaRetorno;
	}
	
	public static boolean checkEquals(User usuarioNoIGI, User pessoaFPW) {

		if (usuarioNoIGI.getAEVALOR() == null) {
			if (pessoaFPW.getAEVALOR() != null)
				return false;
		} else if (!usuarioNoIGI.getAEVALOR().equals(pessoaFPW.getAEVALOR())) {
			return false;
		}

		String CADESCARGO_FPW = "";

		if (pessoaFPW.getCADESCARGO() != null) {
			CADESCARGO_FPW = Normalizer.normalize(pessoaFPW.getCADESCARGO(), Normalizer.Form.NFD);
			CADESCARGO_FPW = CADESCARGO_FPW.replaceAll("[^\\p{ASCII}]", "");
		}

		String CADESCARGO_IGI = "";

		if (usuarioNoIGI.getCADESCARGO() != null) {
			CADESCARGO_IGI = Normalizer.normalize(usuarioNoIGI.getCADESCARGO(), Normalizer.Form.NFD);
			CADESCARGO_IGI = CADESCARGO_IGI.replaceAll("[^\\p{ASCII}]", "");
		}

		if (CADESCARGO_IGI == null) {
			if (CADESCARGO_FPW != null)
				return false;
		} else if (!CADESCARGO_IGI.equals(CADESCARGO_FPW)) {
			return false;
		}

		if (usuarioNoIGI.getCARGCODHIE() == null) {
			if (pessoaFPW.getCARGCODHIE() != null)
				return false;
		} else if (!usuarioNoIGI.getCARGCODHIE().equals(pessoaFPW.getCARGCODHIE())) {
			return false;
		}

		if (usuarioNoIGI.getFUCODEMP() == null) {
			if (pessoaFPW.getFUCODEMP() != null)
				return false;
		} else if (!usuarioNoIGI.getFUCODEMP().equals(pessoaFPW.getFUCODEMP())) {
			return false;
		}

		DecimalFormat decimalFormat = new DecimalFormat("#");

		BigDecimal cpfIGI = usuarioNoIGI.getFUCPF();
		BigDecimal cpfFPW = pessoaFPW.getFUCPF();

		if (cpfIGI == null)
			cpfIGI = new BigDecimal(0);

		if (cpfFPW == null)
			cpfFPW = new BigDecimal(0);

		String cpfStringIGI = decimalFormat.format(cpfIGI);

		String cpfStringFPW = decimalFormat.format(cpfFPW);

		if (cpfStringIGI == null) {
			if (cpfStringFPW != null)
				return false;
		} else if (!cpfStringIGI.equals(cpfStringFPW)) {
			return false;
		}

		if (usuarioNoIGI.getFUCelular() == null) {
			if (pessoaFPW.getFUCelular() != null)
				return false;
		} else if (!usuarioNoIGI.getFUCelular().equals(pessoaFPW.getFUCelular())) {
			return false;
		}

		if (usuarioNoIGI.getFUCentrCus() == null) {
			if (pessoaFPW.getFUCentrCus() != null)
				return false;
		} else if (!usuarioNoIGI.getFUCentrCus().equals(pessoaFPW.getFUCentrCus())) {
			return false;
		}

		if (usuarioNoIGI.getFUCodCargo() == null) {
			if (pessoaFPW.getFUCodCargo() != null)
				return false;
		} else if (!usuarioNoIGI.getFUCodCargo().equals(pessoaFPW.getFUCodCargo())) {
			return false;
		}

		if (usuarioNoIGI.getFUCodGrpHie() == null) {
			if (pessoaFPW.getFUCodGrpHie() != null)
				return false;
		} else if (!usuarioNoIGI.getFUCodGrpHie().equals(pessoaFPW.getFUCodGrpHie())) {
			return false;
		}

		if (usuarioNoIGI.getFUCodLot() == null) {
			if (pessoaFPW.getFUCodLot() != null)
				return false;
		} else if (!usuarioNoIGI.getFUCodLot().equals(pessoaFPW.getFUCodLot())) {
			return false;
		}

		if (usuarioNoIGI.getFUCodSitu() == null) {
			if (pessoaFPW.getFUCodSitu() != null)
				return false;
		} else if (!usuarioNoIGI.getFUCodSitu().equals(pessoaFPW.getFUCodSitu())) {
			return false;
		}

		if (usuarioNoIGI.getFUDtAdmis() == null) {
			if (pessoaFPW.getFUDtAdmis() != null)
				return false;
		} else if (!usuarioNoIGI.getFUDtAdmis().equals(pessoaFPW.getFUDtAdmis())) {
			return false;
		}

		if (usuarioNoIGI.getFUDtIniSit() == null) {
			if (pessoaFPW.getFUDtIniSit() != null)
				return false;
		} else if (!usuarioNoIGI.getFUDtIniSit().equals(pessoaFPW.getFUDtIniSit())) {
			return false;
		}

		if (usuarioNoIGI.getFUDtNasc() == null) {
			if (pessoaFPW.getFUDtNasc() != null)
				return false;
		} else if (!usuarioNoIGI.getFUDtNasc().equals(pessoaFPW.getFUDtNasc())) {
			return false;
		}

		if (usuarioNoIGI.getFUEmail() == null) {
			if (pessoaFPW.getFUEmail() != null)
				return false;
		} else if (!usuarioNoIGI.getFUEmail().equals(pessoaFPW.getFUEmail())) {
			return false;
		}

		String email2IGI = usuarioNoIGI.getFUEmail2();
		String email2FPW = pessoaFPW.getFUEmail2();

		if (email2IGI == null)
			email2IGI = "";

		if (email2FPW == null)
			email2FPW = "";

		if (!email2IGI.equals(email2FPW))
			return false;

		if (usuarioNoIGI.getFUIndBateP() == null) {
			if (pessoaFPW.getFUIndBateP() != null)
				return false;
		} else if (!usuarioNoIGI.getFUIndBateP().equals(pessoaFPW.getFUIndBateP())) {
			return false;
		}

		if (usuarioNoIGI.getFUIndIRRF() == null) {
			if (pessoaFPW.getFUIndIRRF() != null)
				return false;
		} else if (!usuarioNoIGI.getFUIndIRRF().equals(pessoaFPW.getFUIndIRRF())) {
			return false;
		}

		if (usuarioNoIGI.getFUMatFunc() == null) {
			if (pessoaFPW.getFUMatFunc() != null)
				return false;
		} else if (!usuarioNoIGI.getFUMatFunc().equals(pessoaFPW.getFUMatFunc())) {
			return false;
		}

		if (usuarioNoIGI.getFUNomFunc() == null) {
			if (pessoaFPW.getFUNomFunc() != null)
				return false;
		} else if (!usuarioNoIGI.getFUNomFunc().equals(pessoaFPW.getFUNomFunc())) {
			return false;
		}

		if (usuarioNoIGI.getFUOutDcNum() == null) {
			if (pessoaFPW.getFUOutDcNum() != null)
				return false;
		} else if (!usuarioNoIGI.getFUOutDcNum().equals(pessoaFPW.getFUOutDcNum())) {
			return false;
		}

		String fUOutDcTipIGI = usuarioNoIGI.getFUOutDcTip();
		String fUOutDcTipFPW = pessoaFPW.getFUOutDcTip();

		if (fUOutDcTipIGI == null)
			fUOutDcTipIGI = "";

		if (fUOutDcTipFPW == null)
			fUOutDcTipFPW = "";

		if (!fUOutDcTipIGI.equals(fUOutDcTipFPW))
			return false;

		if (usuarioNoIGI.getFUPessoaID() == null) {
			if (pessoaFPW.getFUPessoaID() != null)
				return false;
		} else if (!usuarioNoIGI.getFUPessoaID().equals(pessoaFPW.getFUPessoaID())) {
			return false;
		}

		if (usuarioNoIGI.getFUSexFunc() == null) {
			if (pessoaFPW.getFUSexFunc() != null)
				return false;
		} else if (!usuarioNoIGI.getFUSexFunc().equals(pessoaFPW.getFUSexFunc())) {
			return false;
		}

		if (usuarioNoIGI.getFUTipoNecEsp() == null) {
			if (pessoaFPW.getFUTipoNecEsp() != null)
				return false;
		} else if (!usuarioNoIGI.getFUTipoNecEsp().equals(pessoaFPW.getFUTipoNecEsp())) {
			return false;
		}

		if (usuarioNoIGI.getGESTOR() == null) {
			if (pessoaFPW.getGESTOR() != null)
				return false;
		} else if (!usuarioNoIGI.getGESTOR().equals(pessoaFPW.getGESTOR())) {
			return false;
		}

		if (usuarioNoIGI.getGHCODGRPHIE() == null) {
			if (pessoaFPW.getGHCODGRPHIE() != null)
				return false;
		} else if (!usuarioNoIGI.getGHCODGRPHIE().equals(pessoaFPW.getGHCODGRPHIE())) {
			return false;
		}

		if (usuarioNoIGI.getGHCODGRPHIERARQUICOSUPERIOR() == null) {
			if (pessoaFPW.getGHCODGRPHIERARQUICOSUPERIOR() != null)
				return false;
		} else if (!usuarioNoIGI.getGHCODGRPHIERARQUICOSUPERIOR().equals(pessoaFPW.getGHCODGRPHIERARQUICOSUPERIOR())) {
			return false;
		}

		if (usuarioNoIGI.getGHDESC() == null) {
			if (pessoaFPW.getGHDESC() != null)
				return false;
		} else if (!usuarioNoIGI.getGHDESC().equals(pessoaFPW.getGHDESC())) {
			return false;
		}

		if (usuarioNoIGI.getGHDESCRESUM() == null) {
			if (pessoaFPW.getGHDESCRESUM() != null)
				return false;
		} else if (!usuarioNoIGI.getGHDESCRESUM().equals(pessoaFPW.getGHDESCRESUM())) {
			return false;
		}

		if (usuarioNoIGI.getGHNUMERONIVELHIERARQUICO() == null) {
			if (pessoaFPW.getGHNUMERONIVELHIERARQUICO() != null)
				return false;
		} else if (!usuarioNoIGI.getGHNUMERONIVELHIERARQUICO().equals(pessoaFPW.getGHNUMERONIVELHIERARQUICO())) {
			return false;
		}

		if (usuarioNoIGI.getLODESCLOT() == null) {
			if (pessoaFPW.getLODESCLOT() != null)
				return false;
		} else if (!usuarioNoIGI.getLODESCLOT().equals(pessoaFPW.getLODESCLOT())) {
			return false;
		}

		if (usuarioNoIGI.getMAT_GESTOR() == null) {
			if (pessoaFPW.getMAT_GESTOR() != null)
				return false;
		} else if (!usuarioNoIGI.getMAT_GESTOR().equals(pessoaFPW.getMAT_GESTOR())) {
			return false;
		}

		if (usuarioNoIGI.getOFCODOCORR() == null) {
			if (pessoaFPW.getOFCODOCORR() != null)
				return false;
		} else if (!usuarioNoIGI.getOFCODOCORR().equals(pessoaFPW.getOFCODOCORR())) {
			return false;
		}

		if (usuarioNoIGI.getOFDtFinOco() == null) {
			if (pessoaFPW.getOFDtFinOco() != null)
				return false;
		} else if (!usuarioNoIGI.getOFDtFinOco().equals(pessoaFPW.getOFDtFinOco())) {
			return false;
		}

		if (usuarioNoIGI.getOFDtIniOco() == null) {
			if (pessoaFPW.getOFDtIniOco() != null)
				return false;
		} else if (!usuarioNoIGI.getOFDtIniOco().equals(pessoaFPW.getOFDtIniOco())) {
			return false;
		}

		if (usuarioNoIGI.getOFMatFunc() == null) {
			if (pessoaFPW.getOFMatFunc() != null)
				return false;
		} else if (!usuarioNoIGI.getOFMatFunc().equals(pessoaFPW.getOFMatFunc())) {
			return false;
		}

		if (usuarioNoIGI.getOFNumDiaAx() == null) {
			if (pessoaFPW.getOFNumDiaAx() != null)
				return false;
		} else if (!usuarioNoIGI.getOFNumDiaAx().equals(pessoaFPW.getOFNumDiaAx())) {
			return false;
		}

		if (usuarioNoIGI.getOFNumDiaFe() == null) {
			if (pessoaFPW.getOFNumDiaFe() != null)
				return false;
		} else if (!usuarioNoIGI.getOFNumDiaFe().equals(pessoaFPW.getOFNumDiaFe())) {
			return false;
		}

		if (usuarioNoIGI.getOPNOMOPER() == null) {
			if (pessoaFPW.getOPNOMOPER() != null)
				return false;
		} else if (!usuarioNoIGI.getOPNOMOPER().equals(pessoaFPW.getOPNOMOPER())) {
			return false;
		}

		if (usuarioNoIGI.getHierDesHie() == null) {
			if (pessoaFPW.getHierDesHie() != null)
				return false;
		} else if (!usuarioNoIGI.getHierDesHie().equals(pessoaFPW.getHierDesHie())) {
			return false;
		}

		if (usuarioNoIGI.getHierCodeHie() == null) {
			if (pessoaFPW.getHierCodeHie() != null)
				return false;
		} else if (usuarioNoIGI.getHierCodeHie() != pessoaFPW.getHierCodeHie()) {
			return false;
		}

		return true;
	}
}
